<div align="center">

# Puzzle 15 — Estudio Táctil y Solucionador Híbrido

**Un rompecabezas deslizante moderno (`3×3` a `10×10`) para Android en Kotlin y Jetpack Compose**

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

## Resumen

**Puzzle 15** combina una estética industrial táctil (fichas cerámicas con relieve físico y paletas cuidadas) con un motor algorítmico híbrido capaz de encontrar la ruta más corta en tableros hasta `4×4` y resolver cuadrículas de hasta `10×10` en milisegundos — 100% sin conexión, sin anuncios y sin rastreadores.

## Características Principales

- **Cuadrículas de `3×3` a `10×10`**: tamaños cuadrados y rectangulares personalizados.
- **Solucionador Híbrido**:
  - **`IDA*`** ([`PuzzleSolver.kt`](../app/src/main/java/com/example/solver/PuzzleSolver.kt)): solución óptima hasta `4×4` con distancia Manhattan y conflictos lineales.
  - **Reducción Constructiva** ([`ConstructiveSolver.kt`](../app/src/main/java/com/example/solver/ConstructiveSolver.kt)): resolución por capas hasta `10×10` con optimizador de movimientos.
  - **Reproductor paso a paso** ([`GameHudAndControls.kt`](../app/src/main/java/com/example/ui/components/GameHudAndControls.kt)) con velocidad `1×`–`20×` y prueba de rendimiento integrada.
- **Tablero Personalizado** ([`SetupScreen.kt`](../app/src/main/java/com/example/ui/screens/SetupScreen.kt)): paleta rápida, teclado numérico integrado, bandeja de arrastrar y soltar, importación/exportación y validación de paridad.
- **Personalización e Idiomas** ([`SettingsScreen.kt`](../app/src/main/java/com/example/ui/screens/SettingsScreen.kt)): temas Claro, Oscuro, AMOLED, Material You y 6 idiomas en [`AppStrings.kt`](../app/src/main/java/com/example/i18n/AppStrings.kt).

## Enlaces Rápidos a Archivos

| Componente | Archivo |
| :--- | :--- |
| Entrada principal | [`MainActivity.kt`](../app/src/main/java/com/example/MainActivity.kt) |
| Estado y ViewModel | [`BoardViewModel.kt`](../app/src/main/java/com/example/viewmodel/BoardViewModel.kt) |
| Solucionadores | [`PuzzleSolver.kt`](../app/src/main/java/com/example/solver/PuzzleSolver.kt) · [`ConstructiveSolver.kt`](../app/src/main/java/com/example/solver/ConstructiveSolver.kt) |
| Tablero y HUD | [`PuzzleBoard.kt`](../app/src/main/java/com/example/ui/components/PuzzleBoard.kt) · [`GameHudAndControls.kt`](../app/src/main/java/com/example/ui/components/GameHudAndControls.kt) |
| Pantallas | [`SetupScreen.kt`](../app/src/main/java/com/example/ui/screens/SetupScreen.kt) · [`SettingsScreen.kt`](../app/src/main/java/com/example/ui/screens/SettingsScreen.kt) |
| Datos y Tema | [`SettingsRepository.kt`](../app/src/main/java/com/example/data/SettingsRepository.kt) · [`SettingsState.kt`](../app/src/main/java/com/example/data/SettingsState.kt) · [`Theme.kt`](../app/src/main/java/com/example/ui/theme/Theme.kt) |
| Pruebas y CI | [`ExampleUnitTest.kt`](../app/src/test/java/com/example/ExampleUnitTest.kt) · [`app/build.gradle.kts`](../app/build.gradle.kts) · [`release-apk.yml`](../.github/workflows/release-apk.yml) |
| Privacidad y Licencia | [`PRIVACY_POLICY.md`](PRIVACY_POLICY.md) · [`LICENSE`](../LICENSE) |

## Compilación y Pruebas

```bash
gradle :app:testDebugUnitTest
gradle :app:assembleDebug
```

## Referencias

- [Kotlin](https://kotlinlang.org/docs/home.html) · [Jetpack Compose](https://developer.android.com/compose) · [Material Design 3](https://m3.material.io/) · [Korf (1985) IDA*](https://www.sciencedirect.com/science/article/pii/0004370285900840)
