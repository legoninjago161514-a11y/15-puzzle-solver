<div align="center">

# SlideGrid — Studio Tactile & Solveur Hybride

**Un jeu de taquin moderne (`3×3` à `10×10`) pour Android en Kotlin et Jetpack Compose**

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

## Présentation

**SlideGrid** associe une interface tactile soignée (tuiles céramiques avec relief de pression, animation d'ouverture et palettes studio) à un double moteur algorithmique : recherche optimale `IDA*` jusqu'en `4×4` et réduction constructive par couches jusqu'en `10×10`, 100 % hors ligne, sans publicité ni suivi.

## Fonctionnalités

- **Grilles de `3×3` à `10×10`** : formats carrés et rectangulaires libres.
- **Solveur Hybride** :
  - **`IDA*`** ([`PuzzleSolver.kt`](../app/src/main/java/com/example/solver/PuzzleSolver.kt)) avec distance de Manhattan et conflits linéaires.
  - **Réduction Constructive** ([`ConstructiveSolver.kt`](../app/src/main/java/com/example/solver/ConstructiveSolver.kt)) jusqu'en `10×10` avec optimisation des coups.
  - **Lecteur de solution** ([`GameHudAndControls.kt`](../app/src/main/java/com/example/ui/components/GameHudAndControls.kt)) de `1×` à `20×` et benchmark intégré.
- **Grille Personnalisée** ([`SetupScreen.kt`](../app/src/main/java/com/example/ui/screens/SetupScreen.kt)) : palette rapide, pavé numérique intégré, glisser-déposer, presse-papiers et vérification de parité.
- **Apparence & Langues** ([`SettingsScreen.kt`](../app/src/main/java/com/example/ui/screens/SettingsScreen.kt)) : thèmes Clair, Sombre, AMOLED, Material You et 6 langues dans [`AppStrings.kt`](../app/src/main/java/com/example/i18n/AppStrings.kt).

## Liens Directs vers les Fichiers

| Module | Fichier |
| :--- | :--- |
| Point d'entrée | [`MainActivity.kt`](../app/src/main/java/com/example/MainActivity.kt) |
| ViewModel | [`BoardViewModel.kt`](../app/src/main/java/com/example/viewmodel/BoardViewModel.kt) |
| Solveurs | [`PuzzleSolver.kt`](../app/src/main/java/com/example/solver/PuzzleSolver.kt) · [`ConstructiveSolver.kt`](../app/src/main/java/com/example/solver/ConstructiveSolver.kt) |
| Plateau & HUD | [`PuzzleBoard.kt`](../app/src/main/java/com/example/ui/components/PuzzleBoard.kt) · [`GameHudAndControls.kt`](../app/src/main/java/com/example/ui/components/GameHudAndControls.kt) · [`avd_splash_tiles.xml`](../app/src/main/res/drawable/avd_splash_tiles.xml) |
| Écrans | [`SetupScreen.kt`](../app/src/main/java/com/example/ui/screens/SetupScreen.kt) · [`SettingsScreen.kt`](../app/src/main/java/com/example/ui/screens/SettingsScreen.kt) |
| Données & Thème | [`SettingsRepository.kt`](../app/src/main/java/com/example/data/SettingsRepository.kt) · [`SettingsState.kt`](../app/src/main/java/com/example/data/SettingsState.kt) · [`Theme.kt`](../app/src/main/java/com/example/ui/theme/Theme.kt) |
| Tests & CI | [`ExampleUnitTest.kt`](../app/src/test/java/com/example/ExampleUnitTest.kt) · [`app/build.gradle.kts`](../app/build.gradle.kts) · [`release-apk.yml`](../.github/workflows/release-apk.yml) |
| Confidentialité & Licence | [`PRIVACY_POLICY.md`](PRIVACY_POLICY.md) · [`LICENSE`](../LICENSE) |

## Compilation et Tests

```bash
gradle :app:testDebugUnitTest
gradle :app:assembleDebug
```

## Références

- [Kotlin](https://kotlinlang.org/docs/home.html) · [Jetpack Compose](https://developer.android.com/compose) · [Material Design 3](https://m3.material.io/) · [Korf (1985) IDA*](https://www.sciencedirect.com/science/article/pii/0004370285900840)
