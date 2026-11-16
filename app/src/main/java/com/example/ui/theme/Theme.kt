package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.data.ColorPalette
import com.example.data.ThemeMode

private fun buildColorScheme(
    palette: ColorPalette,
    isDark: Boolean,
    isAmoled: Boolean
): ColorScheme {
    val bg = when {
        isAmoled -> Color(0xFF000000)
        isDark -> when (palette) {
            ColorPalette.TERRA -> Color(0xFF141413)
            ColorPalette.SWISS -> Color(0xFF111111)
            ColorPalette.COBALT -> Color(0xFF11141B)
            ColorPalette.SAGE -> Color(0xFF131613)
            ColorPalette.WALNUT -> Color(0xFF15110D)
            ColorPalette.SLATE -> Color(0xFF121417)
        }
        else -> when (palette) {
            ColorPalette.TERRA -> Color(0xFFF5F2EB)
            ColorPalette.SWISS -> Color(0xFFF3F3F0)
            ColorPalette.COBALT -> Color(0xFFF7F4ED)
            ColorPalette.SAGE -> Color(0xFFF3F2EC)
            ColorPalette.WALNUT -> Color(0xFFF5EFE6)
            ColorPalette.SLATE -> Color(0xFFF1F2F4)
        }
    }

    val surface = when {
        isAmoled -> Color(0xFF0C0C0B)
        isDark -> when (palette) {
            ColorPalette.TERRA -> Color(0xFF1D1C1A)
            ColorPalette.SWISS -> Color(0xFF1A1A1A)
            ColorPalette.COBALT -> Color(0xFF191E28)
            ColorPalette.SAGE -> Color(0xFF1B1F1B)
            ColorPalette.WALNUT -> Color(0xFF201A15)
            ColorPalette.SLATE -> Color(0xFF1A1D21)
        }
        else -> when (palette) {
            ColorPalette.SAGE -> Color(0xFFFAF9F5)
            ColorPalette.WALNUT -> Color(0xFFFBF8F3)
            else -> Color(0xFFFFFFFF)
        }
    }

    val surfaceContainerHigh = when {
        isAmoled -> Color(0xFF161614)
        isDark -> when (palette) {
            ColorPalette.TERRA -> Color(0xFF272522)
            ColorPalette.SWISS -> Color(0xFF242424)
            ColorPalette.COBALT -> Color(0xFF222937)
            ColorPalette.SAGE -> Color(0xFF242924)
            ColorPalette.WALNUT -> Color(0xFF2A221C)
            ColorPalette.SLATE -> Color(0xFF23272D)
        }
        else -> when (palette) {
            ColorPalette.TERRA -> Color(0xFFEBE6DC)
            ColorPalette.SWISS -> Color(0xFFE7E7E2)
            ColorPalette.COBALT -> Color(0xFFEAE5DA)
            ColorPalette.SAGE -> Color(0xFFE6E4D9)
            ColorPalette.WALNUT -> Color(0xFFEAE0D2)
            ColorPalette.SLATE -> Color(0xFFE4E6EA)
        }
    }

    val surfaceVariant = when {
        isAmoled -> Color(0xFF20201D)
        isDark -> when (palette) {
            ColorPalette.TERRA -> Color(0xFF322F2B)
            ColorPalette.SWISS -> Color(0xFF2E2E2E)
            ColorPalette.COBALT -> Color(0xFF2B3446)
            ColorPalette.SAGE -> Color(0xFF2E342D)
            ColorPalette.WALNUT -> Color(0xFF362B23)
            ColorPalette.SLATE -> Color(0xFF2D323A)
        }
        else -> when (palette) {
            ColorPalette.TERRA -> Color(0xFFDFD8CC)
            ColorPalette.SWISS -> Color(0xFFDCDCD6)
            ColorPalette.COBALT -> Color(0xFFDED8CC)
            ColorPalette.SAGE -> Color(0xFFDAD7CA)
            ColorPalette.WALNUT -> Color(0xFFE0D2C0)
            ColorPalette.SLATE -> Color(0xFFD7DAE0)
        }
    }

    val primary = when {
        isDark -> when (palette) {
            ColorPalette.TERRA -> Color(0xFFE46842)
            ColorPalette.SWISS -> Color(0xFFF05537)
            ColorPalette.COBALT -> Color(0xFF729BF2)
            ColorPalette.SAGE -> Color(0xFF8CAE91)
            ColorPalette.WALNUT -> Color(0xFFD49E6A)
            ColorPalette.SLATE -> Color(0xFFE2E4E9)
        }
        else -> when (palette) {
            ColorPalette.TERRA -> Color(0xFFC94A29)
            ColorPalette.SWISS -> Color(0xFFD83820)
            ColorPalette.COBALT -> Color(0xFF1E3F8F)
            ColorPalette.SAGE -> Color(0xFF435E49)
            ColorPalette.WALNUT -> Color(0xFF6B4226)
            ColorPalette.SLATE -> Color(0xFF2C313A)
        }
    }

    val tertiary = when {
        isDark -> when (palette) {
            ColorPalette.TERRA -> Color(0xFF7FA4B0)
            ColorPalette.SWISS -> Color(0xFF7CA3C7)
            ColorPalette.COBALT -> Color(0xFFDEB062)
            ColorPalette.SAGE -> Color(0xFFD68864)
            ColorPalette.WALNUT -> Color(0xFFDF8B4F)
            ColorPalette.SLATE -> Color(0xFFE5AD4B)
        }
        else -> when (palette) {
            ColorPalette.TERRA -> Color(0xFF2E4A56)
            ColorPalette.SWISS -> Color(0xFF2B5B84)
            ColorPalette.COBALT -> Color(0xFFB58134)
            ColorPalette.SAGE -> Color(0xFFB56542)
            ColorPalette.WALNUT -> Color(0xFFB86F24)
            ColorPalette.SLATE -> Color(0xFFC78A28)
        }
    }

    val onPrimary = when {
        isDark && palette == ColorPalette.SLATE -> Color(0xFF121417)
        isDark && palette == ColorPalette.WALNUT -> Color(0xFF19120B)
        isDark && palette == ColorPalette.SAGE -> Color(0xFF111612)
        isDark && palette == ColorPalette.COBALT -> Color(0xFF0D1422)
        isDark -> Color(0xFF19110E)
        else -> Color(0xFFFDFBF7)
    }

    val onBgLight = when (palette) {
        ColorPalette.WALNUT -> Color(0xFF211810)
        ColorPalette.COBALT -> Color(0xFF141821)
        ColorPalette.SAGE -> Color(0xFF1B201C)
        else -> Color(0xFF191816)
    }

    val onBgDark = Color(0xFFF1EDE4)
    val onSurfaceVariantDark = Color(0xFFA8A399)
    val onSurfaceVariantLight = Color(0xFF5E5A53)

    return if (isDark) {
        darkColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            secondary = primary.copy(alpha = 0.85f),
            tertiary = tertiary,
            onTertiary = Color(0xFF121210),
            background = bg,
            onBackground = onBgDark,
            surface = surface,
            onSurface = onBgDark,
            surfaceVariant = surfaceVariant,
            onSurfaceVariant = onSurfaceVariantDark,
            surfaceContainerHigh = surfaceContainerHigh,
            surfaceContainerHighest = surfaceVariant,
            primaryContainer = surfaceVariant,
            onPrimaryContainer = primary,
            secondaryContainer = surfaceContainerHigh,
            onSecondaryContainer = Color(0xFFE6E1D8),
            tertiaryContainer = tertiary.copy(alpha = 0.20f),
            onTertiaryContainer = tertiary,
            outline = Color(0xFF56534C),
            outlineVariant = Color(0xFF34322E)
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            secondary = primary.copy(alpha = 0.85f),
            tertiary = tertiary,
            onTertiary = Color.White,
            background = bg,
            onBackground = onBgLight,
            surface = surface,
            onSurface = onBgLight,
            surfaceVariant = surfaceVariant,
            onSurfaceVariant = onSurfaceVariantLight,
            surfaceContainerHigh = surfaceContainerHigh,
            surfaceContainerHighest = surfaceVariant,
            primaryContainer = surfaceVariant,
            onPrimaryContainer = primary,
            secondaryContainer = surfaceContainerHigh,
            onSecondaryContainer = Color(0xFF282521),
            tertiaryContainer = tertiary.copy(alpha = 0.14f),
            onTertiaryContainer = tertiary,
            outline = Color(0xFF9C968B),
            outlineVariant = Color(0xFFD4CEC2)
        )
    }
}

@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    colorPalette: ColorPalette = ColorPalette.TERRA,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK, ThemeMode.AMOLED -> true
    }
    val isAmoled = (themeMode == ThemeMode.AMOLED)

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            val base = if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            if (isAmoled) {
                base.copy(background = Color.Black, surface = Color(0xFF0C0C0B))
            } else {
                base
            }
        }
        else -> buildColorScheme(colorPalette, isDark, isAmoled)
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
