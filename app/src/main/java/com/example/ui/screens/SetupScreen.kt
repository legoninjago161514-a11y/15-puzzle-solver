package com.example.ui.screens

import android.view.SoundEffectConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HapticMode
import com.example.i18n.LocalStrings
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.viewmodel.BoardViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SetupScreen(viewModel: BoardViewModel) {
    val context = LocalContext.current
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current
    val strings = LocalStrings.current

    val mode by viewModel.setupMode.collectAsState()
    val board by viewModel.setupBoard.collectAsState()
    val selectedCell by viewModel.setupSelectedCell.collectAsState()
    val tray by viewModel.setupTray.collectAsState()
    val selectedTrayItem by viewModel.setupSelectedTrayItem.collectAsState()
    val trayOrder by viewModel.setupTrayOrder.collectAsState()
    val setupCanUndo by viewModel.setupCanUndo.collectAsState()
    val gridRows by viewModel.gridRows.collectAsState()
    val gridCols by viewModel.gridCols.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val hapticMode by viewModel.hapticMode.collectAsState()

    val totalCells = gridRows * gridCols
    val isReady = board.size == totalCells && board.all { it != null } && board.toSet().size == totalCells
    val isNativelySolvable = remember(board, gridRows, gridCols, isReady) {
        if (isReady) viewModel.isConfigurationSolvable(board.map { it!! }, gridRows, gridCols) else true
    }

    // Sub-mode inside Input Tab: 0 = Quick Tile Palette, 1 = Smart Built-in Numpad
    var inputSubMode by remember(gridRows, gridCols) {
        mutableIntStateOf(if (totalCells <= 36) 0 else 1)
    }
    var digitBuffer by remember(selectedCell, gridRows, gridCols) { mutableStateOf("") }

    val triggerTapFeedback = remember(soundEnabled, hapticMode) {
        {
            if (soundEnabled) view.playSoundEffect(SoundEffectConstants.CLICK)
            when (hapticMode) {
                HapticMode.LIGHT -> haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                HapticMode.MEDIUM, HapticMode.STRONG -> haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                HapticMode.OFF -> {}
            }
        }
    }

    // Drag-and-Drop state across both modes
    var draggingNumber by remember { mutableStateOf<Int?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }
    var hoverGridCellIndex by remember { mutableStateOf<Int?>(null) }
    var gridBoundsInWindow by remember { mutableStateOf<Rect?>(null) }
    var rootWindowPos by remember { mutableStateOf(Offset.Zero) }
    var isScrollEnabled by remember { mutableStateOf(true) }

    val cursorPulseTransition = rememberInfiniteTransition(label = "setupCursorPulse")
    val cursorAlpha by cursorPulseTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { rootWindowPos = it.positionInWindow() }
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = strings.setupTitle,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = strings.setupSubtitle(gridRows, gridCols),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.closeSetup() }) {
                            Icon(Icons.Default.Close, contentDescription = strings.closeDesc)
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                digitBuffer = ""
                                viewModel.undoSetup()
                                triggerTapFeedback()
                            },
                            enabled = setupCanUndo
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = strings.undoShortDesc)
                        }
                        FilledTonalButton(
                            onClick = { viewModel.confirmSetup() },
                            enabled = isReady,
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .heightIn(min = 40.dp)
                                .testTag("setup_confirm_button")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(strings.setupConfirmDone, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 600.dp)
                        .navigationBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                        .verticalScroll(rememberScrollState(), enabled = isScrollEnabled),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TabRow(
                        selectedTabIndex = mode,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier.clip(RoundedCornerShape(14.dp))
                    ) {
                        Tab(
                            selected = mode == 0,
                            onClick = { viewModel.setSetupMode(0) },
                            text = { Text(strings.setupModeInput, fontWeight = FontWeight.SemiBold) }
                        )
                        Tab(
                            selected = mode == 1,
                            onClick = { viewModel.setSetupMode(1) },
                            text = { Text(strings.setupModePlace, fontWeight = FontWeight.SemiBold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress & Solvability Banner
                    val filledCount = board.count { it != null }
                    val progressAnim by animateFloatAsState(
                        targetValue = filledCount.toFloat() / totalCells.coerceAtLeast(1),
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "setupProgress"
                    )
                    val sageBanner = Color(0xFF4E7A61)
                    val ochreBanner = Color(0xFFC98E38)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = when {
                            !isReady -> MaterialTheme.colorScheme.surface
                            isNativelySolvable -> sageBanner.copy(alpha = 0.13f)
                            else -> ochreBanner.copy(alpha = 0.15f)
                        },
                        border = BorderStroke(
                            1.dp,
                            when {
                                !isReady -> MaterialTheme.colorScheme.outlineVariant
                                isNativelySolvable -> sageBanner.copy(alpha = 0.50f)
                                else -> ochreBanner.copy(alpha = 0.55f)
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isReady && !isNativelySolvable) Icons.Default.WarningAmber else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = when {
                                        !isReady -> MaterialTheme.colorScheme.primary
                                        isNativelySolvable -> sageBanner
                                        else -> ochreBanner
                                    }
                                )
                                Text(
                                    text = when {
                                        !isReady -> strings.setupProgressText(filledCount, totalCells)
                                        isNativelySolvable -> strings.setupSolvableReady
                                        else -> strings.setupUnsolvableFix
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            if (!isReady) {
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { progressAnim },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Interactive Setup Grid
                    BoxWithConstraints(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        val spacing = if (gridCols >= 8) 2.dp else 4.dp
                        val outerPadding = if (gridCols >= 8) 6.dp else 8.dp
                        val maxW = (maxWidth - 8.dp).coerceAtMost(340.dp)
                        val availableInnerW = maxW - outerPadding * 2

                        val cellWidth = (availableInnerW - spacing * (gridCols - 1)) / gridCols
                        val cellSize = cellWidth.coerceIn(16.dp, 68.dp)
                        val gridWidth = cellSize * gridCols + spacing * (gridCols - 1) + outerPadding * 2
                        val gridHeight = cellSize * gridRows + spacing * (gridRows - 1) + outerPadding * 2

                        val cellCornerRadius = when {
                            gridCols >= 9 || cellSize < 30.dp -> 4.dp
                            gridCols >= 7 || cellSize < 42.dp -> 6.dp
                            else -> 11.dp
                        }

                        Box(
                            modifier = Modifier
                                .width(gridWidth)
                                .height(gridHeight)
                                .clip(RoundedCornerShape((cellCornerRadius * 1.8f).coerceAtLeast(14.dp)))
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
                                    RoundedCornerShape((cellCornerRadius * 1.8f).coerceAtLeast(14.dp))
                                )
                                .padding(outerPadding)
                                .onGloballyPositioned { gridBoundsInWindow = it.boundsInWindow() }
                        ) {
                            for (i in 0 until totalCells) {
                                val row = i / gridCols
                                val col = i % gridCols
                                val number = if (i in board.indices) board[i] else null
                                val isSelected = selectedCell == i
                                val isHovered = hoverGridCellIndex == i

                                val cellBg by animateColorAsState(
                                    targetValue = when {
                                        isHovered -> MaterialTheme.colorScheme.tertiaryContainer
                                        number == 0 -> MaterialTheme.colorScheme.surfaceContainerHighest
                                        isSelected && mode == 0 -> MaterialTheme.colorScheme.primaryContainer
                                        number != null -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.surface
                                    },
                                    animationSpec = tween(180),
                                    label = "cell_bg"
                                )

                                val cellText by animateColorAsState(
                                    targetValue = when {
                                        isHovered -> MaterialTheme.colorScheme.onTertiaryContainer
                                        number == 0 -> MaterialTheme.colorScheme.onSurfaceVariant
                                        isSelected && mode == 0 -> MaterialTheme.colorScheme.onPrimaryContainer
                                        number != null -> MaterialTheme.colorScheme.onPrimary
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    animationSpec = tween(180),
                                    label = "cell_text"
                                )

                                var cellWindowPos by remember { mutableStateOf(Offset.Zero) }
                                val cellScale by animateFloatAsState(
                                    targetValue = when {
                                        isHovered -> if (gridCols <= 6) 1.12f else 1.05f
                                        isSelected && mode == 0 -> if (gridCols <= 6) 1.05f else 1.02f
                                        else -> 1f
                                    },
                                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                    label = "cellScale"
                                )

                                Box(
                                    modifier = Modifier
                                        .offset(x = (cellSize + spacing) * col, y = (cellSize + spacing) * row)
                                        .size(cellSize)
                                        .graphicsLayer {
                                            scaleX = cellScale
                                            scaleY = cellScale
                                        }
                                        .clip(RoundedCornerShape(cellCornerRadius))
                                        .background(cellBg)
                                        .then(
                                            if (isSelected && mode == 0) {
                                                Modifier.border(
                                                    width = 2.dp,
                                                    color = MaterialTheme.colorScheme.primary.copy(alpha = cursorAlpha),
                                                    shape = RoundedCornerShape(cellCornerRadius)
                                                )
                                            } else if (number == 0) {
                                                Modifier.border(
                                                    width = 1.5.dp,
                                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                                                    shape = RoundedCornerShape(cellCornerRadius)
                                                )
                                            } else Modifier
                                        )
                                        .onGloballyPositioned { cellWindowPos = it.positionInWindow() }
                                        .then(
                                            if (number != null) {
                                                Modifier
                                                    .pointerInput(number, i) {
                                                        awaitEachGesture {
                                                            awaitFirstDown(requireUnconsumed = false)
                                                            isScrollEnabled = false
                                                            waitForUpOrCancellation()
                                                            isScrollEnabled = true
                                                        }
                                                    }
                                                    .pointerInput(number, i) {
                                                        detectDragGestures(
                                                            onDragStart = { localOffset ->
                                                                draggingNumber = number
                                                                dragPosition = cellWindowPos + localOffset
                                                            },
                                                            onDrag = { change, dragAmount ->
                                                                change.consume()
                                                                dragPosition += dragAmount
                                                                val bounds = gridBoundsInWindow
                                                                if (bounds != null && bounds.contains(dragPosition)) {
                                                                    val relX = dragPosition.x - bounds.left
                                                                    val relY = dragPosition.y - bounds.top
                                                                    val c = (relX / (bounds.width / gridCols)).toInt().coerceIn(0, gridCols - 1)
                                                                    val r = (relY / (bounds.height / gridRows)).toInt().coerceIn(0, gridRows - 1)
                                                                    hoverGridCellIndex = r * gridCols + c
                                                                } else {
                                                                    hoverGridCellIndex = null
                                                                }
                                                            },
                                                            onDragEnd = {
                                                                val target = hoverGridCellIndex
                                                                if (target != null && target != i) {
                                                                    viewModel.moveSetupItem(fromIndex = i, toIndex = target)
                                                                    triggerTapFeedback()
                                                                } else if (hoverGridCellIndex == null && mode == 1) {
                                                                    viewModel.removePlacedItem(i)
                                                                }
                                                                draggingNumber = null
                                                                hoverGridCellIndex = null
                                                            },
                                                            onDragCancel = {
                                                                draggingNumber = null
                                                                hoverGridCellIndex = null
                                                            }
                                                        )
                                                    }
                                            } else Modifier
                                        )
                                        .clickable {
                                            if (mode == 0) {
                                                digitBuffer = ""
                                                viewModel.selectSetupCell(i)
                                                triggerTapFeedback()
                                            } else {
                                                if (selectedTrayItem != null) {
                                                    viewModel.placeTrayItemAt(i)
                                                    triggerTapFeedback()
                                                } else if (number != null) {
                                                    viewModel.removePlacedItem(i)
                                                }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (number != null) {
                                        val numStr = if (number == 0) "∅" else number.toString()
                                        val numFontSize = when (numStr.length) {
                                            1 -> (cellSize.value * 0.48f).coerceIn(9f, 23f).sp
                                            2 -> (cellSize.value * 0.36f).coerceIn(7.5f, 18f).sp
                                            else -> (cellSize.value * 0.27f).coerceIn(6.5f, 13f).sp
                                        }
                                        Text(
                                            text = numStr,
                                            style = TextStyle(
                                                fontFamily = JetBrainsMonoFontFamily,
                                                fontSize = numFontSize,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = cellText,
                                            maxLines = 1,
                                            softWrap = false,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (mode == 0) {
                        val placedNumbers = remember(board) { board.filterNotNull().toSet() }

                        // Sub-mode Switcher + Cell Cursor Bar
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = {
                                                digitBuffer = ""
                                                if (selectedCell > 0) viewModel.selectSetupCell(selectedCell - 1)
                                            },
                                            enabled = selectedCell > 0
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = strings.prevStepDesc)
                                        }
                                        Text(
                                            text = strings.setupCellLabel(selectedCell / gridCols + 1, selectedCell % gridCols + 1),
                                            style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 13.sp, fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        IconButton(
                                            onClick = {
                                                digitBuffer = ""
                                                if (selectedCell < totalCells - 1) viewModel.selectSetupCell(selectedCell + 1)
                                            },
                                            enabled = selectedCell < totalCells - 1
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = strings.nextStepDesc)
                                        }
                                    }

                                    // Toggle Tile Grid vs Keyboard Keypad
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                            .padding(3.dp),
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        SubModeIconChip(
                                            icon = Icons.Default.GridView,
                                            contentDescription = strings.setupTilePaletteDesc,
                                            selected = inputSubMode == 0,
                                            onClick = { inputSubMode = 0 }
                                        )
                                        SubModeIconChip(
                                            icon = Icons.Default.Keyboard,
                                            contentDescription = strings.setupKeyboardInputDesc,
                                            selected = inputSubMode == 1,
                                            onClick = { inputSubMode = 1 }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                AnimatedContent(
                                    targetState = inputSubMode,
                                    transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(150)) },
                                    label = "inputSubMode"
                                ) { subMode ->
                                    if (subMode == 0) {
                                        // ONE-TAP TILE PALETTE (No system keyboard!)
                                        val allValues = remember(totalCells) { (1 until totalCells).toList() + listOf(0) }
                                        val columnsCount = when {
                                            totalCells <= 9 -> 5
                                            totalCells <= 16 -> 4
                                            totalCells <= 25 -> 5
                                            else -> 6
                                        }
                                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            allValues.chunked(columnsCount).forEach { rowValues ->
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    rowValues.forEach { num ->
                                                        val isUsed = num in placedNumbers
                                                        val isInCurrentCell = board.getOrNull(selectedCell) == num
                                                        Surface(
                                                            onClick = {
                                                                viewModel.inputSetupNumber(num, advanceCell = true)
                                                                triggerTapFeedback()
                                                            },
                                                            shape = RoundedCornerShape(11.dp),
                                                            color = when {
                                                                isInCurrentCell -> MaterialTheme.colorScheme.tertiaryContainer
                                                                !isUsed -> MaterialTheme.colorScheme.primaryContainer
                                                                else -> MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f)
                                                            },
                                                            border = BorderStroke(
                                                                width = if (isInCurrentCell) 1.5.dp else 1.dp,
                                                                color = when {
                                                                    isInCurrentCell -> MaterialTheme.colorScheme.tertiary
                                                                    !isUsed -> MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                                                    else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                                                }
                                                            ),
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .height(44.dp)
                                                        ) {
                                                            Box(contentAlignment = Alignment.Center) {
                                                                Text(
                                                                    text = if (num == 0) "∅" else num.toString(),
                                                                    style = TextStyle(
                                                                        fontFamily = JetBrainsMonoFontFamily,
                                                                        fontSize = 15.sp,
                                                                        fontWeight = if (isUsed) FontWeight.Normal else FontWeight.Bold
                                                                    ),
                                                                    color = when {
                                                                        isInCurrentCell -> MaterialTheme.colorScheme.onTertiaryContainer
                                                                        !isUsed -> MaterialTheme.colorScheme.onPrimaryContainer
                                                                        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
                                                                    }
                                                                )
                                                            }
                                                        }
                                                    }
                                                    repeat(columnsCount - rowValues.size) {
                                                        Spacer(modifier = Modifier.weight(1f))
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        // SMART BUILT-IN CALCULATOR NUMPAD (Zero system keyboard!)
                                        val handleDigitPress: (Int) -> Unit = { digit ->
                                            val candidateStr = digitBuffer + digit.toString()
                                            val candidate = candidateStr.toIntOrNull()
                                            if (candidate != null && candidate in 0 until totalCells) {
                                                // Does any unplaced or larger valid number start with this prefix?
                                                val couldHaveNextDigit = candidate > 0 && (candidate * 10 < totalCells) &&
                                                    ((candidate * 10) until minOf(totalCells, candidate * 10 + 10)).any { it !in placedNumbers }
                                                if (!couldHaveNextDigit) {
                                                    digitBuffer = ""
                                                    viewModel.inputSetupNumber(candidate, advanceCell = true)
                                                } else {
                                                    digitBuffer = candidateStr
                                                }
                                                triggerTapFeedback()
                                            } else if (digit in 0 until totalCells) {
                                                // Fallback: start fresh buffer with new digit
                                                val couldHaveNext = digit > 0 && (digit * 10 < totalCells) &&
                                                    ((digit * 10) until minOf(totalCells, digit * 10 + 10)).any { it !in placedNumbers }
                                                if (!couldHaveNext) {
                                                    digitBuffer = ""
                                                    viewModel.inputSetupNumber(digit, advanceCell = true)
                                                } else {
                                                    digitBuffer = digit.toString()
                                                }
                                                triggerTapFeedback()
                                            }
                                        }

                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            // Buffer Display & Clear Cell
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Surface(
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(46.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 14.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(
                                                            text = if (digitBuffer.isEmpty()) strings.setupBufferHint(totalCells - 1) else strings.setupBufferValue(digitBuffer),
                                                            style = TextStyle(
                                                                fontFamily = JetBrainsMonoFontFamily,
                                                                fontSize = 15.sp,
                                                                fontWeight = FontWeight.Bold
                                                            ),
                                                            color = if (digitBuffer.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
                                                        )
                                                        if (digitBuffer.isNotEmpty()) {
                                                            Text(
                                                                text = strings.setupPressEnterHint,
                                                                fontSize = 11.sp,
                                                                color = MaterialTheme.colorScheme.primary
                                                            )
                                                        }
                                                    }
                                                }
                                                OutlinedButton(
                                                    onClick = {
                                                        digitBuffer = ""
                                                        viewModel.clearSetupCell()
                                                        triggerTapFeedback()
                                                    },
                                                    shape = RoundedCornerShape(12.dp),
                                                    modifier = Modifier.height(46.dp),
                                                    contentPadding = PaddingValues(horizontal = 12.dp)
                                                ) {
                                                    Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(strings.setupClearCell, fontSize = 12.sp)
                                                }
                                            }

                                            // 3x4 Numpad Rows
                                            val rowsKeys = listOf(
                                                listOf(1, 2, 3),
                                                listOf(4, 5, 6),
                                                listOf(7, 8, 9)
                                            )
                                            rowsKeys.forEach { rowNums ->
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    rowNums.forEach { d ->
                                                        NumpadKey(
                                                            label = d.toString(),
                                                            onClick = { handleDigitPress(d) },
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                    }
                                                }
                                            }
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                NumpadKey(
                                                    label = if (digitBuffer.isEmpty()) "∅ (0)" else "0",
                                                    onClick = { handleDigitPress(0) },
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Surface(
                                                    onClick = {
                                                        if (digitBuffer.isNotEmpty()) {
                                                            digitBuffer = digitBuffer.dropLast(1)
                                                        } else {
                                                            viewModel.clearSetupCell()
                                                        }
                                                        triggerTapFeedback()
                                                    },
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(48.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = strings.setupClearCell, modifier = Modifier.size(20.dp))
                                                    }
                                                }
                                                Button(
                                                    onClick = {
                                                        val v = digitBuffer.toIntOrNull()
                                                        if (v != null && v in 0 until totalCells) {
                                                            digitBuffer = ""
                                                            viewModel.inputSetupNumber(v, advanceCell = true)
                                                            triggerTapFeedback()
                                                        } else if (selectedCell < totalCells - 1) {
                                                            viewModel.selectSetupCell(selectedCell + 1)
                                                        }
                                                    },
                                                    shape = RoundedCornerShape(12.dp),
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(48.dp)
                                                ) {
                                                    Text(if (digitBuffer.isNotEmpty()) strings.setupSubmitShort else strings.setupNextShort, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Place / Block-Blast Tray Mode
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(strings.setupTrayInstruction, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    TextButton(onClick = { viewModel.toggleTrayOrder() }) {
                                        Text(if (trayOrder == 0) strings.setupTrayRandom else strings.setupTrayOrder)
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (tray.isEmpty()) {
                                        Text(strings.setupAllPlaced, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                                    } else {
                                        for (item in tray) {
                                            val isSelected = selectedTrayItem == item
                                            var itemWindowPos by remember { mutableStateOf(Offset.Zero) }
                                            val itemBg by animateColorAsState(
                                                targetValue = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primary,
                                                animationSpec = tween(220),
                                                label = "tray_bg"
                                            )
                                            val itemText by animateColorAsState(
                                                targetValue = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimary,
                                                animationSpec = tween(220),
                                                label = "tray_text"
                                            )
                                            val trayScale by animateFloatAsState(
                                                targetValue = if (isSelected) 1.08f else 1f,
                                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                                label = "tray_scale"
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(72.dp)
                                                    .graphicsLayer {
                                                        scaleX = trayScale
                                                        scaleY = trayScale
                                                    }
                                                    .clip(RoundedCornerShape(14.dp))
                                                    .background(itemBg)
                                                    .onGloballyPositioned { itemWindowPos = it.positionInWindow() }
                                                    .pointerInput(item) {
                                                        awaitEachGesture {
                                                            awaitFirstDown(requireUnconsumed = false)
                                                            isScrollEnabled = false
                                                            waitForUpOrCancellation()
                                                            isScrollEnabled = true
                                                        }
                                                    }
                                                    .pointerInput(item) {
                                                        detectDragGestures(
                                                            onDragStart = { localOffset ->
                                                                draggingNumber = item
                                                                dragPosition = itemWindowPos + localOffset
                                                            },
                                                            onDrag = { change, dragAmount ->
                                                                change.consume()
                                                                dragPosition += dragAmount
                                                                val bounds = gridBoundsInWindow
                                                                if (bounds != null && bounds.contains(dragPosition)) {
                                                                    val relX = dragPosition.x - bounds.left
                                                                    val relY = dragPosition.y - bounds.top
                                                                    val c = (relX / (bounds.width / gridCols)).toInt().coerceIn(0, gridCols - 1)
                                                                    val r = (relY / (bounds.height / gridRows)).toInt().coerceIn(0, gridRows - 1)
                                                                    hoverGridCellIndex = r * gridCols + c
                                                                } else {
                                                                    hoverGridCellIndex = null
                                                                }
                                                            },
                                                            onDragEnd = {
                                                                val target = hoverGridCellIndex
                                                                if (target != null) {
                                                                    viewModel.selectTrayItem(item)
                                                                    viewModel.placeTrayItemAt(target)
                                                                    triggerTapFeedback()
                                                                }
                                                                draggingNumber = null
                                                                hoverGridCellIndex = null
                                                            },
                                                            onDragCancel = {
                                                                draggingNumber = null
                                                                hoverGridCellIndex = null
                                                            }
                                                        )
                                                    }
                                                    .clickable {
                                                        viewModel.selectTrayItem(item)
                                                        triggerTapFeedback()
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = if (item == 0) "∅" else item.toString(),
                                                    style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 22.sp, fontWeight = FontWeight.Bold),
                                                    color = itemText
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Utilities Bar (Autofill / Random / Paste / Clear)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalButton(
                            onClick = {
                                digitBuffer = ""
                                viewModel.autoFillRemainingSetup(random = false)
                                triggerTapFeedback()
                            },
                            enabled = !isReady,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 46.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp)
                        ) {
                            Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.setupAutoFill, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }

                        OutlinedButton(
                            onClick = {
                                digitBuffer = ""
                                viewModel.autoFillRemainingSetup(random = true)
                                triggerTapFeedback()
                            },
                            enabled = !isReady,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 46.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp)
                        ) {
                            Icon(Icons.Default.Casino, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.setupRandomFill, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.importBoardFromClipboard(context) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 46.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp)
                        ) {
                            Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.setupPaste, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }

                        OutlinedButton(
                            onClick = {
                                digitBuffer = ""
                                viewModel.clearAllSetupCells()
                                triggerTapFeedback()
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 46.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp)
                        ) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.setupClearAll, fontSize = 12.sp, color = MaterialTheme.colorScheme.error, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        if (draggingNumber != null) {
            val num = draggingNumber!!
            val density = LocalDensity.current
            val tileHalfPx = with(density) { 36.dp.toPx() }
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        translationX = dragPosition.x - rootWindowPos.x - tileHalfPx
                        translationY = dragPosition.y - rootWindowPos.y - tileHalfPx
                        scaleX = 1.15f
                        scaleY = 1.15f
                    }
                    .size(72.dp)
                    .shadow(16.dp, RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (num == 0) "∅" else num.toString(),
                    style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 24.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

@Composable
private fun SubModeIconChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        modifier = Modifier.size(width = 48.dp, height = 40.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(19.dp),
                tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun NumpadKey(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)),
        modifier = modifier.height(48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = TextStyle(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
