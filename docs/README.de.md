<div align="center">

# SlideGrid — Tactile Studio & Hybrider Solver

**Modernes Schiebepuzzle (`3×3` bis `10×10`) für Android mit Kotlin & Jetpack Compose**

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

## Überblick

**SlideGrid** verbindet taktiles Industriedesign mit einer leistungsstarken hybriden Lösungs-Engine: optimale Lösungen für Bretter bis `4×4` per `IDA*` sowie blitzschnelle schichtweise Reduktion bis `10×10` — komplett offline, ohne Werbung oder Tracking.

## Funktionen

- **Rastergrößen von `3×3` bis `10×10`**: quadratische und rechteckige Felder.
- **Hybrider Solver**:
  - **`IDA*`** ([`PuzzleSolver.kt`](../app/src/main/java/com/example/solver/PuzzleSolver.kt)) mit Manhattan-Distanz und linearen Konflikten.
  - **Konstruktive Reduktion** ([`ConstructiveSolver.kt`](../app/src/main/java/com/example/solver/ConstructiveSolver.kt)) bis `10×10` inkl. Zugoptimierung.
  - **Lösungs-Player** ([`GameHudAndControls.kt`](../app/src/main/java/com/example/ui/components/GameHudAndControls.kt)) mit `1×`–`20×` Geschwindigkeit und integriertem Benchmark.
- **Eigenes Brett** ([`SetupScreen.kt`](../app/src/main/java/com/example/ui/screens/SetupScreen.kt)): Kachel-Palette, Ziffernblock, Drag-and-Drop-Ablage, Zwischenablage und Paritätsprüfung.
- **Design & Sprachen** ([`SettingsScreen.kt`](../app/src/main/java/com/example/ui/screens/SettingsScreen.kt)): Hell, Dunkel, AMOLED, Material You und 6 Sprachen in [`AppStrings.kt`](../app/src/main/java/com/example/i18n/AppStrings.kt).

## Direktlinks zu den Dateien

| Bereich | Datei |
| :--- | :--- |
| Einstiegspunkt | [`MainActivity.kt`](../app/src/main/java/com/example/MainActivity.kt) |
| ViewModel | [`BoardViewModel.kt`](../app/src/main/java/com/example/viewmodel/BoardViewModel.kt) |
| Lösungsalgorithmen | [`PuzzleSolver.kt`](../app/src/main/java/com/example/solver/PuzzleSolver.kt) · [`ConstructiveSolver.kt`](../app/src/main/java/com/example/solver/ConstructiveSolver.kt) |
| Spielfeld & HUD | [`PuzzleBoard.kt`](../app/src/main/java/com/example/ui/components/PuzzleBoard.kt) · [`GameHudAndControls.kt`](../app/src/main/java/com/example/ui/components/GameHudAndControls.kt) · [`avd_splash_tiles.xml`](../app/src/main/res/drawable/avd_splash_tiles.xml) |
| Bildschirme | [`SetupScreen.kt`](../app/src/main/java/com/example/ui/screens/SetupScreen.kt) · [`SettingsScreen.kt`](../app/src/main/java/com/example/ui/screens/SettingsScreen.kt) |
| Daten & Theme | [`SettingsRepository.kt`](../app/src/main/java/com/example/data/SettingsRepository.kt) · [`SettingsState.kt`](../app/src/main/java/com/example/data/SettingsState.kt) · [`Theme.kt`](../app/src/main/java/com/example/ui/theme/Theme.kt) |
| Tests & CI | [`ExampleUnitTest.kt`](../app/src/test/java/com/example/ExampleUnitTest.kt) · [`app/build.gradle.kts`](../app/build.gradle.kts) · [`release-apk.yml`](../.github/workflows/release-apk.yml) |
| Datenschutz & Lizenz | [`PRIVACY_POLICY.md`](PRIVACY_POLICY.md) · [`LICENSE`](../LICENSE) |

## Build & Tests

```bash
gradle :app:testDebugUnitTest
gradle :app:assembleDebug
```

## Referenzen

- [Kotlin](https://kotlinlang.org/docs/home.html) · [Jetpack Compose](https://developer.android.com/compose) · [Material Design 3](https://m3.material.io/) · [Korf (1985) IDA*](https://www.sciencedirect.com/science/article/pii/0004370285900840)
