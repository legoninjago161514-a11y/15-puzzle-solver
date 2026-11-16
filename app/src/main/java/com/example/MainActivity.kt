package com.example

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.os.Bundle
import android.view.SoundEffectConstants
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.HapticMode
import com.example.i18n.LocalStrings
import com.example.i18n.StringsProvider
import com.example.ui.components.GameActionDeck
import com.example.ui.components.GameStatusBadge
import com.example.ui.components.GameTelemetryStrip
import com.example.ui.components.GridSizeDialog
import com.example.ui.components.PuzzleBoard
import com.example.ui.components.RulesBottomSheet
import com.example.ui.components.VictoryConfettiOverlay
import com.example.ui.components.VictoryDialog
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SetupScreen
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.BoardViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel = ViewModelProvider(this)[BoardViewModel::class.java]
        splashScreen.setKeepOnScreenCondition { !viewModel.isInitialSettingsLoaded.value }
        splashScreen.setOnExitAnimationListener { splashProvider ->
            val animDuration = splashProvider.iconAnimationDurationMillis
            val animStart = splashProvider.iconAnimationStartMillis
            val elapsed = if (animStart > 0L) System.currentTimeMillis() - animStart else 0L
            val remaining = if (animDuration > 0L) (animDuration - elapsed).coerceIn(0L, 620L) else 0L

            val fadeOut = ObjectAnimator.ofFloat(splashProvider.view, View.ALPHA, 1f, 0f)
            val iconScaleX = ObjectAnimator.ofFloat(splashProvider.iconView, View.SCALE_X, 1f, 1.16f)
            val iconScaleY = ObjectAnimator.ofFloat(splashProvider.iconView, View.SCALE_Y, 1f, 1.16f)

            AnimatorSet().apply {
                playTogether(fadeOut, iconScaleX, iconScaleY)
                duration = 240L
                startDelay = remaining
                interpolator = AccelerateInterpolator()
                doOnEnd { splashProvider.remove() }
                start()
            }
        }

        setContent {
            val appLanguage by viewModel.appLanguage.collectAsState()
            val themeMode by viewModel.themeMode.collectAsState()
            val colorPalette by viewModel.colorPalette.collectAsState()
            val useDynamicColor by viewModel.useDynamicColor.collectAsState()
            val strings = remember(appLanguage) { StringsProvider.resolve(appLanguage) }

            CompositionLocalProvider(LocalStrings provides strings) {
                MyApplicationTheme(
                    themeMode = themeMode,
                    colorPalette = colorPalette,
                    dynamicColor = useDynamicColor
                ) {
                    PuzzleAppRoot(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PuzzleAppRoot(viewModel: BoardViewModel) {
    val boardState by viewModel.boardState.collectAsState()
    val gridRows by viewModel.gridRows.collectAsState()
    val gridCols by viewModel.gridCols.collectAsState()
    val isSolving by viewModel.isSolving.collectAsState()
    val solutionPath by viewModel.solutionPath.collectAsState()
    val solveStats by viewModel.solveStats.collectAsState()
    val isGathering by viewModel.isGathering.collectAsState()
    val isShuffling by viewModel.isShuffling.collectAsState()
    val showSettings by viewModel.showSettingsScreen.collectAsState()
    val showSetup by viewModel.showSetupScreen.collectAsState()
    val showGridDialog by viewModel.showGridDialog.collectAsState()
    val showRulesSheet by viewModel.showRulesSheet.collectAsState()
    val solveError by viewModel.solveError.collectAsState()
    val isBoardSolved by viewModel.isBoardSolved.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val hapticMode by viewModel.hapticMode.collectAsState()
    val showHud by viewModel.showHud.collectAsState()
    val moveCount by viewModel.moveCount.collectAsState()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsState()
    val bestRecord by viewModel.bestRecord.collectAsState()
    val canUndo by viewModel.canUndo.collectAsState()
    val showVictoryModal by viewModel.showVictoryModal.collectAsState()
    val isNewRecord by viewModel.isNewRecord.collectAsState()

    val context = LocalContext.current
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current
    val strings = LocalStrings.current

    var showExitConfirmDialog by remember { mutableStateOf(false) }

    val primaryGlow = MaterialTheme.colorScheme.primary
    val tertiaryGlow = MaterialTheme.colorScheme.tertiary
    val dotGridColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.055f)
    val studioBgColor = MaterialTheme.colorScheme.background

    LaunchedEffect(solveError) {
        solveError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearSolveError()
        }
    }

    val triggerMoveFeedback = remember(soundEnabled, hapticMode) {
        {
            if (soundEnabled) {
                view.playSoundEffect(SoundEffectConstants.CLICK)
            }
            when (hapticMode) {
                HapticMode.LIGHT -> haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                HapticMode.MEDIUM, HapticMode.STRONG -> haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                HapticMode.OFF -> {}
            }
        }
    }

    if (showExitConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showExitConfirmDialog = false },
            title = { Text(strings.exitDialogTitle, fontWeight = FontWeight.Bold) },
            text = { Text(strings.exitDialogMessage) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitConfirmDialog = false
                        (context as? Activity)?.finish()
                    }
                ) {
                    Text(strings.exitConfirm, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitConfirmDialog = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }

    if (showGridDialog) {
        GridSizeDialog(viewModel = viewModel)
    }

    if (showRulesSheet) {
        RulesBottomSheet(onDismiss = { viewModel.closeRulesSheet() })
    }

    if (showVictoryModal) {
        VictoryDialog(
            gridRows = gridRows,
            gridCols = gridCols,
            moveCount = moveCount,
            elapsedSeconds = elapsedSeconds,
            isNewRecord = isNewRecord,
            onPlayAgain = { viewModel.shuffle() },
            onDismiss = { viewModel.dismissVictoryModal() }
        )
    }

    val correctTiles = remember(boardState, gridCols) {
        var count = 0
        for (i in boardState.indices) {
            val v = boardState[i]
            if (v != 0 && v == i + 1) count++
        }
        count
    }
    val totalTiles = (gridRows * gridCols - 1).coerceAtLeast(1)

    val activeDestination = when {
        showSettings -> 1
        showSetup -> 2
        else -> 0
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(color = studioBgColor)
                val step = 22.dp.toPx()
                val radius = 1.dp.toPx()
                var y = step / 2f
                while (y < size.height) {
                    var x = step / 2f
                    while (x < size.width) {
                        drawCircle(
                            color = dotGridColor,
                            radius = radius,
                            center = Offset(x, y)
                        )
                        x += step
                    }
                    y += step
                }
            }
    ) {
        AnimatedContent(
            targetState = activeDestination,
            transitionSpec = {
                when {
                    // Opening Settings (0 -> 1): slide in from right
                    targetState == 1 -> {
                        (slideInHorizontally(tween(280, easing = FastOutSlowInEasing)) { it / 4 } + fadeIn(tween(240)))
                            .togetherWith(slideOutHorizontally(tween(240)) { -it / 8 } + fadeOut(tween(180)) + scaleOut(targetScale = 0.96f))
                    }
                    // Closing Settings (1 -> 0): slide back right
                    initialState == 1 -> {
                        (slideInHorizontally(tween(260, easing = FastOutSlowInEasing)) { -it / 8 } + fadeIn(tween(220)) + scaleIn(initialScale = 0.96f))
                            .togetherWith(slideOutHorizontally(tween(260)) { it / 4 } + fadeOut(tween(180)))
                    }
                    // Opening Setup (0 -> 2): slide up smoothly
                    targetState == 2 -> {
                        (slideInVertically(tween(280, easing = FastOutSlowInEasing)) { it / 5 } + fadeIn(tween(240)))
                            .togetherWith(fadeOut(tween(180)) + scaleOut(targetScale = 0.96f))
                    }
                    // Closing Setup (2 -> 0)
                    else -> {
                        (fadeIn(tween(220)) + scaleIn(initialScale = 0.96f))
                            .togetherWith(slideOutVertically(tween(250)) { it / 5 } + fadeOut(tween(180)))
                    }
                }
            },
            label = "screenNavigation"
        ) { dest ->
            when (dest) {
                1 -> {
                    BackHandler { viewModel.closeSettings() }
                    SettingsScreen(viewModel = viewModel)
                }
                2 -> {
                    BackHandler { viewModel.closeSetup() }
                    SetupScreen(viewModel = viewModel)
                }
                else -> {
                    BackHandler(enabled = !showExitConfirmDialog) {
                        if (solutionPath != null) {
                            viewModel.clearSolution()
                        } else {
                            showExitConfirmDialog = true
                        }
                    }
                Scaffold(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    topBar = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .heightIn(min = 52.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                onClick = { viewModel.openGridDialog() },
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier
                                    .heightIn(min = 42.dp)
                                    .testTag("grid_size_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_grid),
                                        contentDescription = strings.gridSizeTitle,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "${gridRows}×${gridCols}",
                                        style = TextStyle(
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            var titleFontSizeSp by remember(strings.appTitle, gridRows, gridCols) {
                                mutableFloatStateOf(17f)
                            }
                            Text(
                                text = strings.appTitle,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = titleFontSizeSp.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                                onTextLayout = { layoutResult ->
                                    if (layoutResult.hasVisualOverflow && titleFontSizeSp > 12f) {
                                        titleFontSizeSp = (titleFontSizeSp - 0.5f).coerceAtLeast(12f)
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 6.dp)
                            )

                            val rotationAnim by animateFloatAsState(
                                targetValue = if (isShuffling) 360f * 3 else 0f,
                                animationSpec = tween(600, easing = FastOutSlowInEasing),
                                label = "shuffleSpin"
                            )
                            val shuffleScale by animateFloatAsState(
                                targetValue = if (isShuffling) 1.22f else 1f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                label = "shuffleScale"
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(0.dp)
                            ) {
                                IconButton(
                                    onClick = { viewModel.openRulesSheet() },
                                    modifier = Modifier.testTag("rules_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                        contentDescription = strings.helpDesc,
                                        tint = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.openSettings() },
                                    modifier = Modifier.testTag("settings_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = strings.settingsTitle,
                                        tint = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.shuffle() },
                                    modifier = Modifier.testTag("shuffle_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = strings.shuffleDesc,
                                        tint = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier
                                            .graphicsLayer {
                                                scaleX = shuffleScale
                                                scaleY = shuffleScale
                                            }
                                            .rotate(rotationAnim)
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp)
                    ) {
                        val isWideLandscape = maxWidth > 620.dp && maxWidth > maxHeight * 1.1f

                        if (isWideLandscape) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.spacedBy(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1.15f)
                                        .fillMaxHeight()
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    PuzzleBoard(
                                        viewModel = viewModel,
                                        board = boardState,
                                        isGathering = isGathering,
                                        isShuffling = isShuffling,
                                        onTileClick = { tileValue ->
                                            if (viewModel.moveTile(tileValue)) triggerMoveFeedback()
                                        },
                                        onTileSwipe = { tileValue, dx, dy ->
                                            if (viewModel.trySwipeTile(tileValue, dx, dy)) triggerMoveFeedback()
                                        }
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .weight(0.85f)
                                        .widthIn(max = 420.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    GameStatusBadge(
                                        isSolving = isSolving,
                                        isBoardSolved = isBoardSolved,
                                        hasSolution = solutionPath != null,
                                        solveStats = solveStats
                                    )

                                    AnimatedVisibility(visible = showHud) {
                                        GameTelemetryStrip(
                                            moveCount = moveCount,
                                            elapsedSeconds = elapsedSeconds,
                                            correctTiles = correctTiles,
                                            totalTiles = totalTiles,
                                            bestRecord = bestRecord,
                                            canUndo = canUndo,
                                            onUndo = { viewModel.undoMove() }
                                        )
                                    }

                                    GameActionDeck(
                                        viewModel = viewModel,
                                        isSolving = isSolving,
                                        isBoardSolved = isBoardSolved,
                                        solutionPath = solutionPath
                                    )
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    GameStatusBadge(
                                        isSolving = isSolving,
                                        isBoardSolved = isBoardSolved,
                                        hasSolution = solutionPath != null,
                                        solveStats = solveStats
                                    )

                                    AnimatedVisibility(visible = showHud) {
                                        GameTelemetryStrip(
                                            moveCount = moveCount,
                                            elapsedSeconds = elapsedSeconds,
                                            correctTiles = correctTiles,
                                            totalTiles = totalTiles,
                                            bestRecord = bestRecord,
                                            canUndo = canUndo,
                                            onUndo = { viewModel.undoMove() },
                                            modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    PuzzleBoard(
                                        viewModel = viewModel,
                                        board = boardState,
                                        isGathering = isGathering,
                                        isShuffling = isShuffling,
                                        onTileClick = { tileValue ->
                                            if (viewModel.moveTile(tileValue)) triggerMoveFeedback()
                                        },
                                        onTileSwipe = { tileValue, dx, dy ->
                                            if (viewModel.trySwipeTile(tileValue, dx, dy)) triggerMoveFeedback()
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                GameActionDeck(
                                    viewModel = viewModel,
                                    isSolving = isSolving,
                                    isBoardSolved = isBoardSolved,
                                    solutionPath = solutionPath,
                                    modifier = Modifier.padding(bottom = 14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        }

        AnimatedVisibility(
            visible = showVictoryModal,
            enter = fadeIn(tween(250)),
            exit = fadeOut(tween(250))
        ) {
            VictoryConfettiOverlay(
                primaryColor = primaryGlow,
                tertiaryColor = tertiaryGlow
            )
        }
    }
}
