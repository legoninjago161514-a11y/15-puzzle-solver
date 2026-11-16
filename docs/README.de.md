<div align="center">

# SlideGrid — Tactile Studio & Hybrider Solver

**Modernes Schiebepuzzle (`3×3` bis `10×10`) für Android mit Kotlin & Jetpack Compose**

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

[**Neuestes APK herunterladen**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases/latest) · [**Alle Releases**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases) · [**CI-Workflows**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/actions) · [**Fehler melden**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/issues)

</div>

---

## Inhaltsverzeichnis

- [Überblick](#überblick)
- [Download & Installation (APK)](#download--installation-apk)
- [Funktionen im Detail](#funktionen-im-detail)
- [Hybride Solver-Architektur](#hybride-solver-architektur)
- [Projektstruktur & Dateiindex](#projektstruktur--dateiindex)
- [Build, Tests & CI-Releases](#build-tests--ci-releases)
- [Datenschutz & Lizenz](#datenschutz--lizenz)
- [Referenzen & Tech-Stack](#referenzen--tech-stack)

---

## Überblick

**SlideGrid** ist ein reines Offline-Schiebepuzzle für Android, das taktiles Industriedesign (matte 3D-Keramiktasten, Federphysik und kuratierte Farbpaletten) mit einer schnellen hybriden Lösungs-Engine verbindet: garantiert kürzeste Wege bis `4×4` per **`IDA*`** sowie deterministische Lösungen bis **`10×10`** in Millisekunden — komplett ohne Werbung oder Tracking.

---

## Download & Installation (APK)

Signierte Release-APKs (`slidegrid-<tag>.apk`) werden automatisch über [`.github/workflows/release-apk.yml`](../.github/workflows/release-apk.yml) unter [**Releases**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases) bereitgestellt.

1. Öffne die Seite des [**Neuesten Releases**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases/latest).
2. Lade unter **Assets** die Datei **`slidegrid-<version>.apk`** herunter (`Android 7.0+` / min SDK `24`, target SDK `36`).
3. Öffne die `.apk`-Datei auf deinem Android-Gerät und erlaube bei Bedarf die Installation aus dieser Quelle.

---

## Funktionen im Detail

### 1. Gameplay, Gesten & Telemetrie
- **Rastergrößen (`3×3` – `10×10`)**: quadratische Schnellwahl (`3×3`, `4×4`, `5×5`, `6×6`, `8×8`, `10×10`) sowie beliebige rechteckige Felder (`Zeilen × Spalten`) über [`BoardViewModel.kt`](../app/src/main/java/com/example/viewmodel/BoardViewModel.kt) und [`GameHudAndControls.kt`](../app/src/main/java/com/example/ui/components/GameHudAndControls.kt).
- **Taktile Steuerung** ([`PuzzleBoard.kt`](../app/src/main/java/com/example/ui/components/PuzzleBoard.kt)): Antippen, fließendes Wischen und optionales Verschieben ganzer Reihen oder Spalten mit einem Zug.
- **Physik, Sound & Haptik**: 3 Federprofile (*Direkt*, *Federnd*, *Weich*), Positionsmarkierung, Klick-Sound und 4 Haptik-Stufen.
- **HUD, Rückgängig & Rekorde**: Zugzähler, Timer, Fortschrittsbalken, Rückgängig-Verlauf, Sieges-Konfetti und lokale Bestzeiten in [`SettingsRepository.kt`](../app/src/main/java/com/example/data/SettingsRepository.kt).

### 2. Hybrider Solver & Lösungs-Player
- **Optimaler `IDA*`** ([`PuzzleSolver.kt`](../app/src/main/java/com/example/solver/PuzzleSolver.kt)): Iterative-Deepening-A* mit **Manhattan-Distanz** und **linearen Konflikten** bis `4×4`.
- **Konstruktive Reduktion** ([`ConstructiveSolver.kt`](../app/src/main/java/com/example/solver/ConstructiveSolver.kt)): schichtweise Zeilen-/Spalten-Reduktion plus `3×3`-Abschluss und Zugoptimierung bis `10×10`.
- **Player & Benchmark** ([`GameHudAndControls.kt`](../app/src/main/java/com/example/ui/components/GameHudAndControls.kt)): Auto-Wiedergabe (`1×`–`20×`), Einzelschritt vor/zurück und integrierter `3×3`-Benchmark.

### 3. Editor für eigene Bretter ([`SetupScreen.kt`](../app/src/main/java/com/example/ui/screens/SetupScreen.kt))
- **Drei Eingabemodi**: Schnell-Palette, integrierter Ziffernblock und Drag-and-Drop-Ablage (zufällig oder sortiert).
- **Prüfung & Zwischenablage**: automatisches Auffüllen, Import/Export über die Zwischenablage und Paritätsprüfung für 100 % lösbare Anordnungen.

### 4. Studio-Themes, Oberflächen & Nativer SplashScreen
- **Themes & Paletten** ([`Theme.kt`](../app/src/main/java/com/example/ui/theme/Theme.kt), [`Color.kt`](../app/src/main/java/com/example/ui/theme/Color.kt)): *Auto*, *Hell*, *Dunkel*, *OLED* (`#000000`), Material You (`Android 12+`) und 6 Paletten (**Terra, Swiss, Cobalt, Sage, Walnut, Slate**).
- **7 Kachel-Stile & 4 Formate** ([`SettingsState.kt`](../app/src/main/java/com/example/data/SettingsState.kt), [`SettingsScreen.kt`](../app/src/main/java/com/example/ui/screens/SettingsScreen.kt)): *Keramik, Bauhaus, Duo, Holz, Graphit, Mineral, Status*; *Arabisch*, *Römisch*, *Koordinaten* und *Hexadezimal* mit `3×3`-Live-Vorschau.
- **Lokale Typografie**: [`Plus Jakarta Sans`](../app/src/main/res/font/plus_jakarta_sans.ttf) und [`JetBrains Mono`](../app/src/main/res/font/jetbrains_mono.ttf) in [`Type.kt`](../app/src/main/java/com/example/ui/theme/Type.kt).
- **Animierter Start (`Android 12+`)**: System-SplashScreen (`core-splashscreen`) mit [`AnimatedVectorDrawable`](../app/src/main/res/drawable/avd_splash_tiles.xml), [`ic_splash_tiles_vector.xml`](../app/src/main/res/drawable/ic_splash_tiles_vector.xml) und angepasstem Theme in [`themes.xml`](../app/src/main/res/values/themes.xml).

### 5. Live-Mehrsprachigkeit (6 Optionen)
- Sofortige Umschaltung zwischen **Auto**, **English**, **Русский**, **Español**, **Deutsch** und **Français** via [`AppStrings.kt`](../app/src/main/java/com/example/i18n/AppStrings.kt) und [`locales_config.xml`](../app/src/main/res/xml/locales_config.xml).

---

## Hybride Solver-Architektur

| Raster / Modus | Algorithmus | Heuristik / Verfahren | Ergebnis | Datei |
| :--- | :--- | :--- | :--- | :--- |
| Bis `4×4` (`HYBRID` / `OPTIMAL_IDA`) | `IDA*`-Suche | Manhattan + lineare Konflikte | Kürzester optimaler Weg | [`PuzzleSolver.kt`](../app/src/main/java/com/example/solver/PuzzleSolver.kt) |
| `5×5` – `10×10` (oder `FAST_REDUCTION`) | Schichtweise Reduktion | Zeilen/Spalten + `3×3`-Finish + Zugkomprimierung | Sofortige Lösung | [`ConstructiveSolver.kt`](../app/src/main/java/com/example/solver/ConstructiveSolver.kt) |
| `2×N` / `N×2` | Begrenztes BFS | Breitensuche im Zustandsraum | Kompakte gültige Lösung | [`ConstructiveSolver.kt`](../app/src/main/java/com/example/solver/ConstructiveSolver.kt) |

---

## Projektstruktur & Dateiindex

| Bereich | Datei(en) | Aufgabe |
| :--- | :--- | :--- |
| **Einstieg** | [`MainActivity.kt`](../app/src/main/java/com/example/MainActivity.kt) | `SplashScreen`-Übergang, Edge-to-Edge, Navigation, Beenden-Dialog |
| **ViewModel** | [`BoardViewModel.kt`](../app/src/main/java/com/example/viewmodel/BoardViewModel.kt) | Züge, Timer, Undo-Verlauf, Rekorde, Zwischenablage, Solver & Benchmark |
| **Solver** | [`PuzzleSolver.kt`](../app/src/main/java/com/example/solver/PuzzleSolver.kt) · [`ConstructiveSolver.kt`](../app/src/main/java/com/example/solver/ConstructiveSolver.kt) | `IDA*`, konstruktive Reduktion und Zugoptimierung |
| **UI-Komponenten** | [`PuzzleBoard.kt`](../app/src/main/java/com/example/ui/components/PuzzleBoard.kt) · [`GameHudAndControls.kt`](../app/src/main/java/com/example/ui/components/GameHudAndControls.kt) | 3D-Kacheln, Federanimationen, Wischgesten, HUD, Player & Dialoge |
| **Bildschirme** | [`SetupScreen.kt`](../app/src/main/java/com/example/ui/screens/SetupScreen.kt) · [`SettingsScreen.kt`](../app/src/main/java/com/example/ui/screens/SettingsScreen.kt) | Brett-Editor, `3×3`-Live-Vorschau, Paletten und Solver-Optionen |
| **Daten & Theme** | [`SettingsRepository.kt`](../app/src/main/java/com/example/data/SettingsRepository.kt) · [`SettingsState.kt`](../app/src/main/java/com/example/data/SettingsState.kt) · [`Theme.kt`](../app/src/main/java/com/example/ui/theme/Theme.kt) | DataStore-Persistenz, Farbpaletten, [`Color.kt`](../app/src/main/java/com/example/ui/theme/Color.kt) & [`Type.kt`](../app/src/main/java/com/example/ui/theme/Type.kt) |
| **Splash & Res** | [`avd_splash_tiles.xml`](../app/src/main/res/drawable/avd_splash_tiles.xml) · [`themes.xml`](../app/src/main/res/values/themes.xml) · [`AndroidManifest.xml`](../app/src/main/AndroidManifest.xml) | Vektor-Startanimation, Day/Night-Splash-Theme & Manifest |
| **Sprachen** | [`AppStrings.kt`](../app/src/main/java/com/example/i18n/AppStrings.kt) · [`locales_config.xml`](../app/src/main/res/xml/locales_config.xml) · [`strings.xml`](../app/src/main/res/values/strings.xml) | Live-Übersetzungen (`EN`, `RU`, `ES`, `DE`, `FR`) |
| **Build & CI** | [`build.gradle.kts`](../build.gradle.kts) · [`app/build.gradle.kts`](../app/build.gradle.kts) · [`libs.versions.toml`](../gradle/libs.versions.toml) · [`release-apk.yml`](../.github/workflows/release-apk.yml) | Version Catalog, Signatur & automatische APK-Releases |
| **Tests** | [`ExampleUnitTest.kt`](../app/src/test/java/com/example/ExampleUnitTest.kt) · [`ExampleRobolectricTest.kt`](../app/src/test/java/com/example/ExampleRobolectricTest.kt) · [`GreetingScreenshotTest.kt`](../app/src/test/java/com/example/GreetingScreenshotTest.kt) | Solver-Tests, Robolectric & Roborazzi-Snapshots |

---

## Build, Tests & CI-Releases

1. **Repository klonen** (JDK 21 / Android SDK 36 / Gradle 9.3.1):
   ```bash
   git clone https://github.com/legoninjago161514-a11y/15-puzzle-solver.git
   cd 15-puzzle-solver
   ```
2. **Unit- und Robolectric-Tests ausführen**:
   ```bash
   gradle :app:testDebugUnitTest
   ```
3. **Debug- & Release-APK bauen**:
   ```bash
   gradle :app:assembleDebug
   gradle :app:assembleRelease
   ```
4. **Automatisches GitHub-Release**: [**Actions → Build & Release APK**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/actions) starten — das Workflow-Skript erstellt, testet, signiert und lädt `slidegrid-<tag>.apk` in [**Releases**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases) hoch (`.jks`-Schlüssel sind in [`.gitignore`](../.gitignore) ausgeschlossen).

---

## Datenschutz & Lizenz

- **Datenschutz**: keine Werbung, kein Tracking, 100 % lokal — [`docs/PRIVACY_POLICY.md`](PRIVACY_POLICY.md).
- **Lizenz**: MIT — [`LICENSE`](../LICENSE).

---

## Referenzen & Tech-Stack

- [**SlideGrid Releases**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases) · [**Repository**](https://github.com/legoninjago161514-a11y/15-puzzle-solver)
- [Kotlin](https://kotlinlang.org/docs/home.html) · [Jetpack Compose](https://developer.android.com/compose) · [Material Design 3](https://m3.material.io/) · [SplashScreen API](https://developer.android.com/develop/ui/views/launch/splash-screen) · [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [Korf (1985) IDA*](https://www.sciencedirect.com/science/article/pii/0004370285900840) · [Robolectric](https://robolectric.org/) · [Roborazzi](https://github.com/takahirom/roborazzi)
