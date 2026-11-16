package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.BoardRecord
import com.example.data.SolveStats
import com.example.i18n.LocalStrings
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.viewmodel.BoardViewModel

fun formatTimeMMSS(totalSeconds: Long): String {
    val mins = totalSeconds / 60
    val secs = totalSeconds % 60
    return "%02d:%02d".format(mins, secs)
}

@Composable
fun GameTelemetryStrip(
    moveCount: Int,
    elapsedSeconds: Long,
    correctTiles: Int,
    totalTiles: Int,
    bestRecord: BoardRecord?,
    canUndo: Boolean,
    onUndo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    val progress = (correctTiles.toFloat() / totalTiles.coerceAtLeast(1)).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "hudProgress"
    )
    val progressColor by animateColorAsState(
        targetValue = if (progress >= 0.999f) Color(0xFF4E7A61) else MaterialTheme.colorScheme.primary,
        animationSpec = tween(350),
        label = "hudProgressColor"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 560.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 0.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OdometerMetric(
                    label = strings.movesLabel,
                    numericValue = moveCount,
                    displayValue = "$moveCount",
                    subLabel = bestRecord?.let { "${strings.recordPrefix} ${it.bestMoves}" },
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(28.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                )
                TelemetryMetric(
                    label = strings.timeLabel,
                    value = formatTimeMMSS(elapsedSeconds),
                    subLabel = bestRecord?.let { formatTimeMMSS(it.bestTimeSeconds) },
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(28.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                )
                OdometerMetric(
                    label = strings.inPlaceLabel,
                    numericValue = correctTiles,
                    displayValue = "$correctTiles/$totalTiles",
                    subLabel = if (totalTiles > 0) "${(correctTiles * 100 / totalTiles)}%" else "0%",
                    highlightColor = if (progress >= 0.999f) Color(0xFF4E7A61) else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                FilledTonalIconButton(
                    onClick = onUndo,
                    enabled = canUndo,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("undo_button"),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = strings.undoMoveDesc,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Clean architectural completion bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .background(progressColor)
                )
            }
        }
    }
}

@Composable
private fun OdometerMetric(
    label: String,
    numericValue: Int,
    displayValue: String,
    subLabel: String?,
    highlightColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
        AnimatedContent(
            targetState = numericValue to displayValue,
            transitionSpec = {
                val up = targetState.first >= initialState.first
                if (up) {
                    (slideInVertically(tween(190)) { it / 2 } + fadeIn(tween(150)))
                        .togetherWith(slideOutVertically(tween(160)) { -it / 2 } + fadeOut(tween(120)))
                } else {
                    (slideInVertically(tween(190)) { -it / 2 } + fadeIn(tween(150)))
                        .togetherWith(slideOutVertically(tween(160)) { it / 2 } + fadeOut(tween(120)))
                }
            },
            label = "odometer_$label"
        ) { (_, text) ->
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFeatureSettings = "tnum"
                ),
                color = highlightColor,
                maxLines = 1
            )
        }
        if (subLabel != null) {
            Text(
                text = subLabel,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun TelemetryMetric(
    label: String,
    value: String,
    subLabel: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
        Text(
            text = value,
            style = TextStyle(
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFeatureSettings = "tnum"
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
        if (subLabel != null) {
            Text(
                text = subLabel,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                maxLines = 1
            )
        }
    }
}

@Composable
fun GameStatusBadge(
    isSolving: Boolean,
    isBoardSolved: Boolean,
    hasSolution: Boolean,
    solveStats: SolveStats?,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = when {
                isSolving -> "solving"
                isBoardSolved -> "solved"
                hasSolution -> "solution"
                else -> "idle"
            },
            transitionSpec = {
                (fadeIn(animationSpec = tween(220)) + scaleIn(initialScale = 0.88f))
                    .togetherWith(fadeOut(animationSpec = tween(160)) + scaleOut(targetScale = 1.08f))
            },
            label = "statusBadge"
        ) { status ->
            when (status) {
                "solving" -> {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = strings.statusAnalyzing,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                "solved" -> {
                    val sageAccent = Color(0xFF4E7A61)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = sageAccent.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, sageAccent.copy(alpha = 0.45f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = sageAccent,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = strings.statusSolved,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = sageAccent
                            )
                        }
                    }
                }
                "solution" -> {
                    val info = solveStats?.let { "${it.algorithmLabel} • ${it.totalSteps} ${strings.stepsShort} • ${it.elapsedMs} ${strings.msShort}" }
                        ?: strings.solutionPlayerFallback
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Text(
                                text = info,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
                else -> {
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }
        }
    }
}

@Composable
fun GameActionDeck(
    viewModel: BoardViewModel,
    isSolving: Boolean,
    isBoardSolved: Boolean,
    solutionPath: List<Int>?,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    val deckMode = when {
        isSolving -> 0
        solutionPath != null -> 1
        else -> 2
    }

    AnimatedContent(
        targetState = deckMode,
        transitionSpec = {
            (fadeIn(tween(220)) + slideInVertically(tween(240)) { it / 6 } + scaleIn(initialScale = 0.96f))
                .togetherWith(fadeOut(tween(160)) + slideOutVertically(tween(180)) { -it / 8 } + scaleOut(targetScale = 0.96f))
        },
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 560.dp),
        label = "actionDeckTransition"
    ) { mode ->
        when (mode) {
            0 -> {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = strings.solvingStatus,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            1 -> {
                val steps = solutionPath?.size ?: 0
                SolutionPlayerDeck(
                    viewModel = viewModel,
                    totalSteps = steps
                )
            }
            else -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilledTonalButton(
                        onClick = { viewModel.openSetup() },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("custom_board_button"),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_board_setup),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.customBoardButton,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (isBoardSolved) {
                        Button(
                            onClick = { viewModel.shuffle() },
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .testTag("play_shuffle_button"),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.shuffleButton,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    } else {
                        Button(
                            onClick = { viewModel.solve() },
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .testTag("solve_button"),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.solveButton,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SolutionPlayerDeck(
    viewModel: BoardViewModel,
    totalSteps: Int
) {
    val strings = LocalStrings.current
    val currentStep by viewModel.currentSolutionStep.collectAsState()
    val isAutoPlaying by viewModel.isAutoPlaying.collectAsState()
    val autoPlaySpeed by viewModel.autoPlaySpeed.collectAsState()

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${strings.stepLabel} ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    AnimatedContent(
                        targetState = currentStep,
                        transitionSpec = {
                            if (targetState > initialState) {
                                (slideInVertically { it } + fadeIn()).togetherWith(slideOutVertically { -it } + fadeOut())
                            } else {
                                (slideInVertically { -it } + fadeIn()).togetherWith(slideOutVertically { it } + fadeOut())
                            }
                        },
                        label = "stepOdometer"
                    ) { step ->
                        Text(
                            text = "$step",
                            style = TextStyle(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                fontFeatureSettings = "tnum"
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = " / $totalSteps",
                        style = TextStyle(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        onClick = { viewModel.cycleAutoPlaySpeed() },
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.heightIn(min = 36.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "${autoPlaySpeed}×",
                                style = TextStyle(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Surface(
                        onClick = { viewModel.jumpToEndOfSolution() },
                        enabled = currentStep < totalSteps,
                        shape = RoundedCornerShape(14.dp),
                        color = if (currentStep < totalSteps) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.heightIn(min = 36.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FastForward,
                                contentDescription = strings.fastForward,
                                modifier = Modifier.size(15.dp),
                                tint = if (currentStep < totalSteps) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                            Text(
                                text = strings.fastForward,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (currentStep < totalSteps) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }

            // Shimmering Progress Bar
            val progress = (currentStep.toFloat() / totalSteps.coerceAtLeast(1)).coerceIn(0f, 1f)
            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "solutionProgress"
            )
            val shimmerTransition = rememberInfiniteTransition(label = "shimmerTransition")
            val shimmerOffset by shimmerTransition.animateFloat(
                initialValue = -200f,
                targetValue = 600f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "shimmerOffset"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .height(7.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
                if (isAutoPlaying && animatedProgress > 0.05f) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgress)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.45f), Color.Transparent),
                                    start = Offset(shimmerOffset, 0f),
                                    end = Offset(shimmerOffset + 120f, 0f)
                                )
                            )
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { viewModel.clearSolution() },
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Text(
                        text = strings.exitSolution,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.previousSolutionStep() },
                        enabled = currentStep > 0 && !isAutoPlaying,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (currentStep > 0 && !isAutoPlaying) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = strings.prevStepDesc
                        )
                    }

                    val pulseTransition = rememberInfiniteTransition(label = "autoPlayPulse")
                    val pulseScale by pulseTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.32f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(900, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "pulseScale"
                    )
                    val pulseAlpha by pulseTransition.animateFloat(
                        initialValue = 0.45f,
                        targetValue = 0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(900, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "pulseAlpha"
                    )

                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(60.dp)) {
                        if (isAutoPlaying) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .graphicsLayer {
                                        scaleX = pulseScale
                                        scaleY = pulseScale
                                    }
                                    .background(MaterialTheme.colorScheme.error.copy(alpha = pulseAlpha), CircleShape)
                            )
                        }
                        IconButton(
                            onClick = { viewModel.toggleAutoPlay() },
                            enabled = currentStep < totalSteps || isAutoPlaying,
                            modifier = Modifier
                                .size(54.dp)
                                .background(
                                    if (isAutoPlaying) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = if (isAutoPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isAutoPlaying) strings.pauseDesc else strings.playDesc,
                                modifier = Modifier.size(28.dp),
                                tint = if (isAutoPlaying) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.nextSolutionStep() },
                        enabled = currentStep < totalSteps && !isAutoPlaying,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (currentStep < totalSteps && !isAutoPlaying) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = strings.nextStepDesc
                        )
                    }
                }

                Spacer(modifier = Modifier.width(64.dp))
            }
        }
    }
}

@Composable
fun GridSizeDialog(viewModel: BoardViewModel) {
    val strings = LocalStrings.current
    val currentRows by viewModel.gridRows.collectAsState()
    val currentCols by viewModel.gridCols.collectAsState()

    var selectedRows by remember { mutableIntStateOf(currentRows) }
    var selectedCols by remember { mutableIntStateOf(currentCols) }

    Dialog(onDismissRequest = { viewModel.closeGridDialog() }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = strings.gridSizeTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(6.dp))

                AnimatedContent(
                    targetState = "${selectedRows} × ${selectedCols} (${selectedRows * selectedCols} ${strings.cellsLabel})",
                    transitionSpec = {
                        (scaleIn(initialScale = 0.9f) + fadeIn()).togetherWith(scaleOut(targetScale = 1.1f) + fadeOut())
                    },
                    label = "sizeText"
                ) { text ->
                    Text(
                        text = text,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                val solverHint = if (selectedRows * selectedCols <= 16) {
                    strings.solverHintIda
                } else {
                    strings.solverHintConstructive
                }
                Text(
                    text = solverHint,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                val presets = listOf(3 to 3, 4 to 4, 5 to 5, 6 to 6, 8 to 8, 10 to 10)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presets.forEach { (r, c) ->
                        val isPresetActive = selectedRows == r && selectedCols == c
                        Surface(
                            onClick = {
                                selectedRows = r
                                selectedCols = c
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isPresetActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.heightIn(min = 40.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${r}×${c}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPresetActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .size(236.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (r in 1..10) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                for (c in 1..10) {
                                    val isSelected = r <= selectedRows && c <= selectedCols
                                    val cellColor by animateColorAsState(
                                        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                        animationSpec = tween(200),
                                        label = "matrix_cell"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .weight(1f)
                                            .clip(CircleShape)
                                            .background(cellColor)
                                            .clickable {
                                                selectedRows = r.coerceAtLeast(2)
                                                selectedCols = c.coerceAtLeast(2)
                                            }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(strings.rowsLabel, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { if (selectedRows > 2) selectedRows-- }) {
                                Icon(Icons.Default.Remove, contentDescription = strings.decreaseRowsDesc)
                            }
                            Text(
                                "$selectedRows",
                                style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            )
                            IconButton(onClick = { if (selectedRows < 10) selectedRows++ }) {
                                Icon(Icons.Default.Add, contentDescription = strings.increaseRowsDesc)
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(strings.colsLabel, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { if (selectedCols > 2) selectedCols-- }) {
                                Icon(Icons.Default.Remove, contentDescription = strings.decreaseColsDesc)
                            }
                            Text(
                                "$selectedCols",
                                style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            )
                            IconButton(onClick = { if (selectedCols < 10) selectedCols++ }) {
                                Icon(Icons.Default.Add, contentDescription = strings.increaseColsDesc)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.setGridSize(selectedRows, selectedCols) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(strings.apply, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun VictoryDialog(
    gridRows: Int,
    gridCols: Int,
    moveCount: Int,
    elapsedSeconds: Long,
    isNewRecord: Boolean,
    onPlayAgain: () -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalStrings.current
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = strings.victoryTitle,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = strings.gridBadgeFormat(gridRows, gridCols),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )

                if (isNewRecord) {
                    Spacer(modifier = Modifier.height(10.dp))
                    val ochre = Color(0xFFC98E38)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ochre.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, ochre.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = strings.newRecordBadge,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ochre,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(strings.movesStatLabel, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "$moveCount",
                                style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 22.sp, fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(strings.timeLabel, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = formatTimeMMSS(elapsedSeconds),
                                style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 22.sp, fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.playAgain, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(strings.viewBoard, fontSize = 14.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesBottomSheet(onDismiss: () -> Unit) {
    val strings = LocalStrings.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = strings.rulesSheetTitle,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            RuleItem(
                icon = Icons.Default.TouchApp,
                title = strings.ruleTouchTitle,
                description = strings.ruleTouchDesc
            )
            RuleItem(
                icon = Icons.Default.AutoAwesome,
                title = strings.ruleSolverTitle,
                description = strings.ruleSolverDesc
            )
            RuleItem(
                icon = Icons.Default.Timer,
                title = strings.ruleRecordsTitle,
                description = strings.ruleRecordsDesc
            )
        }
    }
}

@Composable
private fun RuleItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text(description, fontSize = 13.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
