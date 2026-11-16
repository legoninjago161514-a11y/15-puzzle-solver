<div align="center">

# Пятнашки (15 Puzzle) — Tactile Studio и Гибридный Решатель

**Современная головоломка для Android (`3×3` — `10×10`) на Kotlin и Jetpack Compose**

[![English](https://img.shields.io/badge/lang-English-1E3F8F?style=flat-square)](../README.md)
[![Русский](https://img.shields.io/badge/lang-Русский-C94A29?style=flat-square)](README.ru.md)
[![Español](https://img.shields.io/badge/lang-Español-C98E38?style=flat-square)](README.es.md)
[![Deutsch](https://img.shields.io/badge/lang-Deutsch-2C313A?style=flat-square)](README.de.md)
[![Français](https://img.shields.io/badge/lang-Français-435E49?style=flat-square)](README.fr.md)

<br/>

![Kotlin](https://img.shields.io/badge/Kotlin-Android-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Min SDK](https://img.shields.io/badge/Min%20SDK-24%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-black?style=for-the-badge)

</div>

---

## Содержание

- [О проекте](#о-проекте)
- [Основные возможности](#основные-возможности)
- [Архитектура решателя](#архитектура-решателя)
- [Структура проекта и ссылки на файлы](#структура-проекта-и-ссылки-на-файлы)
- [Сборка и тестирование](#сборка-и-тестирование)
- [Приватность и лицензия](#приватность-и-лицензия)
- [Технологии и источники](#технологии-и-источники)

---

## О проекте

**«Пятнашки» (15 Puzzle)** объединяет тактильный дизайн в стиле студийной аппаратуры (матовые керамические плитки с объемным профилем нажатия и авторскими палитрами) с быстрым гибридным решателем. Приложение находит кратчайший путь на полях до `4×4` и мгновенно собирает большие сетки до `10×10` — полностью офлайн, без рекламы и слежки.

---

## Основные возможности

### Игровой процесс и размеры сетки
- **Сетки от `3×3` до `10×10`**: быстрые пресеты (`3×3`, `4×4`, `5×5`, `6×6`, `8×8`, `10×10`) и любые прямоугольные поля (`строки × столбцы`).
- **Удобное управление**: сдвиг касанием, плавные свайпы и опциональный **сдвиг целого ряда или столбца**.
- **Телеметрия и рекорды**: счётчик ходов, таймер, шкала прогресса, история отмены ходов и личные рекорды для каждого размера сетки в [`SettingsRepository.kt`](../app/src/main/java/com/example/data/SettingsRepository.kt).

### Гибридный решатель и плеер
- **Оптимальный алгоритм `IDA*`** ([`PuzzleSolver.kt`](../app/src/main/java/com/example/solver/PuzzleSolver.kt)): гарантирует минимальное число ходов на полях до `4×4` с эвристиками **Манхэттен** и **Манхэттен + линейные конфликты**.
- **Конструктивный послойный солвер** ([`ConstructiveSolver.kt`](../app/src/main/java/com/example/solver/ConstructiveSolver.kt)): послойная редукция строк и столбцов + финишер `3×3` + оптимизатор ходов для полей до `10×10`.
- **Плеер решения** ([`GameHudAndControls.kt`](../app/src/main/java/com/example/ui/components/GameHudAndControls.kt)): автовоспроизведение (`1×`–`20×`), пошаговый просмотр вперёд/назад и перемотка в конец.
- **Тест производительности**: прогон 4 эталонных раскладок `3×3` с замером времени, числа ходов и оценённых узлов.

### Конструктор «Своя доска» ([`SetupScreen.kt`](../app/src/main/java/com/example/ui/screens/SetupScreen.kt))
- **Три способа ввода**: палитра плиток в одно касание, встроенная цифровая клавиатура и режим перетаскивания из лотка (Drag & Drop).
- **Умная проверка**: дозаполнение пустых клеток, импорт/экспорт через буфер обмена и контроль чётности перестановки для 100% решаемости.

### Оформление и языки ([`SettingsScreen.kt`](../app/src/main/java/com/example/ui/screens/SettingsScreen.kt))
- **Темы и палитры**: Авто, Светлая, Тёмная, OLED (`#000000`), Material You (Android 12+) и 6 студийных палитр (**Терра, Швейцария, Кобальт, Шалфей, Орех, Графит**) в [`Theme.kt`](../app/src/main/java/com/example/ui/theme/Theme.kt).
- **Стили плиток и обозначений**: Керамика, Баухаус, Дуотон, Дерево, Графит, Минерал, Статус; арабские, римские цифры (`I, II`), координаты (`X,Y`) и `HEX` ([`SettingsState.kt`](../app/src/main/java/com/example/data/SettingsState.kt)).
- **6 языков интерфейса**: Авто, Русский, English, Español, Deutsch, Français ([`AppStrings.kt`](../app/src/main/java/com/example/i18n/AppStrings.kt) и [`locales_config.xml`](../app/src/main/res/xml/locales_config.xml)).

---

## Архитектура решателя

| Размер поля | Режим по умолчанию (`HYBRID`) | Эвристика / метод | Результат |
| :--- | :--- | :--- | :--- |
| До `4×4` | Поиск `IDA*` | Манхэттен + линейные конфликты | Кратчайший оптимальный путь |
| `5×5` – `10×10` | Послойная редукция | Сборка строк/столбцов + `3×3` + сжатие ходов | Быстрое детерминированное решение |
| `2×N` / `N×2` | Ограниченный BFS | Поиск кратчайшего пути по состояниям | Корректное короткое решение |

---

## Структура проекта и ссылки на файлы

```text
.
├── README.md                                        # Главный README (English)
├── LICENSE                                          # Лицензия MIT
├── build.gradle.kts                                 # Корневая конфигурация Gradle
├── settings.gradle.kts                              # Подключение модулей и репозиториев
├── docs/                                            # Документация на разных языках и политика
│   ├── README.ru.md                                 # Русская версия README
│   ├── README.es.md                                 # Испанская версия README
│   ├── README.de.md                                 # Немецкая версия README
│   ├── README.fr.md                                 # Французская версия README
│   └── PRIVACY_POLICY.md                            # Политика конфиденциальности
├── gradle/
│   └── libs.versions.toml                           # Каталог версий зависимостей
└── app/
    ├── build.gradle.kts                             # Зависимости и конфигурация подписи
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml                  # Манифест и конфиг локалей
        │   ├── res/                                 # Иконки, шрифты, цвета и XML-ресурсы
        │   └── java/com/example/
        │       ├── MainActivity.kt                  # Точка входа, навигация и диалог выхода
        │       ├── data/                            # Модели настроек, палитры и DataStore
        │       │   ├── SettingsState.kt
        │       │   └── SettingsRepository.kt
        │       ├── i18n/                            # Система мультиязычности
        │       │   └── AppStrings.kt
        │       ├── solver/                          # Алгоритмы решения головоломки
        │       │   ├── PuzzleSolver.kt
        │       │   └── ConstructiveSolver.kt
        │       ├── viewmodel/                       # Бизнес-логика и состояние игры
        │       │   └── BoardViewModel.kt
        │       └── ui/
        │           ├── components/                  # Игровое поле, HUD и диалоги
        │           │   ├── PuzzleBoard.kt
        │           │   └── GameHudAndControls.kt
        │           ├── screens/                     # Экран настроек и «Своя доска»
        │           │   ├── SetupScreen.kt
        │           │   └── SettingsScreen.kt
        │           └── theme/                       # Тема Material 3 и типографика
        │               ├── Color.kt
        │               ├── Theme.kt
        │               └── Type.kt
        └── test/java/com/example/                   # Юнит-, Robolectric- и скриншот-тесты
            ├── ExampleUnitTest.kt
            ├── ExampleRobolectricTest.kt
            └── GreetingScreenshotTest.kt
```

### Быстрые ссылки на исходники

| Слой | Файл | Назначение |
| :--- | :--- | :--- |
| **Точка входа** | [`MainActivity.kt`](../app/src/main/java/com/example/MainActivity.kt) | Edge-to-edge интерфейс, навигация между экранами, верхняя панель, подтверждение выхода |
| **ViewModel** | [`BoardViewModel.kt`](../app/src/main/java/com/example/viewmodel/BoardViewModel.kt) | Логика ходов, таймер, история отмены, рекорды, буфер обмена, запуск решателей и бенчмарка |
| **Решатель (`IDA*`)** | [`PuzzleSolver.kt`](../app/src/main/java/com/example/solver/PuzzleSolver.kt) | Алгоритм IDA*, манхэттенское расстояние и линейные конфликты |
| **Решатель (`10×10`)** | [`ConstructiveSolver.kt`](../app/src/main/java/com/example/solver/ConstructiveSolver.kt) | Послойный конструктивный алгоритм и оптимизация последовательности ходов |
| **Игровое поле** | [`PuzzleBoard.kt`](../app/src/main/java/com/example/ui/components/PuzzleBoard.kt) | Плитки-кейкапы с физикой пружин, свайпами и анимацией конфетти |
| **HUD и диалоги** | [`GameHudAndControls.kt`](../app/src/main/java/com/example/ui/components/GameHudAndControls.kt) | Статистика, плеер решения, выбор сетки, модальное окно победы и справка |
| **Своя доска** | [`SetupScreen.kt`](../app/src/main/java/com/example/ui/screens/SetupScreen.kt) | Быстрая палитра плиток, цифровая клавиатура, Drag & Drop лоток и контроль решаемости |
| **Настройки** | [`SettingsScreen.kt`](../app/src/main/java/com/example/ui/screens/SettingsScreen.kt) | Живой предпросмотр `3×3`, выбор языка, палитр, физики и параметров решателя |
| **Данные и тема** | [`SettingsRepository.kt`](../app/src/main/java/com/example/data/SettingsRepository.kt) · [`SettingsState.kt`](../app/src/main/java/com/example/data/SettingsState.kt) · [`Theme.kt`](../app/src/main/java/com/example/ui/theme/Theme.kt) | Сохранение в DataStore, цветовые схемы и шрифты ([`Type.kt`](../app/src/main/java/com/example/ui/theme/Type.kt)) |
| **Локализация** | [`AppStrings.kt`](../app/src/main/java/com/example/i18n/AppStrings.kt) · [`locales_config.xml`](../app/src/main/res/xml/locales_config.xml) | Мгновенное переключение между `RU`, `EN`, `ES`, `DE`, `FR` |
| **Сборка и CI** | [`app/build.gradle.kts`](../app/build.gradle.kts) · [`libs.versions.toml`](../gradle/libs.versions.toml) · [`release-apk.yml`](../.github/workflows/release-apk.yml) | Зависимости, подпись и автосборка APK для GitHub Releases |
| **Тесты** | [`ExampleUnitTest.kt`](../app/src/test/java/com/example/ExampleUnitTest.kt) · [`ExampleRobolectricTest.kt`](../app/src/test/java/com/example/ExampleRobolectricTest.kt) | Тесты решателя и Robolectric |

---

## Сборка и тестирование

1. **Клонируйте репозиторий** и откройте в **Android Studio** (JDK 21 / Android SDK 36) или используйте **Gradle 9.3.1**:
   ```bash
   git clone <url-репозитория>
   cd 15-puzzle-android
   ```
2. **Запуск юнит- и Robolectric-тестов**:
   ```bash
   gradle :app:testDebugUnitTest
   ```
3. **Сборка Debug APK**:
   ```bash
   gradle :app:assembleDebug
   ```
4. **Автосборка APK в GitHub Releases (без Android Studio)**:
   Откройте **Actions → Build & Release APK → Run workflow** в репозитории (или отправьте тег `v*`) — GitHub сам соберёт, протестирует и выложит готовый `.apk` в раздел **Releases** через [`release-apk.yml`](../.github/workflows/release-apk.yml).
5. **Релизная сборка для магазинов (AAB)**:
   Передайте параметры ключа подписи через переменные окружения (`KEYSTORE_PATH`, `STORE_PASSWORD`, `KEY_PASSWORD`) или положите `my-upload-key.jks` в корень проекта (уже добавлен в [`.gitignore`](../.gitignore)):
   ```bash
   gradle :app:bundleRelease
   ```

---

## Приватность и лицензия

- **Политика конфиденциальности**: без рекламы, без трекинга, 100% локальное хранение — [docs/PRIVACY_POLICY.md](PRIVACY_POLICY.md).
- **Лицензия**: распространяется по лицензии MIT — [LICENSE](../LICENSE).

---

## Технологии и источники

- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Jetpack Compose](https://developer.android.com/compose) и [Material Design 3](https://m3.material.io/)
- [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [Richard E. Korf (1985) — *Depth-First Iterative-Deepening: An Optimal Admissible Tree Search*](https://www.sciencedirect.com/science/article/pii/0004370285900840)
- [Robolectric](https://robolectric.org/) и [Roborazzi](https://github.com/takahirom/roborazzi)
