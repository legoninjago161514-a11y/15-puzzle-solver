<div align="center">

# SlideGrid — Studio Tactile & Solveur Hybride

**Un jeu de taquin moderne (`3×3` à `10×10`) pour Android en Kotlin et Jetpack Compose**

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

[**Télécharger le dernier APK**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases/latest) · [**Toutes les versions**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases) · [**Workflows CI**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/actions) · [**Signaler un bug**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/issues)

</div>

---

## Sommaire

- [Présentation](#présentation)
- [Téléchargement et Installation (APK)](#téléchargement-et-installation-apk)
- [Fonctionnalités Détaillées](#fonctionnalités-détaillées)
- [Architecture du Solveur Hybride](#architecture-du-solveur-hybride)
- [Structure du Projet et Index des Fichiers](#structure-du-projet-et-index-des-fichiers)
- [Compilation, Tests et Releases CI](#compilation-tests-et-releases-ci)
- [Confidentialité et Licence](#confidentialité-et-licence)
- [Références et Stack Technique](#références-et-stack-technique)

---

## Présentation

**SlideGrid** est un jeu de taquin hors ligne pour Android alliant design industriel tactile (tuiles céramiques 3D, physique à ressorts et palettes studio) et double moteur algorithmique : recherche optimale la plus courte jusqu'en `4×4` via **`IDA*`** et résolution déterministe jusqu'en **`10×10`** en quelques millisecondes — sans publicité ni suivi.

---

## Téléchargement et Installation (APK)

Les APK signés (`slidegrid-<tag>.apk`) sont générés automatiquement dans la section [**Releases**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases) via [`.github/workflows/release-apk.yml`](../.github/workflows/release-apk.yml).

1. Ouvrez la page de la [**Dernière Version**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases/latest).
2. Sous **Assets**, téléchargez **`slidegrid-<version>.apk`** (`Android 7.0+` / min SDK `24`, target SDK `36`).
3. Ouvrez le fichier sur votre appareil Android et autorisez l'installation si demandé.

---

## Fonctionnalités Détaillées

### 1. Gameplay, Gestes et Télémétrie
- **Grilles de `3×3` à `10×10`** : formats carrés rapides (`3×3`, `4×4`, `5×5`, `6×6`, `8×8`, `10×10`) et grilles rectangulaires personnalisées (`lignes × colonnes`) gérés par [`BoardViewModel.kt`](../app/src/main/java/com/example/viewmodel/BoardViewModel.kt) et [`GameHudAndControls.kt`](../app/src/main/java/com/example/ui/components/GameHudAndControls.kt).
- **Contrôle Tactile Fluide** ([`PuzzleBoard.kt`](../app/src/main/java/com/example/ui/components/PuzzleBoard.kt)) : glissement au toucher, balayage continu et déplacement optionnel de toute une ligne ou colonne en un seul geste.
- **Physique, Audio et Haptique** : 3 profils de ressort (*Vif*, *Rebondissant*, *Doux*), repère de tuile alignée, clic sonore et 4 niveaux haptiques.
- **HUD, Annulation et Records** : compteur de coups, chronomètre, barre de progression, historique d'annulation, confettis de victoire et records locaux dans [`SettingsRepository.kt`](../app/src/main/java/com/example/data/SettingsRepository.kt).

### 2. Solveur Hybride et Lecteur
- **Moteur Optimal `IDA*`** ([`PuzzleSolver.kt`](../app/src/main/java/com/example/solver/PuzzleSolver.kt)) : heuristiques **Manhattan** et **Manhattan + Conflits Linéaires** jusqu'en `4×4`.
- **Réduction Constructive** ([`ConstructiveSolver.kt`](../app/src/main/java/com/example/solver/ConstructiveSolver.kt)) : réduction par couches suivie d'une finition `3×3` et d'une compression des coups jusqu'en `10×10`.
- **Lecteur et Benchmark** ([`GameHudAndControls.kt`](../app/src/main/java/com/example/ui/components/GameHudAndControls.kt)) : lecture auto (`1×`–`20×`), pas à pas et benchmark `3×3` intégré.

### 3. Éditeur de Grille Personnalisée ([`SetupScreen.kt`](../app/src/main/java/com/example/ui/screens/SetupScreen.kt))
- **Trois Modes de Saisie** : palette rapide en un appui, pavé numérique intégré et réserve glisser-déposer (aléatoire ou ordonnée).
- **Validation** : remplissage automatique, import/export via presse-papiers et vérification de parité garantissant une grille 100 % soluble.

### 4. Thèmes Studio, Finitions et Démarrage Animé Natif
- **Thèmes & Palettes** ([`Theme.kt`](../app/src/main/java/com/example/ui/theme/Theme.kt), [`Color.kt`](../app/src/main/java/com/example/ui/theme/Color.kt)) : *Auto*, *Clair*, *Sombre*, *OLED* (`#000000`), Material You (`Android 12+`) et 6 palettes (**Terra, Swiss, Cobalt, Sage, Walnut, Slate**).
- **7 Finitions & 4 Notations** ([`SettingsState.kt`](../app/src/main/java/com/example/data/SettingsState.kt), [`SettingsScreen.kt`](../app/src/main/java/com/example/ui/screens/SettingsScreen.kt)) : *Céramique, Bauhaus, Duo, Bois, Graphite, Minéral, Statut* ; formats *Arabe*, *Romain*, *Coordonnées* et *Hexadécimal* avec aperçu `3×3` en direct.
- **Typographie Locale** : [`Plus Jakarta Sans`](../app/src/main/res/font/plus_jakarta_sans.ttf) et [`JetBrains Mono`](../app/src/main/res/font/jetbrains_mono.ttf) dans [`Type.kt`](../app/src/main/java/com/example/ui/theme/Type.kt).
- **SplashScreen Animé (`Android 12+`)** : API `core-splashscreen` avec [`AnimatedVectorDrawable`](../app/src/main/res/drawable/avd_splash_tiles.xml), [`ic_splash_tiles_vector.xml`](../app/src/main/res/drawable/ic_splash_tiles_vector.xml) et thèmes adaptatifs dans [`themes.xml`](../app/src/main/res/values/themes.xml).

### 5. Localisation en Direct (6 options)
- Changement instantané entre **Auto**, **English**, **Русский**, **Español**, **Deutsch** et **Français** via [`AppStrings.kt`](../app/src/main/java/com/example/i18n/AppStrings.kt) et [`locales_config.xml`](../app/src/main/res/xml/locales_config.xml).

---

## Architecture du Solveur Hybride

| Taille / Mode | Algorithme | Heuristique / Méthode | Résultat | Fichier |
| :--- | :--- | :--- | :--- | :--- |
| Jusqu'à `4×4` (`HYBRID` / `OPTIMAL_IDA`) | Recherche `IDA*` | Manhattan + conflits linéaires | Plus court chemin optimal | [`PuzzleSolver.kt`](../app/src/main/java/com/example/solver/PuzzleSolver.kt) |
| `5×5` – `10×10` (ou `FAST_REDUCTION`) | Réduction par couches | Lignes/Colonnes + finition `3×3` + compression | Résolution instantanée | [`ConstructiveSolver.kt`](../app/src/main/java/com/example/solver/ConstructiveSolver.kt) |
| `2×N` / `N×2` | BFS borné | Parcours en largeur | Solution compacte valide | [`ConstructiveSolver.kt`](../app/src/main/java/com/example/solver/ConstructiveSolver.kt) |

---

## Structure du Projet et Index des Fichiers

| Module | Fichier(s) | Rôle |
| :--- | :--- | :--- |
| **Entrée** | [`MainActivity.kt`](../app/src/main/java/com/example/MainActivity.kt) | Transition `SplashScreen`, edge-to-edge, navigation, confirmation de sortie |
| **ViewModel** | [`BoardViewModel.kt`](../app/src/main/java/com/example/viewmodel/BoardViewModel.kt) | Coups, chrono, annulation, records, presse-papiers, solveur & benchmark |
| **Solveurs** | [`PuzzleSolver.kt`](../app/src/main/java/com/example/solver/PuzzleSolver.kt) · [`ConstructiveSolver.kt`](../app/src/main/java/com/example/solver/ConstructiveSolver.kt) | `IDA*`, réduction constructive et optimisation des coups |
| **Composants UI** | [`PuzzleBoard.kt`](../app/src/main/java/com/example/ui/components/PuzzleBoard.kt) · [`GameHudAndControls.kt`](../app/src/main/java/com/example/ui/components/GameHudAndControls.kt) | Tuiles 3D, ressorts, gestes, HUD, lecteur et modales |
| **Écrans** | [`SetupScreen.kt`](../app/src/main/java/com/example/ui/screens/SetupScreen.kt) · [`SettingsScreen.kt`](../app/src/main/java/com/example/ui/screens/SettingsScreen.kt) | Éditeur perso, aperçu `3×3`, palettes et options du solveur |
| **Données & Thème** | [`SettingsRepository.kt`](../app/src/main/java/com/example/data/SettingsRepository.kt) · [`SettingsState.kt`](../app/src/main/java/com/example/data/SettingsState.kt) · [`Theme.kt`](../app/src/main/java/com/example/ui/theme/Theme.kt) | DataStore local, palettes, [`Color.kt`](../app/src/main/java/com/example/ui/theme/Color.kt) & [`Type.kt`](../app/src/main/java/com/example/ui/theme/Type.kt) |
| **Splash & Res** | [`avd_splash_tiles.xml`](../app/src/main/res/drawable/avd_splash_tiles.xml) · [`themes.xml`](../app/src/main/res/values/themes.xml) · [`AndroidManifest.xml`](../app/src/main/AndroidManifest.xml) | Animation vectorielle de lancement, thème jour/nuit & manifeste |
| **Langues** | [`AppStrings.kt`](../app/src/main/java/com/example/i18n/AppStrings.kt) · [`locales_config.xml`](../app/src/main/res/xml/locales_config.xml) · [`strings.xml`](../app/src/main/res/values/strings.xml) | Traductions réactives (`EN`, `RU`, `ES`, `DE`, `FR`) |
| **Build & CI** | [`build.gradle.kts`](../build.gradle.kts) · [`app/build.gradle.kts`](../app/build.gradle.kts) · [`libs.versions.toml`](../gradle/libs.versions.toml) · [`release-apk.yml`](../.github/workflows/release-apk.yml) | Catalogue Gradle, signature et publication APK sur GitHub Releases |
| **Tests** | [`ExampleUnitTest.kt`](../app/src/test/java/com/example/ExampleUnitTest.kt) · [`ExampleRobolectricTest.kt`](../app/src/test/java/com/example/ExampleRobolectricTest.kt) · [`GreetingScreenshotTest.kt`](../app/src/test/java/com/example/GreetingScreenshotTest.kt) | Tests solveurs, Robolectric & captures Roborazzi |

---

## Compilation, Tests et Releases CI

1. **Cloner le dépôt** (JDK 21 / Android SDK 36 / Gradle 9.3.1) :
   ```bash
   git clone https://github.com/legoninjago161514-a11y/15-puzzle-solver.git
   cd 15-puzzle-solver
   ```
2. **Lancer les tests unitaires et Robolectric** :
   ```bash
   gradle :app:testDebugUnitTest
   ```
3. **Compiler l'APK Debug et Release** :
   ```bash
   gradle :app:assembleDebug
   gradle :app:assembleRelease
   ```
4. **Release automatique sur GitHub** : exécutez [**Actions → Build & Release APK**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/actions) pour tester, signer et publier `slidegrid-<tag>.apk` dans [**Releases**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases) (clés `.jks` ignorées par [`.gitignore`](../.gitignore)).

---

## Confidentialité et Licence

- **Confidentialité** : zéro pub, zéro traceur, stockage 100 % local — [`docs/PRIVACY_POLICY.md`](PRIVACY_POLICY.md).
- **Licence** : MIT — [`LICENSE`](../LICENSE).

---

## Références et Stack Technique

- [**Releases SlideGrid**](https://github.com/legoninjago161514-a11y/15-puzzle-solver/releases) · [**Dépôt GitHub**](https://github.com/legoninjago161514-a11y/15-puzzle-solver)
- [Kotlin](https://kotlinlang.org/docs/home.html) · [Jetpack Compose](https://developer.android.com/compose) · [Material Design 3](https://m3.material.io/) · [SplashScreen API](https://developer.android.com/develop/ui/views/launch/splash-screen) · [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [Korf (1985) IDA*](https://www.sciencedirect.com/science/article/pii/0004370285900840) · [Robolectric](https://robolectric.org/) · [Roborazzi](https://github.com/takahirom/roborazzi)
