<div align="center">

# SlideGrid — Tactile N-Puzzle & Hybrid Solver

**A modern sliding puzzle (`3×3` to `10×10`) for Android built with Kotlin & Jetpack Compose**

[![English](https://img.shields.io/badge/lang-English-1E3F8F?style=flat-square)](README.md)
[![Русский](https://img.shields.io/badge/lang-Русский-C94A29?style=flat-square)](docs/README.ru.md)
[![Español](https://img.shields.io/badge/lang-Español-C98E38?style=flat-square)](docs/README.es.md)
[![Deutsch](https://img.shields.io/badge/lang-Deutsch-2C313A?style=flat-square)](docs/README.de.md)
[![Français](https://img.shields.io/badge/lang-Français-435E49?style=flat-square)](docs/README.fr.md)

<br/>

![Kotlin](https://img.shields.io/badge/Kotlin-Android-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Min SDK](https://img.shields.io/badge/Min%20SDK-24%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-black?style=for-the-badge)

</div>

---

## Table of Contents

- [Overview](#overview)
- [Key Features](#key-features)
- [Hybrid Solver Architecture](#hybrid-solver-architecture)
- [Project Structure & File Index](#project-structure--file-index)
- [Build & Testing](#build--testing)
- [Privacy & License](#privacy--license)
- [References & Tech Stack](#references--tech-stack)

---

## Overview

**SlideGrid** combines tactile industrial design (matte ceramic keycap tiles, physical press bevels, animated keycap startup choreography, curated editorial palettes) with a high-performance dual-engine algorithmic solver capable of solving classic `3×3` / `4×4` boards optimally and reducing arbitrary `10×10` permutations in milliseconds — completely offline, with zero ads or trackers.

---

## Key Features

### Gameplay & Grid Flexibility
- **Grid Sizes from `3×3` to `10×10`**: Classic square boards (`3×3`, `4×4`, `5×5`, `6×6`, `8×8`, `10×10`) plus custom rectangular dimensions (`rows × cols`).
- **Fluid Gestures**: Tap-to-slide, continuous directional swipe gestures, and optional **multi-tile row/column shifting**.
- **Telemetry HUD & Records**: Live move counter, timer, aligned-tile completion bar, move undo history, and per-grid personal records stored in [SettingsRepository.kt](app/src/main/java/com/example/data/SettingsRepository.kt).

### Hybrid Algorithmic Solver & Player
- **Optimal `IDA*` Engine** ([`PuzzleSolver.kt`](app/src/main/java/com/example/solver/PuzzleSolver.kt)): Guarantees shortest-move solutions for grids up to `4×4` using **Manhattan Distance** or **Manhattan + Linear Conflict** heuristics.
- **Constructive Reduction Solver** ([`ConstructiveSolver.kt`](app/src/main/java/com/example/solver/ConstructiveSolver.kt)): Layer-by-layer row/column reduction followed by a `3×3` finisher for grids up to `10×10` with move optimization.
- **Interactive Solution Player** ([`GameHudAndControls.kt`](app/src/main/java/com/example/ui/components/GameHudAndControls.kt)): Play/pause auto-playback (`1×`–`20×` speed), step forward/backward, and jump to end.
- **Built-in Benchmark**: Evaluates 4 reference `3×3` permutations and reports elapsed time, move count, and evaluated search nodes.

### Custom Board Editor ([`SetupScreen.kt`](app/src/main/java/com/example/ui/screens/SetupScreen.kt))
- **Multiple Input Modes**:
  - One-tap numeric tile palette
  - Built-in calculator-style numeric keypad
  - Drag-and-drop tile tray with sequential or randomized ordering
- **Smart Validation**: Auto-fill remaining tiles, clipboard import/export, and automatic permutation parity verification to ensure 100% solvability.

### Tactile Studio Themes & Localization ([`SettingsScreen.kt`](app/src/main/java/com/example/ui/screens/SettingsScreen.kt))
- **Themes & Palettes**: System, Light, Dark, AMOLED (`#000000`), Material You dynamic colors (Android 12+), and 6 curated studio palettes (**Terra, Swiss, Cobalt, Sage, Walnut, Slate**) defined in [`Theme.kt`](app/src/main/java/com/example/ui/theme/Theme.kt).
- **Tile Finishes & Notations**: Ceramic, Bauhaus, Duo, Wood, Graphite, Mineral, Status Heatmap; Arabic, Roman (`I, II`), Coordinate (`X,Y`), and Hexadecimal notations ([`SettingsState.kt`](app/src/main/java/com/example/data/SettingsState.kt)).
- **6 UI Languages**: System Auto, Russian, English, Spanish, German, French ([`AppStrings.kt`](app/src/main/java/com/example/i18n/AppStrings.kt) & [`locales_config.xml`](app/src/main/res/xml/locales_config.xml)).

---

## Hybrid Solver Architecture

| Grid Size | Default Mode (`HYBRID`) | Heuristic / Method | Output |
| :--- | :--- | :--- | :--- |
| Up to `4×4` | `IDA*` Search | Manhattan + Linear Conflict | Shortest optimal path |
| `5×5` – `10×10` | Layer-by-Layer Reduction | Row/Col reduction + `3×3` finisher + move cancellation | Fast deterministic solve |
| `2×N` / `N×2` | Bounded BFS | Shortest state-space traversal | Valid short solution |

---

## Project Structure & File Index

```text
.
├── README.md                                        # Main documentation (English)
├── LICENSE                                          # MIT License
├── build.gradle.kts                                 # Root Gradle configuration
├── settings.gradle.kts                              # Gradle module & repositories setup
├── docs/                                            # Localized documentation & store policies
│   ├── README.ru.md                                 # Russian README
│   ├── README.es.md                                 # Spanish README
│   ├── README.de.md                                 # German README
│   ├── README.fr.md                                 # French README
│   └── PRIVACY_POLICY.md                            # Offline-first Privacy Policy
├── gradle/
│   └── libs.versions.toml                           # Centralized Version Catalog
└── app/
    ├── build.gradle.kts                             # App dependencies & signing config
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml                  # App manifest & per-app locale config
        │   ├── res/                                 # Drawables, adaptive icons, fonts & XML
        │   └── java/com/example/
        │       ├── MainActivity.kt                  # Entry point, root navigation & exit dialog
        │       ├── data/                            # Settings state, palettes & DataStore
        │       │   ├── SettingsState.kt
        │       │   └── SettingsRepository.kt
        │       ├── i18n/                            # Multi-language localization engine
        │       │   └── AppStrings.kt
        │       ├── solver/                          # Algorithmic puzzle solvers
        │       │   ├── PuzzleSolver.kt
        │       │   └── ConstructiveSolver.kt
        │       ├── viewmodel/                       # State management & game logic
        │       │   └── BoardViewModel.kt
        │       └── ui/
        │           ├── components/                  # Reusable game UI & tactile board
        │           │   ├── PuzzleBoard.kt
        │           │   └── GameHudAndControls.kt
        │           ├── screens/                     # Secondary screens
        │           │   ├── SetupScreen.kt
        │           │   └── SettingsScreen.kt
        │           └── theme/                       # Studio Material 3 theme & typography
        │               ├── Color.kt
        │               ├── Theme.kt
        │               └── Type.kt
        └── test/java/com/example/                   # JVM unit, Robolectric & screenshot tests
            ├── ExampleUnitTest.kt
            ├── ExampleRobolectricTest.kt
            └── GreetingScreenshotTest.kt
```

### Quick File Links

| Layer | File | Responsibility |
| :--- | :--- | :--- |
| **App Entry** | [`MainActivity.kt`](app/src/main/java/com/example/MainActivity.kt) | Edge-to-edge setup, screen transitions, top bar, back-press exit guard |
| **ViewModel** | [`BoardViewModel.kt`](app/src/main/java/com/example/viewmodel/BoardViewModel.kt) | Board state, timer, undo stack, records, clipboard I/O, solver & benchmark orchestration |
| **Solver (`IDA*`)** | [`PuzzleSolver.kt`](app/src/main/java/com/example/solver/PuzzleSolver.kt) | Iterative Deepening A* search, Manhattan & Linear Conflict heuristics |
| **Solver (`10×10`)** | [`ConstructiveSolver.kt`](app/src/main/java/com/example/solver/ConstructiveSolver.kt) | Constructive layer-by-layer reduction & redundant move optimizer |
| **Game Board** | [`PuzzleBoard.kt`](app/src/main/java/com/example/ui/components/PuzzleBoard.kt) · [`avd_splash_tiles.xml`](app/src/main/res/drawable/avd_splash_tiles.xml) | Keycap tiles, spring animations, native AVD splash & confetti |
| **HUD & Dialogs** | [`GameHudAndControls.kt`](app/src/main/java/com/example/ui/components/GameHudAndControls.kt) | Telemetry strip, solution player, grid selector, victory & guide sheets |
| **Custom Setup** | [`SetupScreen.kt`](app/src/main/java/com/example/ui/screens/SetupScreen.kt) | Tile palette, numeric keypad, drag tray & parity check |
| **Settings** | [`SettingsScreen.kt`](app/src/main/java/com/example/ui/screens/SettingsScreen.kt) | Live preview board, theme/palette controls, solver options & benchmark UI |
| **Data & Theme** | [`SettingsRepository.kt`](app/src/main/java/com/example/data/SettingsRepository.kt) · [`SettingsState.kt`](app/src/main/java/com/example/data/SettingsState.kt) · [`Theme.kt`](app/src/main/java/com/example/ui/theme/Theme.kt) | Jetpack DataStore persistence, palettes, tile color helpers & typography ([`Type.kt`](app/src/main/java/com/example/ui/theme/Type.kt)) |
| **Localization** | [`AppStrings.kt`](app/src/main/java/com/example/i18n/AppStrings.kt) · [`locales_config.xml`](app/src/main/res/xml/locales_config.xml) | Reactive translations (`RU`, `EN`, `ES`, `DE`, `FR`) |
| **Build & CI** | [`app/build.gradle.kts`](app/build.gradle.kts) · [`libs.versions.toml`](gradle/libs.versions.toml) · [`release-apk.yml`](.github/workflows/release-apk.yml) | Dependencies, signing config & automated GitHub Release APK workflow |
| **Tests** | [`ExampleUnitTest.kt`](app/src/test/java/com/example/ExampleUnitTest.kt) · [`ExampleRobolectricTest.kt`](app/src/test/java/com/example/ExampleRobolectricTest.kt) | Solver verification & Robolectric UI/resource tests |

---

## Build & Testing

1. **Clone the repository** and open it in **Android Studio** (JDK 21 / Android SDK 36) or use **Gradle 9.3.1**:
   ```bash
   git clone <repository-url>
   cd slidegrid-android
   ```
2. **Run all unit & Robolectric tests**:
   ```bash
   gradle :app:testDebugUnitTest
   ```
3. **Build Debug APK**:
   ```bash
   gradle :app:assembleDebug
   ```
4. **Automated GitHub Release APK**:
   Open **Actions → Build & Release APK → Run workflow** on GitHub (or push a `v*` tag) to automatically build, test, sign, and publish the APK into **Releases** via [`release-apk.yml`](.github/workflows/release-apk.yml).
5. **Store Release Signing (AAB)**:
   Provide keystore credentials via environment variables (`KEYSTORE_PATH`, `STORE_PASSWORD`, `KEY_PASSWORD`) or place `my-upload-key.jks` at the project root (ignored by [`.gitignore`](.gitignore)) and run:
   ```bash
   gradle :app:bundleRelease
   ```

---

## Privacy & License

- **Privacy Policy**: Zero ads, zero analytics, 100% local storage — read [docs/PRIVACY_POLICY.md](docs/PRIVACY_POLICY.md).
- **License**: Distributed under the MIT License — see [LICENSE](LICENSE).

---

## References & Tech Stack

- [Kotlin Programming Language](https://kotlinlang.org/docs/home.html)
- [Jetpack Compose](https://developer.android.com/compose) & [Material Design 3](https://m3.material.io/)
- [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [Richard E. Korf (1985) — *Depth-First Iterative-Deepening: An Optimal Admissible Tree Search*](https://www.sciencedirect.com/science/article/pii/0004370285900840)
- [Robolectric Testing Framework](https://robolectric.org/) & [Roborazzi](https://github.com/takahirom/roborazzi)
