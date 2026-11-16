<div align="center">

# SlideGrid — Tactile N-Puzzle & Hybrid Solver

**A modern sliding puzzle (`3×3` to `10×10`) for Android built with Kotlin & Jetpack Compose**

[![English](https://img.shields.io/badge/lang-English-1E3F8F?style=flat-square)](README.md)
[![Русский](https://img.shields.io/badge/lang-Русский-C94A29?style=flat-square)](docs/README.ru.md)
[![Español](https://img.shields.io/badge/lang-Español-C98E38?style=flat-square)](docs/README.es.md)
[![Deutsch](https://img.shields.io/badge/lang-Deutsch-2C313A?style=flat-square)](docs/README.de.md)
[![Français](https://img.shields.io/badge/lang-Français-435E49?style=flat-square)](docs/README.fr.md)

<br/>

[![Latest Release](https://img.shields.io/github/v/release/legoninjago161514-a11y/15-puzzle-solver?style=for-the-badge&color=C94A29&label=Release)](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases/latest)
[![Download APK](https://img.shields.io/badge/Download-APK-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases/latest)
[![Kotlin](https://img.shields.io/badge/Kotlin-Android-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/compose)
[![License: MIT](https://img.shields.io/badge/License-MIT-black?style=for-the-badge)](LICENSE)

[**Download Latest APK**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases/latest) · [**All Releases**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases) · [**CI Workflows**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/actions) · [**Report an Issue**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/issues)

</div>

---

## Table of Contents

- [Overview](#overview)
- [Download & Install (APK)](#download--install-apk)
- [Detailed Feature Breakdown](#detailed-feature-breakdown)
- [Hybrid Solver Architecture](#hybrid-solver-architecture)
- [Project Structure & File Index](#project-structure--file-index)
- [Build, Testing & CI Releases](#build-testing--ci-releases)
- [Privacy & License](#privacy--license)
- [References & Tech Stack](#references--tech-stack)

---

## Overview

**SlideGrid** is an offline-first Android sliding puzzle that pairs tactile industrial design (3D matte ceramic keycaps, spring physics, and curated studio palettes) with a high-performance dual-engine solver. It computes guaranteed shortest-move solutions for classic `3×3` and `4×4` boards via **`IDA*`** and deterministically solves arbitrary layouts up to **`10×10`** in milliseconds — with zero ads, zero trackers, and 100% local persistence.

---

## Download & Install (APK)

Pre-built signed release APKs (`slidegrid-<tag>.apk`) are published automatically in the repository's [**Releases**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases) section via [`.github/workflows/release-apk.yml`](.github/workflows/release-apk.yml).

1. Open the [**Latest Release**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases/latest) page.
2. Under **Assets**, download **`slidegrid-<version>.apk`** (`Android 7.0+` / min SDK `24`, target SDK `36`).
3. Open the downloaded `.apk` on your Android device and allow installation from your browser/file manager if prompted.

---

## Detailed Feature Breakdown

### 1. Gameplay, Gestures & Telemetry
- **Grid Sizes (`3×3` – `10×10`)**: Quick square presets (`3×3`, `4×4`, `5×5`, `6×6`, `8×8`, `10×10`) plus custom rectangular dimensions (`rows × cols`), orchestrated by [`BoardViewModel.kt`](app/src/main/java/com/example/viewmodel/BoardViewModel.kt) and selected via [`GameHudAndControls.kt`](app/src/main/java/com/example/ui/components/GameHudAndControls.kt).
- **Responsive Tactile Controls** ([`PuzzleBoard.kt`](app/src/main/java/com/example/ui/components/PuzzleBoard.kt)): Tap tiles to move, drag continuously with swipe gestures, and optionally slide an entire row or column in a single gesture.
- **Physics, Audio & Haptics**: 3 spring profiles (*Snappy*, *Bouncy*, *Smooth*), subtle aligned-tile dot markers, click sound effects, and 4 haptic intensity levels (*Off*, *Light*, *Medium*, *Strong*).
- **HUD, Undo & Personal Bests**: Real-time move counter, session timer, aligned-tile progress bar, move undo history, confetti victory modal, and per-dimension best move/time records persisted locally in [`SettingsRepository.kt`](app/src/main/java/com/example/data/SettingsRepository.kt).

### 2. Hybrid Solver Engine & Interactive Player
- **Optimal `IDA*` Engine** ([`PuzzleSolver.kt`](app/src/main/java/com/example/solver/PuzzleSolver.kt)): Iterative Deepening A* search supporting **Manhattan Distance** and **Manhattan + Linear Conflict** heuristics to guarantee minimum move counts on grids up to `4×4`.
- **Constructive Reduction Solver** ([`ConstructiveSolver.kt`](app/src/main/java/com/example/solver/ConstructiveSolver.kt)): Deterministic layer-by-layer row/column reduction paired with a `3×3` finisher and redundant-move compression for boards up to `10×10`.
- **Solution Playback & Benchmark** ([`GameHudAndControls.kt`](app/src/main/java/com/example/ui/components/GameHudAndControls.kt)): Auto-play (`1×`–`20×` speed), step forward/backward, jump-to-end controls, and an in-app 4-board `3×3` benchmark reporting runtime, move totals, and expanded nodes.

### 3. Custom Board Editor ([`SetupScreen.kt`](app/src/main/java/com/example/ui/screens/SetupScreen.kt))
- **Three Input Modes**:
  1. **Tile Palette**: Fast one-tap cell assignment.
  2. **Numeric Keypad**: Built-in calculator-style keypad for multi-digit boards up to `99`.
  3. **Drag & Drop Tray**: Arrange tiles visually with sequential or randomized tray ordering.
- **Validation & Clipboard**: Auto-fill missing cells, import/export layouts via system clipboard, and automatic permutation parity validation so every custom board is 100% solvable.

### 4. Studio Themes, Keycap Finishes & Native Animated Splash
- **Themes & Palettes** ([`Theme.kt`](app/src/main/java/com/example/ui/theme/Theme.kt), [`Color.kt`](app/src/main/java/com/example/ui/theme/Color.kt)): *System*, *Light*, *Dark*, true *AMOLED* (`#000000`), Material You dynamic colors (`Android 12+`), and 6 curated studio palettes (**Terra, Swiss, Cobalt, Sage, Walnut, Slate**).
- **7 Tile Finishes & 4 Notations** ([`SettingsState.kt`](app/src/main/java/com/example/data/SettingsState.kt), [`SettingsScreen.kt`](app/src/main/java/com/example/ui/screens/SettingsScreen.kt)): *Ceramic, Bauhaus, Duo, Wood, Graphite, Mineral, Status*; display tiles in *Arabic*, *Roman* (`I, II`), *Coordinate* (`X,Y`), or *Hexadecimal* notation, tested live on an interactive `3×3` preview board.
- **Bundled Editorial Typography**: Local [`Plus Jakarta Sans`](app/src/main/res/font/plus_jakarta_sans.ttf) and [`JetBrains Mono`](app/src/main/res/font/jetbrains_mono.ttf) configured in [`Type.kt`](app/src/main/java/com/example/ui/theme/Type.kt).
- **Native Cold-Start Animation**: Seamless Android SplashScreen (`core-splashscreen`) using a vector [`AnimatedVectorDrawable`](app/src/main/res/drawable/avd_splash_tiles.xml) built on [`ic_splash_tiles_vector.xml`](app/src/main/res/drawable/ic_splash_tiles_vector.xml) and adaptive light/dark backgrounds in [`themes.xml`](app/src/main/res/values/themes.xml) so the 2×2 keycaps slide into formation immediately from millisecond zero.

### 5. Live Multi-Language Localization
- Switch live inside Settings between **System Auto**, **English**, **Русский**, **Español**, **Deutsch**, and **Français** via [`AppStrings.kt`](app/src/main/java/com/example/i18n/AppStrings.kt) and [`locales_config.xml`](app/src/main/res/xml/locales_config.xml).

---

## Hybrid Solver Architecture

| Grid Size / Mode | Active Algorithm | Heuristic / Method | Output Characteristic | Source |
| :--- | :--- | :--- | :--- | :--- |
| Up to `4×4` (`HYBRID` / `OPTIMAL_IDA`) | `IDA*` Search | Manhattan + Linear Conflict | Shortest optimal path | [`PuzzleSolver.kt`](app/src/main/java/com/example/solver/PuzzleSolver.kt) |
| `5×5` – `10×10` (or `FAST_REDUCTION`) | Layer-by-Layer Reduction | Row/Col reduction + `3×3` finisher + move cancellation | Instant deterministic solve | [`ConstructiveSolver.kt`](app/src/main/java/com/example/solver/ConstructiveSolver.kt) |
| `2×N` / `N×2` | Bounded BFS | State-space breadth-first traversal | Compact valid solution | [`ConstructiveSolver.kt`](app/src/main/java/com/example/solver/ConstructiveSolver.kt) |

---

## Project Structure & File Index

```text
.
├── README.md                                        # Main project documentation (English)
├── LICENSE                                          # MIT License
├── build.gradle.kts                                 # Root Gradle configuration
├── settings.gradle.kts                              # Module & repository setup
├── .gitignore                                       # Git exclusion rules
├── .github/workflows/
│   └── release-apk.yml                              # Automated test, APK build & GitHub Release workflow
├── docs/                                            # Localized READMEs & store policies
│   ├── README.ru.md                                 # Russian README
│   ├── README.es.md                                 # Spanish README
│   ├── README.de.md                                 # German README
│   ├── README.fr.md                                 # French README
│   └── PRIVACY_POLICY.md                            # Offline-first Privacy Policy
├── gradle/
│   └── libs.versions.toml                           # Centralized Gradle Version Catalog
└── app/
    ├── build.gradle.kts                             # Dependencies, SDK versions & signing config
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml                  # Manifest, splash theme & locale config
        │   ├── res/
        │   │   ├── animator/                        # Vector splash tile animators
        │   │   ├── drawable/                        # AVD splash & studio vector icons
        │   │   ├── font/                            # Plus Jakarta Sans & JetBrains Mono
        │   │   ├── values/ & values-night/          # Themes, colors & default strings
        │   │   ├── values-{ru,es,de,fr}/            # Localized launcher resources
        │   │   └── xml/                             # locales_config.xml & backup rules
        │   └── java/com/example/
        │       ├── MainActivity.kt                  # SplashScreen API setup, navigation & exit guard
        │       ├── data/                            # Settings state, tile styles & DataStore
        │       │   ├── SettingsState.kt
        │       │   └── SettingsRepository.kt
        │       ├── i18n/                            # Live 5-language localization dictionary
        │       │   └── AppStrings.kt
        │       ├── solver/                          # Algorithmic puzzle solvers
        │       │   ├── PuzzleSolver.kt
        │       │   └── ConstructiveSolver.kt
        │       ├── viewmodel/                       # Game state, timer, records & solver orchestration
        │       │   └── BoardViewModel.kt
        │       └── ui/
        │           ├── components/                  # Tactile board, HUD, dialogs & solution player
        │           │   ├── PuzzleBoard.kt
        │           │   └── GameHudAndControls.kt
        │           ├── screens/                     # Custom Board editor & Settings screen
        │           │   ├── SetupScreen.kt
        │           │   └── SettingsScreen.kt
        │           └── theme/                       # Material 3 palettes & bundled typography
        │               ├── Color.kt
        │               ├── Theme.kt
        │               └── Type.kt
        └── test/java/com/example/                   # Unit, Robolectric & screenshot tests
            ├── ExampleUnitTest.kt
            ├── ExampleRobolectricTest.kt
            └── GreetingScreenshotTest.kt
```

### Quick File Reference

| Layer | File(s) | Responsibility |
| :--- | :--- | :--- |
| **App Entry** | [`MainActivity.kt`](app/src/main/java/com/example/MainActivity.kt) | `SplashScreen` exit transition, edge-to-edge layout, screen routing, back-press exit guard |
| **ViewModel** | [`BoardViewModel.kt`](app/src/main/java/com/example/viewmodel/BoardViewModel.kt) | Board moves, timer, undo history, personal records, clipboard I/O, solver & benchmark runner |
| **Solvers** | [`PuzzleSolver.kt`](app/src/main/java/com/example/solver/PuzzleSolver.kt) · [`ConstructiveSolver.kt`](app/src/main/java/com/example/solver/ConstructiveSolver.kt) | `IDA*` optimal search, layer-by-layer constructive reduction, move compression |
| **UI Components** | [`PuzzleBoard.kt`](app/src/main/java/com/example/ui/components/PuzzleBoard.kt) · [`GameHudAndControls.kt`](app/src/main/java/com/example/ui/components/GameHudAndControls.kt) | Keycap tiles, spring animations, swipes, HUD telemetry, solution player, victory & guide sheets |
| **Screens** | [`SetupScreen.kt`](app/src/main/java/com/example/ui/screens/SetupScreen.kt) · [`SettingsScreen.kt`](app/src/main/java/com/example/ui/screens/SettingsScreen.kt) | Custom board editor, `3×3` live preview, themes, tile finishes, solver & benchmark controls |
| **Data & Theme** | [`SettingsRepository.kt`](app/src/main/java/com/example/data/SettingsRepository.kt) · [`SettingsState.kt`](app/src/main/java/com/example/data/SettingsState.kt) · [`Theme.kt`](app/src/main/java/com/example/ui/theme/Theme.kt) | Jetpack DataStore persistence, palettes, tile color helpers, [`Color.kt`](app/src/main/java/com/example/ui/theme/Color.kt) & [`Type.kt`](app/src/main/java/com/example/ui/theme/Type.kt) |
| **Splash & Res** | [`avd_splash_tiles.xml`](app/src/main/res/drawable/avd_splash_tiles.xml) · [`themes.xml`](app/src/main/res/values/themes.xml) · [`AndroidManifest.xml`](app/src/main/AndroidManifest.xml) | Animated startup keycap vector (`res/animator/anim_splash_*.xml`), theme setup & manifest |
| **Localization** | [`AppStrings.kt`](app/src/main/java/com/example/i18n/AppStrings.kt) · [`locales_config.xml`](app/src/main/res/xml/locales_config.xml) · [`strings.xml`](app/src/main/res/values/strings.xml) | Live switching across `EN`, `RU`, `ES`, `DE`, `FR` |
| **Build & CI** | [`build.gradle.kts`](build.gradle.kts) · [`app/build.gradle.kts`](app/build.gradle.kts) · [`libs.versions.toml`](gradle/libs.versions.toml) · [`release-apk.yml`](.github/workflows/release-apk.yml) | Gradle dependencies, release signing & automated GitHub Release APK pipeline |
| **Tests** | [`ExampleUnitTest.kt`](app/src/test/java/com/example/ExampleUnitTest.kt) · [`ExampleRobolectricTest.kt`](app/src/test/java/com/example/ExampleRobolectricTest.kt) · [`GreetingScreenshotTest.kt`](app/src/test/java/com/example/GreetingScreenshotTest.kt) | Solver tests, Robolectric resource checks & Roborazzi UI snapshots |

---

## Build, Testing & CI Releases

1. **Clone the repository** and open it in **Android Studio** (JDK 21 / Android SDK 36) or use **Gradle 9.3.1**:
   ```bash
   git clone https://github.com/legoninjago161514-a11y/15-puzzle-solver.git
   cd 15-puzzle-solver
   ```
2. **Run all unit, Robolectric & screenshot tests**:
   ```bash
   gradle :app:testDebugUnitTest
   ```
3. **Build Debug APK**:
   ```bash
   gradle :app:assembleDebug
   ```
4. **Automated GitHub Release APK**:
   Open [**Actions → Build & Release APK**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/actions) → **Run workflow** (or push a `v*` tag). [`.github/workflows/release-apk.yml`](.github/workflows/release-apk.yml) runs unit tests, signs the release build, and attaches `slidegrid-<tag>.apk` to [**Releases**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases).
5. **Custom Release Signing (APK / AAB)**:
   Supply `KEYSTORE_PATH`, `STORE_PASSWORD`, and `KEY_PASSWORD` via environment variables (or GitHub Secrets; `.jks` keystores are excluded by [`.gitignore`](.gitignore)) and run:
   ```bash
   gradle :app:assembleRelease
   gradle :app:bundleRelease
   ```

---

## Privacy & License

- **Privacy Policy**: Zero ads, zero telemetry, 100% local on-device storage — read [`docs/PRIVACY_POLICY.md`](docs/PRIVACY_POLICY.md).
- **License**: Distributed under the MIT License — see [`LICENSE`](LICENSE).

---

## References & Tech Stack

- [**SlideGrid Releases**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases) & [**Source Code**](https://github.com/legoninjago161514-a11y/15-puzzle-solver)
- [Kotlin Programming Language](https://kotlinlang.org/docs/home.html)
- [Jetpack Compose](https://developer.android.com/compose) & [Material Design 3](https://m3.material.io/)
- [Android SplashScreen API (`core-splashscreen`)](https://developer.android.com/develop/ui/views/launch/splash-screen)
- [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [Richard E. Korf (1985) — *Depth-First Iterative-Deepening: An Optimal Admissible Tree Search*](https://www.sciencedirect.com/science/article/pii/0004370285900840)
- [Robolectric](https://robolectric.org/) & [Roborazzi](https://github.com/takahirom/roborazzi)
