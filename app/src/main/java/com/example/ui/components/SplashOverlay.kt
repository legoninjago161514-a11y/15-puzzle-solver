package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.i18n.LocalStrings
import com.example.ui.theme.JetBrainsMonoFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Animated 2x2 tactile keycap choreography on cold start:
 * tiles 15, 1, and 2 snap in and mechanically slide into the iconic
 * `[1, 2 / 15, _]` SlideGrid formation before dissolving into the live board.
 */
@Composable
fun SplashOverlay(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    val density = LocalDensity.current

    // Animation stage:
    // 0 = mount -> pop in tray & scrambled keycaps [15, 1 / _, 2]
    // 1 = slide 15 down  -> [_, 1 / 15, 2]
    // 2 = slide 1  left  -> [1, _ / 15, 2]
    // 3 = slide 2  up    -> [1, 2 / 15, _] (locked in!)
    var stage by remember { mutableIntStateOf(0) }
    var canTapSkip by remember { mutableStateOf(false) }
    var isSolvedLocked by remember { mutableStateOf(false) }

    val trayPop = remember { Animatable(0.84f) }
    val trayAlpha = remember { Animatable(0f) }
    val pulseProgress = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val titleOffsetY = remember { Animatable(18f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val progressAnim = remember { Animatable(0.06f) }

    LaunchedEffect(Unit) {
        launch {
            trayAlpha.animateTo(1f, tween(260, easing = FastOutSlowInEasing))
        }
        launch {
            trayPop.animateTo(
                1f,
                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)
            )
        }
        launch {
            delay(140)
            titleAlpha.animateTo(1f, tween(340, easing = FastOutSlowInEasing))
        }
        launch {
            delay(140)
            titleOffsetY.animateTo(
                0f,
                spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)
            )
        }
        launch {
            progressAnim.animateTo(1f, tween(1350, easing = FastOutSlowInEasing))
        }

        delay(260)
        canTapSkip = true
        stage = 1 // 15 slides down

        delay(250)
        stage = 2 // 1 slides left

        delay(250)
        stage = 3 // 2 slides up -> locked in!
        isSolvedLocked = true

        launch {
            subtitleAlpha.animateTo(1f, tween(280, easing = LinearEasing))
        }
        launch {
            pulseProgress.snapTo(0f)
            pulseProgress.animateTo(1f, tween(520, easing = FastOutSlowInEasing))
        }
        launch {
            trayPop.animateTo(
                1.045f,
                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessHigh)
            )
            trayPop.animateTo(
                1f,
                spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
            )
        }

        delay(680)
        onFinished()
    }

    val tile15Col = 0f
    val tile15Row = if (stage >= 1) 1f else 0f
    val tile1Col = if (stage >= 2) 0f else 1f
    val tile1Row = 0f
    val tile2Col = 1f
    val tile2Row = if (stage >= 3) 0f else 1f

    val bgColor = MaterialTheme.colorScheme.background
    val primaryColor = MaterialTheme.colorScheme.primary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val onBgColor = MaterialTheme.colorScheme.onBackground
    val surfaceColor = MaterialTheme.colorScheme.surface
    val surfaceHighColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    val dotGridColor = onBgColor.copy(alpha = 0.06f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("splash_overlay")
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = canTapSkip
            ) {
                onFinished()
            }
            .drawBehind {
                drawRect(color = bgColor)

                // Engineering studio dot matrix
                val step = 22.dp.toPx()
                val radius = 1.dp.toPx()
                var y = step / 2f
                while (y < size.height) {
                    var x = step / 2f
                    while (x < size.width) {
                        drawCircle(color = dotGridColor, radius = radius, center = Offset(x, y))
                        x += step
                    }
                    y += step
                }

                // Ambient radial studio glow behind emblem
                val center = Offset(size.width / 2f, size.height * 0.44f)
                val glowRadius = size.minDimension * 0.55f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = if (isSolvedLocked) 0.17f else 0.10f),
                            tertiaryColor.copy(alpha = 0.04f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = glowRadius
                    ),
                    radius = glowRadius,
                    center = center
                )
            }
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Tactile 2x2 Keycap Tray Emblem with radial lock-in ring
            Box(
                modifier = Modifier.size(210.dp),
                contentAlignment = Alignment.Center
            ) {
                // Expanding lock-in pulse ring
                if (pulseProgress.value > 0f && pulseProgress.value < 1f) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val p = pulseProgress.value
                        val ringRadius = (size.minDimension * 0.34f) + (size.minDimension * 0.16f * p)
                        drawCircle(
                            color = primaryColor.copy(alpha = (1f - p) * 0.55f),
                            radius = ringRadius,
                            style = Stroke(width = (3.dp.toPx() * (1f - p * 0.65f)).coerceAtLeast(1f))
                        )
                    }
                }

                val traySize = 152.dp
                val trayPadding = 12.dp
                val cellGap = 8.dp
                val tileSize = (traySize - trayPadding * 2 - cellGap) / 2f
                val stepPx = with(density) { (tileSize + cellGap).toPx() }

                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = surfaceHighColor,
                    border = BorderStroke(
                        width = 1.25.dp,
                        color = if (isSolvedLocked) primaryColor.copy(alpha = 0.55f) else outlineVariant
                    ),
                    shadowElevation = if (isSolvedLocked) 14.dp else 8.dp,
                    modifier = Modifier
                        .size(traySize)
                        .graphicsLayer {
                            scaleX = trayPop.value
                            scaleY = trayPop.value
                            alpha = trayAlpha.value
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(trayPadding)
                    ) {
                        // Recessed empty slot wells
                        for (r in 0..1) {
                            for (c in 0..1) {
                                Box(
                                    modifier = Modifier
                                        .offset {
                                            IntOffset((c * stepPx).roundToInt(), (r * stepPx).roundToInt())
                                        }
                                        .size(tileSize)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(bgColor.copy(alpha = 0.52f))
                                )
                            }
                        }

                        // Keycap 1 (Light ceramic)
                        SplashKeycapTile(
                            label = "1",
                            col = tile1Col,
                            row = tile1Row,
                            stepPx = stepPx,
                            tileSize = tileSize,
                            topColor = lerp(surfaceColor, Color.White, 0.25f),
                            bottomColor = lerp(surfaceHighColor, onBgColor, 0.08f),
                            textColor = onBgColor,
                            highlightBorder = Color.White.copy(alpha = 0.45f)
                        )

                        // Keycap 2 (Muted slate / tertiary)
                        SplashKeycapTile(
                            label = "2",
                            col = tile2Col,
                            row = tile2Row,
                            stepPx = stepPx,
                            tileSize = tileSize,
                            topColor = lerp(tertiaryColor, surfaceHighColor, 0.28f),
                            bottomColor = lerp(tertiaryColor, Color.Black, 0.28f),
                            textColor = Color(0xFFF7F5F0),
                            highlightBorder = Color.White.copy(alpha = 0.22f)
                        )

                        // Keycap 15 (Hero primary accent)
                        SplashKeycapTile(
                            label = "15",
                            col = tile15Col,
                            row = tile15Row,
                            stepPx = stepPx,
                            tileSize = tileSize,
                            topColor = primaryColor,
                            bottomColor = lerp(primaryColor, Color.Black, 0.30f),
                            textColor = MaterialTheme.colorScheme.onPrimary,
                            highlightBorder = Color.White.copy(alpha = 0.30f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Split-tone Brand Wordmark
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = onBgColor, fontWeight = FontWeight.Black)) {
                        append("Slide")
                    }
                    withStyle(SpanStyle(color = primaryColor, fontWeight = FontWeight.Black)) {
                        append("Grid")
                    }
                },
                fontSize = 36.sp,
                letterSpacing = (-0.5).sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer {
                    alpha = titleAlpha.value
                    translationY = with(density) { titleOffsetY.value.dp.toPx() }
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = strings.splashTagline,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer {
                    alpha = subtitleAlpha.value.coerceAtLeast(titleAlpha.value * 0.55f)
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Sleek Studio Progress Capsule
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(bottom = 28.dp)
                    .graphicsLayer { alpha = titleAlpha.value }
            ) {
                Box(
                    modifier = Modifier
                        .width(148.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(outlineVariant.copy(alpha = 0.45f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressAnim.value.coerceIn(0f, 1f))
                            .height(4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(primaryColor, tertiaryColor)
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "IDA*  •  3×3–10×10",
                        style = TextStyle(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.6.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SplashKeycapTile(
    label: String,
    col: Float,
    row: Float,
    stepPx: Float,
    tileSize: Dp,
    topColor: Color,
    bottomColor: Color,
    textColor: Color,
    highlightBorder: Color
) {
    val animCol by animateFloatAsState(
        targetValue = col,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "splashTileCol_$label"
    )
    val animRow by animateFloatAsState(
        targetValue = row,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "splashTileRow_$label"
    )

    val cornerShape = RoundedCornerShape(14.dp)
    Box(
        modifier = Modifier
            .offset {
                IntOffset((animCol * stepPx).roundToInt(), (animRow * stepPx).roundToInt())
            }
            .size(tileSize)
            .shadow(4.dp, cornerShape, ambientColor = Color.Black.copy(alpha = 0.25f))
            .clip(cornerShape)
            .background(bottomColor)
            .padding(bottom = 4.dp) // Physical 3D keycap lip
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        topColor,
                        lerp(topColor, bottomColor, 0.22f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Subtle top bevel catchlight
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(1.dp)
                .clip(RoundedCornerShape(11.dp))
                .drawBehind {
                    drawLine(
                        color = highlightBorder,
                        start = Offset(6.dp.toPx(), 1.dp.toPx()),
                        end = Offset(size.width - 6.dp.toPx(), 1.dp.toPx()),
                        strokeWidth = 1.5.dp.toPx()
                    )
                }
        )

        Text(
            text = label,
            style = TextStyle(
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold
            ),
            color = textColor
        )
    }
}
