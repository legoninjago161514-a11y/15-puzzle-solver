package com.example.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ThemeMode(val title: String) {
    SYSTEM("Авто"),
    LIGHT("Свет"),
    DARK("Тень"),
    AMOLED("OLED")
}

enum class ColorPalette(val title: String, val primaryColor: Color) {
    TERRA("Терра", Color(0xFFC94A29)),
    SWISS("Швейцария", Color(0xFFD83820)),
    COBALT("Кобальт", Color(0xFF1E3F8F)),
    SAGE("Шалфей", Color(0xFF435E49)),
    WALNUT("Орех", Color(0xFF6B4226)),
    SLATE("Графит", Color(0xFF2C313A))
}

enum class TileColorScheme(val title: String, val previewColors: List<Color>) {
    CERAMIC("Керамика", listOf(Color(0xFFF6F2EA), Color(0xFFD9CFC1), Color(0xFFC94A29))),
    BAUHAUS("Баухаус", listOf(Color(0xFFDF9F42), Color(0xFFC85032), Color(0xFF2D4559))),
    TWO_TONE("Дуотон", listOf(Color(0xFFE8E0D2), Color(0xFF343E48), Color(0xFFC94A29))),
    WOOD("Дерево", listOf(Color(0xFFE6D5B8), Color(0xFFB98B5E), Color(0xFF5B3B24))),
    MONOCHROME("Графит", listOf(Color(0xFF1C1C1A), Color(0xFF6B6A66), Color(0xFFE8E5DE))),
    MINERAL("Минерал", listOf(Color(0xFFD9C5A9), Color(0xFFC96846), Color(0xFF2E4658))),
    HEATMAP("Статус", listOf(Color(0xFF4E7A61), Color(0xFFC98E38), Color(0xFFB05044)))
}

enum class NumberFormat(val title: String) {
    ARABIC("1 2 3"),
    ROMAN("I II"),
    COORDINATES("X,Y"),
    HEX("HEX")
}

enum class CornerRadiusStyle(val title: String, val radiusDp: Dp) {
    SHARP("Острые", 4.dp),
    BALANCED("Баланс", 9.dp),
    ROUNDED("Мягкие", 16.dp)
}

enum class SpringAnimationSpeed(val title: String, val stiffness: Float, val damping: Float) {
    SNAPPY("Быстро", 700f, 0.78f),
    BOUNCY("Упруго", 420f, 0.65f),
    SMOOTH("Мягко", 250f, 0.86f)
}

enum class SolverAlgorithm(val title: String, val subtitle: String) {
    HYBRID("Гибрид", "IDA* до 4×4, послойный выше"),
    OPTIMAL_IDA("IDA*", "Кратчайший путь (до 4×4)"),
    FAST_REDUCTION("Быстрый", "Мгновенная сборка для любых")
}

enum class HeuristicType(val title: String, val subtitle: String) {
    MANHATTAN_LINEAR_CONFLICT("Конфликты", "Манхэттен + линейные конфликты"),
    PURE_MANHATTAN("Манхэттен", "Классическая сумма расстояний")
}

enum class HapticMode(val title: String) {
    OFF("Выкл"),
    LIGHT("Лёгк."),
    MEDIUM("Сред."),
    STRONG("Сильн.")
}

data class BoardRecord(
    val bestMoves: Int,
    val bestTimeSeconds: Long
)

data class SolveStats(
    val algorithmLabel: String,
    val totalSteps: Int,
    val elapsedMs: Long
)

data class BenchmarkResult(
    val elapsedTimeMs: Long,
    val totalMoves: Int,
    val nodesEvaluated: Int,
    val solvedCount: Int,
    val rating: String
)

object TileStyleHelper {
    fun getColors(
        value: Int,
        totalTiles: Int,
        dist: Int,
        scheme: TileColorScheme,
        isDark: Boolean
    ): Pair<Color, Color> {
        return getColors(value, totalTiles, dist, 4, scheme, isDark)
    }

    fun getColors(
        value: Int,
        totalTiles: Int,
        dist: Int,
        gridCols: Int,
        scheme: TileColorScheme,
        isDark: Boolean
    ): Pair<Color, Color> {
        val targetRow = (value - 1) / gridCols.coerceAtLeast(1)
        val targetCol = (value - 1) % gridCols.coerceAtLeast(1)
        return when (scheme) {
            TileColorScheme.CERAMIC -> {
                if (isDark) {
                    if (dist == 0) Color(0xFF33302B) to Color(0xFFF3EFE6)
                    else Color(0xFF23221F) to Color(0xFFE7E2D8)
                } else {
                    if (dist == 0) Color(0xFFE9E2D4) to Color(0xFF1A1917)
                    else Color(0xFFFCF9F4) to Color(0xFF1A1917)
                }
            }
            TileColorScheme.BAUHAUS -> {
                when (targetRow % 4) {
                    0 -> if (isDark) Color(0xFFEBE4D6) to Color(0xFF1A1917) else Color(0xFFF2ECE1) to Color(0xFF1A1917)
                    1 -> Color(0xFFDF9F42) to Color(0xFF1E170C)
                    2 -> Color(0xFFC85032) to Color(0xFFFCF8F2)
                    else -> Color(0xFF2D4559) to Color(0xFFF2EFE9)
                }
            }
            TileColorScheme.TWO_TONE -> {
                val isEven = (targetRow + targetCol) % 2 == 0
                if (isEven) {
                    if (isDark) Color(0xFFE6DEC8) to Color(0xFF1B1A17)
                    else Color(0xFFEDE5D5) to Color(0xFF1B1A17)
                } else {
                    if (isDark) Color(0xFF2C353D) to Color(0xFFF2EFE9)
                    else Color(0xFF313B44) to Color(0xFFF6F3EC)
                }
            }
            TileColorScheme.WOOD -> {
                when {
                    dist == 0 -> Color(0xFFE2CCA8) to Color(0xFF24170B)
                    dist == 1 -> Color(0xFFB58559) to Color(0xFFFFFBF4)
                    else -> Color(0xFF5C3A21) to Color(0xFFF7EFE4)
                }
            }
            TileColorScheme.MONOCHROME -> {
                if (isDark) {
                    when {
                        dist == 0 -> Color(0xFFF0EDE5) to Color(0xFF141413)
                        dist == 1 -> Color(0xFF6B6A66) to Color(0xFFF5F3EE)
                        else -> Color(0xFF262624) to Color(0xFFDAD7CF)
                    }
                } else {
                    when {
                        dist == 0 -> Color(0xFF1A1917) to Color(0xFFF5F2EB)
                        dist == 1 -> Color(0xFF68655E) to Color(0xFFF7F4EE)
                        else -> Color(0xFFE4DFD5) to Color(0xFF1A1917)
                    }
                }
            }
            TileColorScheme.MINERAL -> {
                val t = ((value - 1).toFloat() / (totalTiles.coerceAtLeast(2) - 1)).coerceIn(0f, 1f)
                val bg = lerpColor(
                    start = Color(0xFFD9C5A9),
                    mid = Color(0xFFC96446),
                    end = Color(0xFF2D4559),
                    t = t
                )
                val fg = if (t < 0.35f) Color(0xFF1C1915) else Color(0xFFFAF6EE)
                bg to fg
            }
            TileColorScheme.HEATMAP -> {
                when {
                    dist == 0 -> Color(0xFF4E7A61) to Color(0xFFF8FBF9)
                    dist == 1 -> Color(0xFFC98E38) to Color(0xFF1F170A)
                    else -> Color(0xFFB05044) to Color(0xFFFCF6F5)
                }
            }
        }
    }

    private fun lerpColor(start: Color, mid: Color, end: Color, t: Float): Color {
        return if (t < 0.5f) {
            val k = t * 2f
            Color(
                red = start.red + (mid.red - start.red) * k,
                green = start.green + (mid.green - start.green) * k,
                blue = start.blue + (mid.blue - start.blue) * k,
                alpha = 1f
            )
        } else {
            val k = (t - 0.5f) * 2f
            Color(
                red = mid.red + (end.red - mid.red) * k,
                green = mid.green + (end.green - mid.green) * k,
                blue = mid.blue + (end.blue - mid.blue) * k,
                alpha = 1f
            )
        }
    }

    fun formatNumber(value: Int, format: NumberFormat, gridCols: Int): String {
        return when (format) {
            NumberFormat.ARABIC -> value.toString()
            NumberFormat.ROMAN -> toRoman(value)
            NumberFormat.COORDINATES -> {
                val r = (value - 1) / gridCols + 1
                val c = (value - 1) % gridCols + 1
                "$r,$c"
            }
            NumberFormat.HEX -> value.toString(16).uppercase()
        }
    }

    private fun toRoman(num: Int): String {
        if (num <= 0) return ""
        val values = intArrayOf(100, 90, 50, 40, 10, 9, 5, 4, 1)
        val symbols = arrayOf("C", "XC", "L", "XL", "X", "IX", "V", "IV", "I")
        val sb = StringBuilder()
        var n = num
        for (i in values.indices) {
            while (n >= values[i]) {
                n -= values[i]
                sb.append(symbols[i])
            }
        }
        return sb.toString()
    }
}
