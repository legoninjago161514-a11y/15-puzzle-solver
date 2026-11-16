<div align="center">

# SlideGrid — Тактильные пятнашки и Гибридный Решатель

**Современная головоломка для Android (`3×3` — `10×10`) на Kotlin и Jetpack Compose**

[![English](https://img.shields.io/badge/lang-English-1E3F8F?style=flat-square)](../README.md)
[![Русский](https://img.shields.io/badge/lang-Русский-C94A29?style=flat-square)](README.ru.md)
[![Español](https://img.shields.io/badge/lang-Español-C98E38?style=flat-square)](README.es.md)
[![Deutsch](https://img.shields.io/badge/lang-Deutsch-2C313A?style=flat-square)](README.de.md)
[![Français](https://img.shields.io/badge/lang-Français-435E49?style=flat-square)](README.fr.md)

<br/>

[![Latest Release](https://img.shields.io/github/v/release/legoninjago161514-a11y/15-puzzle-solver?style=for-the-badge&color=C94A29&label=Release)](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases/latest)
[![Download APK](https://img.shields.io/badge/Download-APK-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases/latest)
[![Kotlin](https://img.shields.io/badge/Kotlin-Android-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/compose)
[![License: MIT](https://img.shields.io/badge/License-MIT-black?style=for-the-badge)](../LICENSE)

[**Скачать APK (Последний релиз)**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases/latest) · [**Все релизы**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases) · [**Сборки CI (Actions)**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/actions) · [**Сообщить об ошибке**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/issues)

</div>

---

## Содержание

- [О проекте](#о-проекте)
- [Скачивание и установка (APK)](#скачивание-и-установка-apk)
- [Подробный разбор возможностей](#подробный-разбор-возможностей)
- [Архитектура гибридного решателя](#архитектура-гибридного-решателя)
- [Структура проекта и ссылки на файлы](#структура-проекта-и-ссылки-на-файлы)
- [Сборка, тесты и авторелизы CI](#сборка-тесты-и-авторелизы-ci)
- [Приватность и лицензия](#приватность-и-лицензия)
- [Технологии и источники](#технологии-и-источники)

---

## О проекте

**SlideGrid** — это офлайн-головоломка для Android, объединяющая тактильный промышленный дизайн (матовые объёмные плитки-кейкапы, физику пружин и студийные палитры) с быстрым двухдвижковым решателем. Приложение гарантированно находит кратчайший путь на полях до `4×4` через **`IDA*`** и детерминированно собирает произвольные сетки до **`10×10`** за миллисекунды — полностью без интернета, рекламы и трекеров.

---

## Скачивание и установка (APK)

Готовые подписанные `.apk`-файлы (`slidegrid-<тег>.apk`) автоматически публикуются в разделе [**Releases**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases) через сценарий [`.github/workflows/release-apk.yml`](../.github/workflows/release-apk.yml).

1. Откройте страницу [**Последнего релиза**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases/latest).
2. В блоке **Assets** скачайте файл **`slidegrid-<версия>.apk`** (`Android 7.0+` / min SDK `24`, target SDK `36`).
3. Откройте файл на Android-устройстве и при запросе разрешите установку из браузера или проводника.

---

## Подробный разбор возможностей

### 1. Игровой процесс, жесты и телеметрия
- **Размеры сетки (`3×3` – `10×10`)**: быстрые квадратные пресеты (`3×3`, `4×4`, `5×5`, `6×6`, `8×8`, `10×10`) и произвольные прямоугольные поля (`строки × столбцы`) под управлением [`BoardViewModel.kt`](../app/src/main/java/com/example/viewmodel/BoardViewModel.kt) и диалога в [`GameHudAndControls.kt`](../app/src/main/java/com/example/ui/components/GameHudAndControls.kt).
- **Тактильное управление** ([`PuzzleBoard.kt`](../app/src/main/java/com/example/ui/components/PuzzleBoard.kt)): сдвиг нажатием, плавные свайпы и опциональный **сдвиг целого ряда или столбца** одним жестом.
- **Физика, звук и вибрация**: 3 профиля пружин (*Быстро*, *Упруго*, *Мягко*), индикатор правильного места, звуковой щелчок и 4 режима тактильного отклика.
- **HUD, отмена ходов и рекорды**: счётчик ходов, таймер, шкала прогресса, стек отмены, конфетти при победе и личные рекорды для каждой сетки в [`SettingsRepository.kt`](../app/src/main/java/com/example/data/SettingsRepository.kt).

### 2. Гибридный решатель и плеер решения
- **Оптимальный движок `IDA*`** ([`PuzzleSolver.kt`](../app/src/main/java/com/example/solver/PuzzleSolver.kt)): итеративное углубление с эвристиками **Манхэттен** и **Манхэттен + линейные конфликты** гарантирует минимум ходов до `4×4`.
- **Конструктивный солвер** ([`ConstructiveSolver.kt`](../app/src/main/java/com/example/solver/ConstructiveSolver.kt)): послойная редукция строк/столбцов, финишер `3×3` и сжатие лишних ходов для полей до `10×10`.
- **Плеер решения и бенчмарк** ([`GameHudAndControls.kt`](../app/src/main/java/com/example/ui/components/GameHudAndControls.kt)): автовоспроизведение (`1×`–`20×`), пошаговый просмотр назад/вперёд, перемотка в конец и тест производительности на 4 эталонных раскладках `3×3`.

### 3. Конструктор «Своя доска» ([`SetupScreen.kt`](../app/src/main/java/com/example/ui/screens/SetupScreen.kt))
- **Три режима ввода**:
  1. **Палитра плиток** — быстрое заполнение в одно касание.
  2. **Цифровая клавиатура** — встроенный нумпад для двузначных чисел до `99`.
  3. **Лоток Drag & Drop** — перетаскивание со случайным или упорядоченным набором.
- **Умная проверка**: авто-дозаполнение пустых клеток, импорт/экспорт через буфер обмена и контроль чётности перестановки для 100% решаемости.

### 4. Темы, оформление плиток и нативная заставка
- **Темы и палитры** ([`Theme.kt`](../app/src/main/java/com/example/ui/theme/Theme.kt), [`Color.kt`](../app/src/main/java/com/example/ui/theme/Color.kt)): *Авто*, *Свет*, *Тень*, чистый *OLED* (`#000000`), Material You (`Android 12+`) и 6 авторских палитр (**Терра, Швейцария, Кобальт, Шалфей, Орех, Графит**).
- **7 материалов плиток и 4 формата обозначений** ([`SettingsState.kt`](../app/src/main/java/com/example/data/SettingsState.kt), [`SettingsScreen.kt`](../app/src/main/java/com/example/ui/screens/SettingsScreen.kt)): *Керамика, Баухаус, Дуотон, Дерево, Графит, Минерал, Статус*; арабские, римские (`I, II`), координаты (`X,Y`) и `HEX` с живым предпросмотром `3×3`.
- **Локальная типографика**: встроенные шрифты [`Plus Jakarta Sans`](../app/src/main/res/font/plus_jakarta_sans.ttf) и [`JetBrains Mono`](../app/src/main/res/font/jetbrains_mono.ttf) в [`Type.kt`](../app/src/main/java/com/example/ui/theme/Type.kt).
- **Анимированный старт `Android 12+`**: системный SplashScreen (`core-splashscreen`) с векторной анимацией [`avd_splash_tiles.xml`](../app/src/main/res/drawable/avd_splash_tiles.xml), [`ic_splash_tiles_vector.xml`](../app/src/main/res/drawable/ic_splash_tiles_vector.xml) и адаптивными темами в [`themes.xml`](../app/src/main/res/values/themes.xml) — плитки сдвигаются с 0-й миллисекунды без чёрной рамки и лишних задержек.

### 5. Мультиязычный интерфейс (6 вариантов)
- Мгновенное переключение без перезапуска: **Авто**, **Русский**, **English**, **Español**, **Deutsch**, **Français** через [`AppStrings.kt`](../app/src/main/java/com/example/i18n/AppStrings.kt) и [`locales_config.xml`](../app/src/main/res/xml/locales_config.xml).

---

## Архитектура гибридного решателя

| Размер поля / режим | Активный алгоритм | Эвристика / метод | Результат | Исходник |
| :--- | :--- | :--- | :--- | :--- |
| До `4×4` (`HYBRID` / `OPTIMAL_IDA`) | Поиск `IDA*` | Манхэттен + линейные конфликты | Кратчайший оптимальный путь | [`PuzzleSolver.kt`](../app/src/main/java/com/example/solver/PuzzleSolver.kt) |
| `5×5` – `10×10` (или `FAST_REDUCTION`) | Послойная редукция | Сборка строк/столбцов + `3×3` + сжатие ходов | Мгновенное детерминированное решение | [`ConstructiveSolver.kt`](../app/src/main/java/com/example/solver/ConstructiveSolver.kt) |
| `2×N` / `N×2` | Ограниченный BFS | Поиск кратчайшего пути по состояниям | Компактное валидное решение | [`ConstructiveSolver.kt`](../app/src/main/java/com/example/solver/ConstructiveSolver.kt) |

---

## Структура проекта и ссылки на файлы

```text
.
├── README.md                                        # Главный README (English)
├── LICENSE                                          # Лицензия MIT
├── build.gradle.kts                                 # Корневая конфигурация Gradle
├── settings.gradle.kts                              # Настройка модулей и репозиториев
├── .gitignore                                       # Исключения Git
├── .github/workflows/
│   └── release-apk.yml                              # Автотесты, сборка APK и публикация в Releases
├── docs/                                            # Документация на языках и политика
│   ├── README.ru.md                                 # Русская версия README
│   ├── README.es.md                                 # Испанская версия README
│   ├── README.de.md                                 # Немецкая версия README
│   ├── README.fr.md                                 # Французская версия README
│   └── PRIVACY_POLICY.md                            # Политика конфиденциальности
├── gradle/
│   └── libs.versions.toml                           # Каталог версий зависимостей
└── app/
    ├── build.gradle.kts                             # Зависимости, SDK и конфигурация подписи
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml                  # Манифест, тема заставки и конфиг локалей
        │   ├── res/
        │   │   ├── animator/                        # Аниматоры векторного сплэша
        │   │   ├── drawable/                        # AVD-заставка и векторные иконки
        │   │   ├── font/                            # Plus Jakarta Sans и JetBrains Mono
        │   │   ├── values/ & values-night/          # Темы, цвета и базовые строки
        │   │   ├── values-{ru,es,de,fr}/            # Локализованные ресурсы
        │   │   └── xml/                             # locales_config.xml и правила бэкапа
        │   └── java/com/example/
        │       ├── MainActivity.kt                  # SplashScreen API, навигация и диалог выхода
        │       ├── data/                            # Настройки, стили плиток и DataStore
        │       │   ├── SettingsState.kt
        │       │   └── SettingsRepository.kt
        │       ├── i18n/                            # Словарь локализаций на 5 языков
        │       │   └── AppStrings.kt
        │       ├── solver/                          # Алгоритмы решения
        │       │   ├── PuzzleSolver.kt
        │       │   └── ConstructiveSolver.kt
        │       ├── viewmodel/                       # Состояние доски, таймер, рекорды и солвер
        │       │   └── BoardViewModel.kt
        │       └── ui/
        │           ├── components/                  # Игровое поле, HUD, диалоги и плеер
        │           │   ├── PuzzleBoard.kt
        │           │   └── GameHudAndControls.kt
        │           ├── screens/                     # Конструктор доски и настройки
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

### Быстрые ссылки на исходники и ресурсы

| Слой | Файл(ы) | Назначение |
| :--- | :--- | :--- |
| **Точка входа** | [`MainActivity.kt`](../app/src/main/java/com/example/MainActivity.kt) | Переход `SplashScreen`, edge-to-edge, навигация экранов, защита от случайного выхода |
| **ViewModel** | [`BoardViewModel.kt`](../app/src/main/java/com/example/viewmodel/BoardViewModel.kt) | Ходы, таймер, стек отмены, рекорды, буфер обмена, запуск солвера и бенчмарка |
| **Решатели** | [`PuzzleSolver.kt`](../app/src/main/java/com/example/solver/PuzzleSolver.kt) · [`ConstructiveSolver.kt`](../app/src/main/java/com/example/solver/ConstructiveSolver.kt) | Оптимальный `IDA*`, послойная конструктивная редукция, сжатие ходов |
| **Компоненты UI** | [`PuzzleBoard.kt`](../app/src/main/java/com/example/ui/components/PuzzleBoard.kt) · [`GameHudAndControls.kt`](../app/src/main/java/com/example/ui/components/GameHudAndControls.kt) | 3D-плитки, пружины, свайпы, HUD, плеер решения, модальное окно победы и справка |
| **Экраны** | [`SetupScreen.kt`](../app/src/main/java/com/example/ui/screens/SetupScreen.kt) · [`SettingsScreen.kt`](../app/src/main/java/com/example/ui/screens/SettingsScreen.kt) | Редактор доски, живой предпросмотр `3×3`, палитры, физика и параметры решателя |
| **Данные и тема** | [`SettingsRepository.kt`](../app/src/main/java/com/example/data/SettingsRepository.kt) · [`SettingsState.kt`](../app/src/main/java/com/example/data/SettingsState.kt) · [`Theme.kt`](../app/src/main/java/com/example/ui/theme/Theme.kt) | Jetpack DataStore, палитры, [`Color.kt`](../app/src/main/java/com/example/ui/theme/Color.kt) и [`Type.kt`](../app/src/main/java/com/example/ui/theme/Type.kt) |
| **Сплэш и ресурсы** | [`avd_splash_tiles.xml`](../app/src/main/res/drawable/avd_splash_tiles.xml) · [`themes.xml`](../app/src/main/res/values/themes.xml) · [`AndroidManifest.xml`](../app/src/main/AndroidManifest.xml) | Векторная анимация запуска, адаптивные темы сплэша и манифест |
| **Локализация** | [`AppStrings.kt`](../app/src/main/java/com/example/i18n/AppStrings.kt) · [`locales_config.xml`](../app/src/main/res/xml/locales_config.xml) · [`strings.xml`](../app/src/main/res/values/strings.xml) | Мгновенный перевод интерфейса (`EN`, `RU`, `ES`, `DE`, `FR`) |
| **Сборка и CI** | [`build.gradle.kts`](../build.gradle.kts) · [`app/build.gradle.kts`](../app/build.gradle.kts) · [`libs.versions.toml`](../gradle/libs.versions.toml) · [`release-apk.yml`](../.github/workflows/release-apk.yml) | Зависимости, конфигурация подписи и автосборка APK в GitHub Releases |
| **Тесты** | [`ExampleUnitTest.kt`](../app/src/test/java/com/example/ExampleUnitTest.kt) · [`ExampleRobolectricTest.kt`](../app/src/test/java/com/example/ExampleRobolectricTest.kt) · [`GreetingScreenshotTest.kt`](../app/src/test/java/com/example/GreetingScreenshotTest.kt) | Тесты решателя, проверки Robolectric и Roborazzi-снапшоты |

---

## Сборка, тесты и авторелизы CI

1. **Клонируйте репозиторий** и откройте в **Android Studio** (JDK 21 / Android SDK 36) или используйте **Gradle 9.3.1**:
   ```bash
   git clone https://github.com/legoninjago161514-a11y/15-puzzle-solver.git
   cd 15-puzzle-solver
   ```
2. **Запуск всех юнит-, Robolectric- и скриншот-тестов**:
   ```bash
   gradle :app:testDebugUnitTest
   ```
3. **Сборка Debug APK**:
   ```bash
   gradle :app:assembleDebug
   ```
4. **Автосборка APK в GitHub Releases**:
   Откройте [**Actions → Build & Release APK**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/actions) → **Run workflow** (или отправьте тег `v*`). Пайплайн [`.github/workflows/release-apk.yml`](../.github/workflows/release-apk.yml) сам прогонит тесты, подпишет сборку и прикрепит `slidegrid-<тег>.apk` в [**Releases**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases).
5. **Своя релизная подпись (APK / AAB)**:
   Передайте `KEYSTORE_PATH`, `STORE_PASSWORD` и `KEY_PASSWORD` через переменные окружения или GitHub Secrets (ключи `.jks` исключены в [`.gitignore`](../.gitignore)):
   ```bash
   gradle :app:assembleRelease
   gradle :app:bundleRelease
   ```

---

## Приватность и лицензия

- **Политика конфиденциальности**: без рекламы, без телеметрии, 100% локальное хранение — [`docs/PRIVACY_POLICY.md`](PRIVACY_POLICY.md).
- **Лицензия**: распространяется по лицензии MIT — [`LICENSE`](../LICENSE).

---

## Технологии и источники

- [**Релизы SlideGrid**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases) и [**Репозиторий**](https://github.com/legoninjago161514-a11y/15-puzzle-solver)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Jetpack Compose](https://developer.android.com/compose) и [Material Design 3](https://m3.material.io/)
- [Android SplashScreen API (`core-splashscreen`)](https://developer.android.com/develop/ui/views/launch/splash-screen)
- [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [Richard E. Korf (1985) — *Depth-First Iterative-Deepening: An Optimal Admissible Tree Search*](https://www.sciencedirect.com/science/article/pii/0004370285900840)
- [Robolectric](https://robolectric.org/) и [Roborazzi](https://github.com/takahirom/roborazzi)
