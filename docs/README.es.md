<div align="center">

# SlideGrid — Estudio Táctil y Solucionador Híbrido

**Un rompecabezas deslizante moderno (`3×3` a `10×10`) para Android en Kotlin y Jetpack Compose**

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

[**Descargar último APK**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases/latest) · [**Todos los lanzamientos**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases) · [**CI (Actions)**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/actions) · [**Reportar un error**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/issues)

</div>

---

## Tabla de Contenidos

- [Resumen](#resumen)
- [Descarga e Instalación (APK)](#descarga-e-instalación-apk)
- [Características Detalladas](#características-detalladas)
- [Arquitectura del Solucionador Híbrido](#arquitectura-del-solucionador-híbrido)
- [Estructura del Proyecto e Índice de Archivos](#estructura-del-proyecto-e-índice-de-archivos)
- [Compilación, Pruebas y Releases CI](#compilación-pruebas-y-releases-ci)
- [Privacidad y Licencia](#privacidad-y-licencia)
- [Referencias y Stack Tecnológico](#referencias-y-stack-tecnológico)

---

## Resumen

**SlideGrid** es un rompecabezas deslizante sin conexión para Android que combina diseño industrial táctil (fichas cerámicas 3D con relieve físico, físicas de resorte y paletas cuidadas) con un motor algorítmico dual: encuentra la ruta óptima más corta hasta `4×4` mediante **`IDA*`** y resuelve de forma determinista cuadrículas de hasta **`10×10`** en milisegundos — sin anuncios ni rastreadores.

---

## Descarga e Instalación (APK)

Los APK firmados (`slidegrid-<tag>.apk`) se generan automáticamente en la sección [**Releases**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases) mediante [`.github/workflows/release-apk.yml`](../.github/workflows/release-apk.yml).

1. Abre la página del [**Último Lanzamiento**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases/latest).
2. En **Assets**, descarga **`slidegrid-<versión>.apk`** (`Android 7.0+` / min SDK `24`, target SDK `36`).
3. Abre el archivo en tu dispositivo Android y permite la instalación si el sistema lo solicita.

---

## Características Detalladas

### 1. Jugabilidad, Gestos y Telemetría
- **Cuadrículas de `3×3` a `10×10`**: tamaños predefinidos (`3×3`, `4×4`, `5×5`, `6×6`, `8×8`, `10×10`) y dimensiones rectangulares personalizadas (`filas × columnas`) gestionadas por [`BoardViewModel.kt`](../app/src/main/java/com/example/viewmodel/BoardViewModel.kt) y [`GameHudAndControls.kt`](../app/src/main/java/com/example/ui/components/GameHudAndControls.kt).
- **Control Táctil Fluido** ([`PuzzleBoard.kt`](../app/src/main/java/com/example/ui/components/PuzzleBoard.kt)): toque para deslizar, arrastre continuo y desplazamiento opcional de filas o columnas completas en un solo gesto.
- **Física, Sonido y Háptica**: 3 perfiles de resorte (*Rápido*, *Elástico*, *Suave*), marcador de posición correcta, clic acústico y 4 niveles hápticos.
- **HUD, Deshacer y Récords**: contador de movimientos, temporizador, barra de progreso, historial para deshacer, confeti de victoria y récords guardados localmente en [`SettingsRepository.kt`](../app/src/main/java/com/example/data/SettingsRepository.kt).

### 2. Solucionador Híbrido y Reproductor
- **Motor Óptimo `IDA*`** ([`PuzzleSolver.kt`](../app/src/main/java/com/example/solver/PuzzleSolver.kt)): búsqueda con heurísticas **Manhattan** y **Manhattan + Conflictos Lineales** hasta `4×4`.
- **Reducción Constructiva** ([`ConstructiveSolver.kt`](../app/src/main/java/com/example/solver/ConstructiveSolver.kt)): reducción por capas de filas y columnas más cierre `3×3` y compresión de movimientos hasta `10×10`.
- **Reproductor y Benchmark** ([`GameHudAndControls.kt`](../app/src/main/java/com/example/ui/components/GameHudAndControls.kt)): reproducción automática (`1×`–`20×`), paso anterior/siguiente, salto al final y prueba integrada `3×3`.

### 3. Editor de Tablero Personalizado ([`SetupScreen.kt`](../app/src/main/java/com/example/ui/screens/SetupScreen.kt))
- **Tres Modos de Entrada**: paleta rápida de un toque, teclado numérico integrado y bandeja de arrastrar y soltar (aleatorio u ordenado).
- **Validación**: autocompletado de celdas restantes, importación/exportación desde portapapeles y verificación automática de paridad para garantizar solución 100%.

### 4. Temas Studio, Acabados y Arranque Animado Nativo
- **Temas y Paletas** ([`Theme.kt`](../app/src/main/java/com/example/ui/theme/Theme.kt), [`Color.kt`](../app/src/main/java/com/example/ui/theme/Color.kt)): *Sistema*, *Claro*, *Oscuro*, *OLED* (`#000000`), Material You (`Android 12+`) y 6 paletas (**Terra, Swiss, Cobalt, Sage, Walnut, Slate**).
- **7 Acabados y 4 Notaciones** ([`SettingsState.kt`](../app/src/main/java/com/example/data/SettingsState.kt), [`SettingsScreen.kt`](../app/src/main/java/com/example/ui/screens/SettingsScreen.kt)): *Cerámica, Bauhaus, Dúo, Madera, Grafito, Mineral, Estado*; números *Arábigos*, *Romanos*, *Coordenadas* y *Hexadecimales* con vista previa `3×3` en vivo.
- **Tipografía Integrada**: [`Plus Jakarta Sans`](../app/src/main/res/font/plus_jakarta_sans.ttf) y [`JetBrains Mono`](../app/src/main/res/font/jetbrains_mono.ttf) en [`Type.kt`](../app/src/main/java/com/example/ui/theme/Type.kt).
- **SplashScreen Animado (`Android 12+`)**: API `core-splashscreen` con [`AnimatedVectorDrawable`](../app/src/main/res/drawable/avd_splash_tiles.xml), [`ic_splash_tiles_vector.xml`](../app/src/main/res/drawable/ic_splash_tiles_vector.xml) y fondos adaptativos en [`themes.xml`](../app/src/main/res/values/themes.xml).

### 5. Localización en Vivo (6 opciones)
- Cambio instantáneo entre **Auto**, **English**, **Русский**, **Español**, **Deutsch** y **Français** en [`AppStrings.kt`](../app/src/main/java/com/example/i18n/AppStrings.kt) y [`locales_config.xml`](../app/src/main/res/xml/locales_config.xml).

---

## Arquitectura del Solucionador Híbrido

| Tamaño / Modo | Algoritmo | Heurística / Método | Resultado | Código |
| :--- | :--- | :--- | :--- | :--- |
| Hasta `4×4` (`HYBRID` / `OPTIMAL_IDA`) | Búsqueda `IDA*` | Manhattan + Conflictos Lineales | Ruta más corta óptima | [`PuzzleSolver.kt`](../app/src/main/java/com/example/solver/PuzzleSolver.kt) |
| `5×5` – `10×10` (o `FAST_REDUCTION`) | Reducción por Capas | Filas/Columnas + cierre `3×3` + optimizador | Solución determinista instantánea | [`ConstructiveSolver.kt`](../app/src/main/java/com/example/solver/ConstructiveSolver.kt) |
| `2×N` / `N×2` | BFS Acotado | Búsqueda en anchura | Solución compacta válida | [`ConstructiveSolver.kt`](../app/src/main/java/com/example/solver/ConstructiveSolver.kt) |

---

## Estructura del Proyecto e Índice de Archivos

| Capa | Archivo(s) | Responsabilidad |
| :--- | :--- | :--- |
| **Entrada** | [`MainActivity.kt`](../app/src/main/java/com/example/MainActivity.kt) | Transición `SplashScreen`, edge-to-edge, navegación, confirmación de salida |
| **ViewModel** | [`BoardViewModel.kt`](../app/src/main/java/com/example/viewmodel/BoardViewModel.kt) | Movimientos, reloj, deshacer, récords, portapapeles, solucionador y benchmark |
| **Solucionadores** | [`PuzzleSolver.kt`](../app/src/main/java/com/example/solver/PuzzleSolver.kt) · [`ConstructiveSolver.kt`](../app/src/main/java/com/example/solver/ConstructiveSolver.kt) | `IDA*`, reducción constructiva por capas y compresión de pasos |
| **Componentes UI** | [`PuzzleBoard.kt`](../app/src/main/java/com/example/ui/components/PuzzleBoard.kt) · [`GameHudAndControls.kt`](../app/src/main/java/com/example/ui/components/GameHudAndControls.kt) | Fichas 3D, animaciones de resorte, gestos, HUD, reproductor y diálogos |
| **Pantallas** | [`SetupScreen.kt`](../app/src/main/java/com/example/ui/screens/SetupScreen.kt) · [`SettingsScreen.kt`](../app/src/main/java/com/example/ui/screens/SettingsScreen.kt) | Editor personalizado, vista previa `3×3`, temas, acabados y ajustes del motor |
| **Datos y Tema** | [`SettingsRepository.kt`](../app/src/main/java/com/example/data/SettingsRepository.kt) · [`SettingsState.kt`](../app/src/main/java/com/example/data/SettingsState.kt) · [`Theme.kt`](../app/src/main/java/com/example/ui/theme/Theme.kt) | DataStore local, paletas, [`Color.kt`](../app/src/main/java/com/example/ui/theme/Color.kt) y [`Type.kt`](../app/src/main/java/com/example/ui/theme/Type.kt) |
| **Splash y Res** | [`avd_splash_tiles.xml`](../app/src/main/res/drawable/avd_splash_tiles.xml) · [`themes.xml`](../app/src/main/res/values/themes.xml) · [`AndroidManifest.xml`](../app/src/main/AndroidManifest.xml) | Vector animado de inicio, temas día/noche y manifiesto |
| **Idiomas** | [`AppStrings.kt`](../app/src/main/java/com/example/i18n/AppStrings.kt) · [`locales_config.xml`](../app/src/main/res/xml/locales_config.xml) · [`strings.xml`](../app/src/main/res/values/strings.xml) | Traducciones reactivas (`EN`, `RU`, `ES`, `DE`, `FR`) |
| **Build y CI** | [`build.gradle.kts`](../build.gradle.kts) · [`app/build.gradle.kts`](../app/build.gradle.kts) · [`libs.versions.toml`](../gradle/libs.versions.toml) · [`release-apk.yml`](../.github/workflows/release-apk.yml) | Catálogo Gradle, firma y publicación automática de APK en GitHub Releases |
| **Pruebas** | [`ExampleUnitTest.kt`](../app/src/test/java/com/example/ExampleUnitTest.kt) · [`ExampleRobolectricTest.kt`](../app/src/test/java/com/example/ExampleRobolectricTest.kt) · [`GreetingScreenshotTest.kt`](../app/src/test/java/com/example/GreetingScreenshotTest.kt) | Pruebas algorítmicas, Robolectric y capturas Roborazzi |

---

## Compilación, Pruebas y Releases CI

1. **Clonar el repositorio** (JDK 21 / Android SDK 36 / Gradle 9.3.1):
   ```bash
   git clone https://github.com/legoninjago161514-a11y/15-puzzle-solver.git
   cd 15-puzzle-solver
   ```
2. **Ejecutar pruebas unitarias y Robolectric**:
   ```bash
   gradle :app:testDebugUnitTest
   ```
3. **Compilar APK Debug y Release**:
   ```bash
   gradle :app:assembleDebug
   gradle :app:assembleRelease
   ```
4. **Release automático en GitHub**: ejecuta [**Actions → Build & Release APK**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/actions) para compilar, probar, firmar y subir `slidegrid-<tag>.apk` a [**Releases**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases) (claves `.jks` ignoradas en [`.gitignore`](../.gitignore)).

---

## Privacidad y Licencia

- **Política de Privacidad**: cero anuncios, cero telemetría, almacenamiento 100% local — [`docs/PRIVACY_POLICY.md`](PRIVACY_POLICY.md).
- **Licencia**: MIT — [`LICENSE`](../LICENSE).

---

## Referencias y Stack Tecnológico

- [**Releases de SlideGrid**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases) · [**Repositorio**](https://github.com/legoninjago161514-a11y/15-puzzle-solver)
- [Kotlin](https://kotlinlang.org/docs/home.html) · [Jetpack Compose](https://developer.android.com/compose) · [Material Design 3](https://m3.material.io/) · [SplashScreen API](https://developer.android.com/develop/ui/views/launch/splash-screen) · [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [Korf (1985) IDA*](https://www.sciencedirect.com/science/article/pii/0004370285900840) · [Robolectric](https://robolectric.org/) · [Roborazzi](https://github.com/takahirom/roborazzi)
