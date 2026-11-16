package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.data.CornerRadiusStyle
import com.example.data.NumberFormat
import com.example.data.SpringAnimationSpeed
import com.example.data.ThemeMode
import com.example.data.TileColorScheme
import com.example.data.TileStyleHelper
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.viewmodel.BoardViewModel
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

private data class ActiveDragSlide(
    val sourceTile: Int,
    val affectedTiles: Set<Int>,
    val dirX: Int, // -1, 0, +1
    val dirY: Int, // -1, 0, +1
    val progressPx: Float
)

@Composable
fun PuzzleBoard(
    viewModel: BoardViewModel,
    board: List<Int>,
    isGathering: Boolean,
    isShuffling: Boolean,
    onTileClick: (Int) -> Unit,
    onTileSwipe: (Int, Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val gridRows by viewModel.gridRows.collectAsState()
    val gridCols by viewModel.gridCols.collectAsState()
    val lastMovedTile by viewModel.lastMovedTile.collectAsState()
    val solutionPath by viewModel.solutionPath.collectAsState()
    val currentStep by viewModel.currentSolutionStep.collectAsState()
    val isBoardSolved by viewModel.isBoardSolved.collectAsState()

    val tileColorScheme by viewModel.tileColorScheme.collectAsState()
    val numberFormat by viewModel.numberFormat.collectAsState()
    val cornerRadiusStyle by viewModel.cornerRadiusStyle.collectAsState()
    val springAnimation by viewModel.springAnimation.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val highlightCorrectTiles by viewModel.highlightCorrectTiles.collectAsState()
    val swipeGesturesEnabled by viewModel.swipeGesturesEnabled.collectAsState()
    val multiTileMove by viewModel.multiTileMove.collectAsState()

    val hintTile = remember(solutionPath, currentStep) {
        val path = solutionPath
        if (path != null && currentStep in path.indices) path[currentStep] else null
    }

    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK, ThemeMode.AMOLED -> true
    }

    val boardScale by animateFloatAsState(
        targetValue = if (isGathering) 0.94f else if (isShuffling) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "boardScale"
    )

    var activeDrag by remember { mutableStateOf<ActiveDragSlide?>(null) }

    // Clear any lingering drag when board changes
    LaunchedEffect(board, isGathering, isShuffling) {
        activeDrag = null
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .graphicsLayer {
                scaleX = boardScale
                scaleY = boardScale
            },
        contentAlignment = Alignment.Center
    ) {
        val spacing = when {
            gridCols >= 9 || gridRows >= 9 -> 2.dp
            gridCols >= 7 || gridRows >= 7 -> 3.dp
            else -> 5.dp
        }
        val gridPadding = when {
            gridCols >= 8 || gridRows >= 8 -> 8.dp
            else -> 12.dp
        }

        val maxAvailableW = (maxWidth - gridPadding * 2).coerceAtLeast(0.dp)
        val maxAvailableH = (maxHeight - gridPadding * 2).coerceAtLeast(0.dp)

        val cellW = (maxAvailableW - spacing * (gridCols - 1)) / gridCols
        val cellH = (maxAvailableH - spacing * (gridRows - 1)) / gridRows

        val cellSize = minOf(cellW, cellH).coerceAtLeast(14.dp)
        val gridWidth = cellSize * gridCols + spacing * (gridCols - 1)
        val gridHeight = cellSize * gridRows + spacing * (gridRows - 1)

        val totalBoardW = gridWidth + gridPadding * 2
        val totalBoardH = gridHeight + gridPadding * 2

        val density = LocalDensity.current
        val cellStepPx = with(density) { (cellSize + spacing).toPx() }.coerceAtLeast(1f)

        val baseRadius = cornerRadiusStyle.radiusDp
        val cornerRadius = when {
            gridCols >= 9 || cellSize < 32.dp -> minOf(baseRadius, 4.dp)
            gridCols >= 7 || cellSize < 44.dp -> minOf(baseRadius, 7.dp)
            gridCols >= 5 || cellSize < 60.dp -> minOf(baseRadius, 11.dp)
            else -> baseRadius
        }
        val boardContainerRadius = (cornerRadius * 1.8f).coerceAtLeast(14.dp)

        Box(
            modifier = Modifier
                .size(width = totalBoardW, height = totalBoardH)
                .testTag("puzzle_board")
        ) {
            // Board tray frame — machined matte chassis
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(
                        elevation = if (isDark) 6.dp else 3.dp,
                        shape = RoundedCornerShape(boardContainerRadius),
                        ambientColor = Color.Black.copy(alpha = 0.25f)
                    )
                    .clip(RoundedCornerShape(boardContainerRadius))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(boardContainerRadius)
                    )
            ) {
                for (r in 0 until gridRows) {
                    for (c in 0 until gridCols) {
                        Box(
                            modifier = Modifier
                                .offset(
                                    x = gridPadding + (cellSize + spacing) * c,
                                    y = gridPadding + (cellSize + spacing) * r
                                )
                                .size(cellSize)
                                .clip(RoundedCornerShape(cornerRadius))
                                .background(MaterialTheme.colorScheme.background.copy(alpha = if (isDark) 0.55f else 0.65f))
                                .border(
                                    width = if (gridCols >= 8) 0.75.dp else 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f),
                                    shape = RoundedCornerShape(cornerRadius)
                                )
                        )
                    }
                }
            }

            // Central hub for gathering/shuffling choreography
            val dispenserScale by animateFloatAsState(
                targetValue = if (isShuffling || isGathering) 1.25f else 0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "dispenserScale"
            )
            val centerX = gridPadding + (gridWidth - cellSize) / 2
            val centerY = gridPadding + (gridHeight - cellSize) / 2

            if (dispenserScale > 0.01f) {
                val haloTransition = rememberInfiniteTransition(label = "hubHalo")
                val haloScale by haloTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.75f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(650, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "haloScale"
                )
                val haloAlpha by haloTransition.animateFloat(
                    initialValue = 0.45f,
                    targetValue = 0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(650, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "haloAlpha"
                )

                Box(
                    modifier = Modifier
                        .offset(x = centerX, y = centerY)
                        .size(cellSize.coerceAtLeast(40.dp))
                        .graphicsLayer {
                            scaleX = dispenserScale * haloScale
                            scaleY = dispenserScale * haloScale
                        }
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = haloAlpha))
                )

                Box(
                    modifier = Modifier
                        .offset(x = centerX, y = centerY)
                        .size(cellSize.coerceAtLeast(40.dp))
                        .graphicsLayer {
                            scaleX = dispenserScale
                            scaleY = dispenserScale
                        }
                        .zIndex(5f)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    val rotation by animateFloatAsState(
                        targetValue = if (isShuffling) 360f * 4 else 0f,
                        animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing),
                        label = "rotation"
                    )
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier
                            .size(cellSize.coerceAtLeast(40.dp) * 0.5f)
                            .rotate(rotation),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Tiles
            val totalCells = gridRows * gridCols
            val totalTiles = totalCells - 1
            val emptyIdx = board.indexOf(0)
            val emptyR = if (emptyIdx != -1) emptyIdx / gridCols else -1
            val emptyC = if (emptyIdx != -1) emptyIdx % gridCols else -1

            val tiles = (1..totalTiles).toList()
            tiles.forEach { tileValue ->
                val index = board.indexOf(tileValue)
                if (index != -1) {
                    val targetRow = index / gridCols
                    val targetCol = index % gridCols

                    val gridX = gridPadding + (cellSize + spacing) * targetCol
                    val gridY = gridPadding + (cellSize + spacing) * targetRow

                    val targetX = if (isGathering) centerX else gridX
                    val targetY = if (isGathering) centerY else gridY

                    val animatedX by animateDpAsState(
                        targetValue = targetX,
                        animationSpec = spring(
                            dampingRatio = springAnimation.damping,
                            stiffness = springAnimation.stiffness
                        ),
                        label = "x_$tileValue"
                    )
                    val animatedY by animateDpAsState(
                        targetValue = targetY,
                        animationSpec = spring(
                            dampingRatio = springAnimation.damping,
                            stiffness = springAnimation.stiffness
                        ),
                        label = "y_$tileValue"
                    )

                    val gatherRotation by animateFloatAsState(
                        targetValue = if (isGathering) ((tileValue % 5) - 2) * 11f else 0f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "gatherRot_$tileValue"
                    )
                    val gatherScale by animateFloatAsState(
                        targetValue = if (isGathering) 0.72f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "gatherScale_$tileValue"
                    )

                    val currentDrag = activeDrag
                    val isBeingDragged = currentDrag != null && tileValue in currentDrag.affectedTiles
                    val dragShiftX = if (isBeingDragged && currentDrag != null) currentDrag.dirX * currentDrag.progressPx else 0f
                    val dragShiftY = if (isBeingDragged && currentDrag != null) currentDrag.dirY * currentDrag.progressPx else 0f

                    // Compute slide group for continuous finger drag
                    val slideInfo = remember(index, emptyIdx, multiTileMove, board) {
                        if (emptyIdx == -1 || isGathering || isShuffling) null
                        else if (targetRow == emptyR) {
                            val distCols = kotlin.math.abs(emptyC - targetCol)
                            if (!multiTileMove && distCols != 1) null
                            else {
                                val dirX = if (emptyC > targetCol) 1 else -1
                                val group = mutableSetOf<Int>()
                                var c = targetCol
                                while (c != emptyC) {
                                    group.add(board[targetRow * gridCols + c])
                                    c += dirX
                                }
                                Triple(group, dirX, 0)
                            }
                        } else if (targetCol == emptyC) {
                            val distRows = kotlin.math.abs(emptyR - targetRow)
                            if (!multiTileMove && distRows != 1) null
                            else {
                                val dirY = if (emptyR > targetRow) 1 else -1
                                val group = mutableSetOf<Int>()
                                var r = targetRow
                                while (r != emptyR) {
                                    group.add(board[r * gridCols + targetCol])
                                    r += dirY
                                }
                                Triple(group, 0, dirY)
                            }
                        } else null
                    }

                    val dragModifier = if (swipeGesturesEnabled && slideInfo != null) {
                        Modifier.pointerInput(tileValue, index, emptyIdx, cellStepPx, slideInfo) {
                            var accumX = 0f
                            var accumY = 0f
                            detectDragGestures(
                                onDragStart = {
                                    accumX = 0f
                                    accumY = 0f
                                    activeDrag = ActiveDragSlide(
                                        sourceTile = tileValue,
                                        affectedTiles = slideInfo.first,
                                        dirX = slideInfo.second,
                                        dirY = slideInfo.third,
                                        progressPx = 0f
                                    )
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    accumX += dragAmount.x
                                    accumY += dragAmount.y
                                    val projected = (accumX * slideInfo.second + accumY * slideInfo.third)
                                        .coerceIn(0f, cellStepPx)
                                    activeDrag = ActiveDragSlide(
                                        sourceTile = tileValue,
                                        affectedTiles = slideInfo.first,
                                        dirX = slideInfo.second,
                                        dirY = slideInfo.third,
                                        progressPx = projected
                                    )
                                },
                                onDragEnd = {
                                    val endedDrag = activeDrag
                                    activeDrag = null
                                    if (endedDrag != null && endedDrag.progressPx > cellStepPx * 0.28f) {
                                        onTileClick(tileValue)
                                    }
                                },
                                onDragCancel = {
                                    activeDrag = null
                                }
                            )
                        }
                    } else if (swipeGesturesEnabled) {
                        Modifier.pointerInput(tileValue) {
                            var dx = 0f
                            var dy = 0f
                            var sent = false
                            detectDragGestures(
                                onDragStart = { dx = 0f; dy = 0f; sent = false },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    if (!sent) {
                                        dx += dragAmount.x
                                        dy += dragAmount.y
                                        val thr = 18.dp.toPx()
                                        if (kotlin.math.abs(dx) > thr || kotlin.math.abs(dy) > thr) {
                                            sent = true
                                            onTileSwipe(tileValue, dx, dy)
                                        }
                                    }
                                }
                            )
                        }
                    } else {
                        Modifier
                    }

                    Tile(
                        value = tileValue,
                        totalTiles = totalTiles,
                        index = index,
                        gridRows = gridRows,
                        gridCols = gridCols,
                        cellSize = cellSize,
                        cornerRadius = cornerRadius,
                        tileColorScheme = tileColorScheme,
                        numberFormat = numberFormat,
                        springAnimation = springAnimation,
                        isDark = isDark,
                        isLastMoved = (tileValue == lastMovedTile),
                        isHintTarget = (tileValue == hintTile),
                        isBeingDragged = isBeingDragged,
                        isBoardSolved = isBoardSolved && !isGathering && !isShuffling,
                        highlightCorrectTiles = highlightCorrectTiles,
                        onClick = { onTileClick(tileValue) },
                        modifier = Modifier
                            .offset(x = animatedX, y = animatedY)
                            .size(cellSize)
                            .zIndex(if (isBeingDragged) 3f else 1f)
                            .graphicsLayer {
                                translationX = dragShiftX
                                translationY = dragShiftY
                                rotationZ = gatherRotation
                                scaleX = gatherScale
                                scaleY = gatherScale
                            }
                            .then(dragModifier)
                    )
                }
            }
        }
    }
}

@Composable
fun Tile(
    value: Int,
    totalTiles: Int,
    index: Int,
    gridRows: Int,
    gridCols: Int,
    cellSize: Dp,
    cornerRadius: Dp,
    tileColorScheme: TileColorScheme,
    numberFormat: NumberFormat,
    springAnimation: SpringAnimationSpeed,
    isDark: Boolean,
    isLastMoved: Boolean,
    isHintTarget: Boolean,
    isBeingDragged: Boolean,
    isBoardSolved: Boolean,
    highlightCorrectTiles: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val targetRow = (value - 1) / gridCols
    val targetCol = (value - 1) % gridCols
    val currentRow = index / gridCols
    val currentCol = index % gridCols
    val dist = kotlin.math.abs(targetRow - currentRow) + kotlin.math.abs(targetCol - currentCol)

    val (targetContainerColor, targetContentColor) = TileStyleHelper.getColors(
        value = value,
        totalTiles = totalTiles,
        dist = dist,
        gridCols = gridCols,
        scheme = tileColorScheme,
        isDark = isDark
    )

    val animatedContainerColor by animateColorAsState(
        targetValue = targetContainerColor,
        animationSpec = tween(280, easing = FastOutSlowInEasing),
        label = "tileBgColor"
    )
    val animatedContentColor by animateColorAsState(
        targetValue = targetContentColor,
        animationSpec = tween(280, easing = FastOutSlowInEasing),
        label = "tileTextColor"
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressScale by animateFloatAsState(
        targetValue = when {
            isBeingDragged -> 1.045f
            isPressed -> 0.95f
            isLastMoved && gridCols <= 6 -> 1.02f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = springAnimation.damping, stiffness = springAnimation.stiffness),
        label = "pressScale"
    )

    // Victory diagonal wave animation
    val waveScale = remember { Animatable(1f) }
    LaunchedEffect(isBoardSolved) {
        if (isBoardSolved) {
            val staggerMs = ((currentRow + currentCol) * 42L).coerceAtMost(480L)
            delay(staggerMs)
            waveScale.animateTo(
                targetValue = if (gridCols <= 6) 1.09f else 1.05f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
            )
            waveScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)
            )
        } else {
            waveScale.snapTo(1f)
        }
    }

    val hintPulse = if (isHintTarget) {
        val inf = rememberInfiniteTransition(label = "hintPulse")
        val p by inf.animateFloat(
            initialValue = 1f,
            targetValue = 1.06f,
            animationSpec = infiniteRepeatable(
                animation = tween(620, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "hintScale"
        )
        p
    } else 1f

    val elevation by animateDpAsState(
        targetValue = when {
            isBeingDragged -> 10.dp
            isPressed -> 0.5.dp
            dist == 0 -> 1.5.dp
            else -> 3.dp
        },
        animationSpec = tween(180),
        label = "tileElevation"
    )

    val text = TileStyleHelper.formatNumber(value, numberFormat, gridCols)
    val fontSize = when {
        text.length <= 1 -> (cellSize.value * 0.48f).coerceIn(10f, 30f).sp
        text.length <= 2 -> (cellSize.value * 0.38f).coerceIn(8f, 22f).sp
        text.length <= 4 -> (cellSize.value * 0.27f).coerceIn(7f, 16f).sp
        else -> (cellSize.value * 0.21f).coerceIn(6f, 13f).sp
    }

    // Physical keycap skirt/bevel color (naturally darkened base)
    val skirtColor = remember(animatedContainerColor) {
        Color(
            red = (animatedContainerColor.red * 0.72f).coerceIn(0f, 1f),
            green = (animatedContainerColor.green * 0.72f).coerceIn(0f, 1f),
            blue = (animatedContainerColor.blue * 0.72f).coerceIn(0f, 1f),
            alpha = 1f
        )
    }
    val topCapRadius = (cornerRadius - 1.dp).coerceAtLeast(3.dp)
    val skirtBottomDp = when {
        gridCols >= 8 -> 1.5.dp
        isPressed -> 1.dp
        else -> 3.dp
    }
    val skirtTopDp = if (isPressed && gridCols < 8) 2.dp else 0.dp

    val borderColor = when {
        isHintTarget -> MaterialTheme.colorScheme.primary
        isBeingDragged -> MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
        else -> skirtColor.copy(alpha = 0.85f)
    }

    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = pressScale * hintPulse * waveScale.value
                scaleY = pressScale * hintPulse * waveScale.value
            }
            .shadow(elevation, RoundedCornerShape(cornerRadius))
            .clip(RoundedCornerShape(cornerRadius))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("tile_$value"),
        shape = RoundedCornerShape(cornerRadius),
        color = skirtColor,
        border = BorderStroke(
            width = if (isHintTarget || isBeingDragged) 2.dp else if (gridCols >= 8) 0.75.dp else 1.dp,
            color = borderColor
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = skirtTopDp, bottom = skirtBottomDp, start = 1.dp, end = 1.dp)
                .clip(RoundedCornerShape(topCapRadius))
                .background(animatedContainerColor)
                .border(
                    width = 0.75.dp,
                    color = Color.White.copy(alpha = if (isDark) 0.08f else 0.45f),
                    shape = RoundedCornerShape(topCapRadius)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = animatedContentColor,
                style = TextStyle(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    fontFeatureSettings = "tnum"
                ),
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Clip
            )

            if (highlightCorrectTiles && dist == 0 && gridCols <= 7) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding((cellSize * 0.10f).coerceAtLeast(3.dp))
                        .size((cellSize * 0.09f).coerceIn(4.dp, 6.dp))
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.85f))
                )
            }
        }
    }
}

@Composable
fun MiniBoardPreview(
    board: List<Int>,
    tileColorScheme: TileColorScheme,
    numberFormat: NumberFormat,
    cornerRadiusStyle: CornerRadiusStyle,
    springAnimation: SpringAnimationSpeed,
    highlightCorrectTiles: Boolean,
    isDark: Boolean,
    onTileClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val gridCols = 3
    val cellSize = 44.dp
    val spacing = 4.dp
    val padding = 8.dp
    val totalSide = cellSize * 3 + spacing * 2 + padding * 2
    val corner = minOf(cornerRadiusStyle.radiusDp, 12.dp)

    Box(
        modifier = modifier
            .size(totalSide)
            .clip(RoundedCornerShape((corner * 1.5f).coerceAtLeast(10.dp)))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
                RoundedCornerShape((corner * 1.5f).coerceAtLeast(10.dp))
            )
    ) {
        for (r in 0 until 3) {
            for (c in 0 until 3) {
                Box(
                    modifier = Modifier
                        .offset(
                            x = padding + (cellSize + spacing) * c,
                            y = padding + (cellSize + spacing) * r
                        )
                        .size(cellSize)
                        .clip(RoundedCornerShape(corner))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.55f))
                )
            }
        }
        (1..8).forEach { value ->
            val idx = board.indexOf(value)
            if (idx != -1) {
                val r = idx / gridCols
                val col = idx % gridCols
                val ax by animateDpAsState(
                    targetValue = padding + (cellSize + spacing) * col,
                    animationSpec = spring(dampingRatio = springAnimation.damping, stiffness = springAnimation.stiffness),
                    label = "mini_x_$value"
                )
                val ay by animateDpAsState(
                    targetValue = padding + (cellSize + spacing) * r,
                    animationSpec = spring(dampingRatio = springAnimation.damping, stiffness = springAnimation.stiffness),
                    label = "mini_y_$value"
                )
                Tile(
                    value = value,
                    totalTiles = 8,
                    index = idx,
                    gridRows = 3,
                    gridCols = gridCols,
                    cellSize = cellSize,
                    cornerRadius = corner,
                    tileColorScheme = tileColorScheme,
                    numberFormat = numberFormat,
                    springAnimation = springAnimation,
                    isDark = isDark,
                    isLastMoved = false,
                    isHintTarget = false,
                    isBeingDragged = false,
                    isBoardSolved = false,
                    highlightCorrectTiles = highlightCorrectTiles,
                    onClick = { onTileClick(value) },
                    modifier = Modifier
                        .offset(x = ax, y = ay)
                        .size(cellSize)
                )
            }
        }
    }
}

private data class ConfettiParticle(
    val xFraction: Float,
    val speed: Float,
    val angle: Float,
    val sizeDp: Float,
    val color: Color,
    val phase: Float
)

@Composable
fun VictoryConfettiOverlay(
    primaryColor: Color,
    tertiaryColor: Color,
    modifier: Modifier = Modifier
) {
    val particles = remember(primaryColor, tertiaryColor) {
        val palette = listOf(
            primaryColor,
            tertiaryColor,
            Color(0xFF4E7A61),
            Color(0xFFDF9F42),
            Color(0xFFC85032),
            Color(0xFF2D4559)
        )
        List(44) { idx ->
            val rnd = Random(idx * 97 + 13)
            ConfettiParticle(
                xFraction = rnd.nextFloat(),
                speed = 0.55f + rnd.nextFloat() * 0.9f,
                angle = (rnd.nextFloat() - 0.5f) * 0.6f,
                sizeDp = 5f + rnd.nextFloat() * 6f,
                color = palette[idx % palette.size],
                phase = rnd.nextFloat() * 2f * PI.toFloat()
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "confetti")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confettiProgress"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { p ->
            val yFrac = ((progress * p.speed + p.phase / (2f * PI.toFloat())) % 1.15f) - 0.1f
            val xDrift = sin(progress * 6f + p.phase) * 26f + p.angle * 90f * yFrac
            val cx = p.xFraction * size.width + xDrift
            val cy = yFrac * size.height
            val alpha = (1f - (yFrac.coerceIn(0.6f, 1.1f) - 0.6f) * 2f).coerceIn(0f, 0.85f)
            drawCircle(
                color = p.color.copy(alpha = alpha),
                radius = p.sizeDp.dp.toPx() / 2f,
                center = Offset(cx, cy),
            )
        }
    }
}
