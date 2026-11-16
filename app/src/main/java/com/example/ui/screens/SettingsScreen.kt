package com.example.ui.screens

import android.os.Build
import android.view.SoundEffectConstants
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.data.ColorPalette
import com.example.data.CornerRadiusStyle
import com.example.data.HapticMode
import com.example.data.HeuristicType
import com.example.data.NumberFormat
import com.example.data.SolverAlgorithm
import com.example.data.SpringAnimationSpeed
import com.example.data.ThemeMode
import com.example.data.TileColorScheme
import com.example.i18n.AppLanguage
import com.example.i18n.LocalStrings
import com.example.ui.components.MiniBoardPreview
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.viewmodel.BoardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: BoardViewModel) {
    val context = LocalContext.current
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current
    val strings = LocalStrings.current

    val appLanguage by viewModel.appLanguage.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val colorPalette by viewModel.colorPalette.collectAsState()
    val useDynamicColor by viewModel.useDynamicColor.collectAsState()
    val tileColorScheme by viewModel.tileColorScheme.collectAsState()
    val numberFormat by viewModel.numberFormat.collectAsState()
    val cornerRadiusStyle by viewModel.cornerRadiusStyle.collectAsState()
    val springAnimation by viewModel.springAnimation.collectAsState()
    val autoPlaySpeed by viewModel.autoPlaySpeed.collectAsState()
    val multiTileMove by viewModel.multiTileMove.collectAsState()
    val swipeGesturesEnabled by viewModel.swipeGesturesEnabled.collectAsState()
    val showHud by viewModel.showHud.collectAsState()
    val highlightCorrectTiles by viewModel.highlightCorrectTiles.collectAsState()
    val hapticMode by viewModel.hapticMode.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val solverAlgorithm by viewModel.solverAlgorithm.collectAsState()
    val heuristicType by viewModel.heuristicType.collectAsState()
    val benchmarkResult by viewModel.benchmarkResult.collectAsState()
    val isRunningBenchmark by viewModel.isRunningBenchmark.collectAsState()
    val gamesWon by viewModel.gamesWon.collectAsState()

    var showResetRecordsDialog by remember { mutableStateOf(false) }
    var showResetSettingsDialog by remember { mutableStateOf(false) }
    var previewBoard by remember { mutableStateOf(listOf(1, 2, 3, 4, 5, 0, 7, 8, 6)) }

    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK, ThemeMode.AMOLED -> true
    }

    if (showResetRecordsDialog) {
        AlertDialog(
            onDismissRequest = { showResetRecordsDialog = false },
            title = { Text(strings.resetRecordsDialogTitle) },
            text = { Text(strings.resetRecordsDialogText) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetAllRecords()
                        showResetRecordsDialog = false
                        Toast.makeText(context, strings.recordsClearedToast, Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text(strings.resetConfirm, color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetRecordsDialog = false }) { Text(strings.cancel) }
            }
        )
    }

    if (showResetSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showResetSettingsDialog = false },
            title = { Text(strings.resetSettingsDialogTitle) },
            text = { Text(strings.resetSettingsDialogText) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetAllSettings()
                        showResetSettingsDialog = false
                        Toast.makeText(context, strings.settingsResetToast, Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text(strings.resetConfirm, color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetSettingsDialog = false }) { Text(strings.cancel) }
            }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = strings.settingsTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.closeSettings() },
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.settingsBackDesc
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
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
                    .widthIn(max = 620.dp)
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // LIVE INTERACTIVE PREVIEW CARD
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f)),
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MiniBoardPreview(
                            board = previewBoard,
                            tileColorScheme = tileColorScheme,
                            numberFormat = numberFormat,
                            cornerRadiusStyle = cornerRadiusStyle,
                            springAnimation = springAnimation,
                            highlightCorrectTiles = highlightCorrectTiles,
                            isDark = isDark,
                            onTileClick = { tileVal ->
                                val idx = previewBoard.indexOf(tileVal)
                                val zero = previewBoard.indexOf(0)
                                val r1 = idx / 3
                                val c1 = idx % 3
                                val r2 = zero / 3
                                val c2 = zero % 3
                                if (kotlin.math.abs(r1 - r2) + kotlin.math.abs(c1 - c2) == 1) {
                                    val next = previewBoard.toMutableList()
                                    next[zero] = tileVal
                                    next[idx] = 0
                                    previewBoard = next
                                    if (soundEnabled) view.playSoundEffect(SoundEffectConstants.CLICK)
                                    when (hapticMode) {
                                        HapticMode.LIGHT -> haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        HapticMode.MEDIUM, HapticMode.STRONG -> haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        HapticMode.OFF -> {}
                                    }
                                }
                            }
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = strings.livePreviewTitle,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = strings.livePreviewDesc,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                val schemeName = strings.tileSchemeTitles[tileColorScheme] ?: tileColorScheme.title
                                val cornerName = strings.cornerTitles[cornerRadiusStyle] ?: cornerRadiusStyle.title
                                Text(
                                    text = "$schemeName • $cornerName",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // 1. APPEARANCE & LANGUAGE
                SettingsSectionCard(
                    icon = Icons.Default.Palette,
                    title = strings.sectionAppearance
                ) {
                    SettingsSubLabel(strings.languageLabel)
                    SegmentedSelectorRow(
                        items = AppLanguage.entries.map { lang ->
                            lang to if (lang == AppLanguage.SYSTEM) strings.languageSystemLabel else lang.chipLabel
                        },
                        selected = appLanguage,
                        onSelect = { viewModel.setAppLanguage(it) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    SettingsSubLabel(strings.themeModeLabel)
                    SegmentedSelectorRow(
                        items = ThemeMode.entries.map { it to (strings.themeModeTitles[it] ?: it.title) },
                        selected = themeMode,
                        onSelect = { viewModel.setThemeMode(it) }
                    )

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Spacer(modifier = Modifier.height(10.dp))
                        SettingsSwitchRow(
                            title = strings.dynamicColorsTitle,
                            subtitle = strings.dynamicColorsSubtitle,
                            checked = useDynamicColor,
                            onCheckedChange = { viewModel.setUseDynamicColor(it) }
                        )
                    }

                    if (!useDynamicColor) {
                        Spacer(modifier = Modifier.height(10.dp))
                        val paletteLabel = strings.paletteTitles[colorPalette] ?: colorPalette.title
                        SettingsSubLabel("${strings.accentColorLabel} ($paletteLabel)")
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ColorPalette.entries.forEach { palette ->
                                val isSelected = (palette == colorPalette)
                                val pName = strings.paletteTitles[palette] ?: palette.title
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .clickable { viewModel.setColorPalette(palette) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(palette.primaryColor)
                                            .then(
                                                if (isSelected) {
                                                    Modifier.border(2.5.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                                } else Modifier
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = pName,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))

                    SettingsSubLabel(strings.tileSchemeLabel)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TileColorScheme.entries.forEach { scheme ->
                            val isSelected = (scheme == tileColorScheme)
                            val schemeName = strings.tileSchemeTitles[scheme] ?: scheme.title
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .clickable { viewModel.setTileColorScheme(scheme) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        scheme.previewColors.forEach { swatch ->
                                            Box(
                                                modifier = Modifier
                                                    .size(9.dp)
                                                    .clip(CircleShape)
                                                    .background(swatch)
                                            )
                                        }
                                    }
                                    Text(
                                        text = schemeName,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    SettingsSubLabel(strings.cornerRadiusLabel)
                    SegmentedSelectorRow(
                        items = CornerRadiusStyle.entries.map { it to (strings.cornerTitles[it] ?: it.title) },
                        selected = cornerRadiusStyle,
                        onSelect = { viewModel.setCornerRadiusStyle(it) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    SettingsSubLabel(strings.numberFormatLabel)
                    SegmentedSelectorRow(
                        items = NumberFormat.entries.map { it to it.title },
                        selected = numberFormat,
                        onSelect = { viewModel.setNumberFormat(it) }
                    )
                }

                // 2. GAMEPLAY & CONTROLS
                SettingsSectionCard(
                    icon = Icons.Default.Tune,
                    title = strings.sectionGameplay
                ) {
                    SettingsSwitchRow(
                        title = strings.multiTileTitle,
                        subtitle = strings.multiTileSubtitle,
                        checked = multiTileMove,
                        onCheckedChange = { viewModel.setMultiTileMove(it) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                    SettingsSwitchRow(
                        title = strings.swipeTitle,
                        subtitle = strings.swipeSubtitle,
                        checked = swipeGesturesEnabled,
                        onCheckedChange = { viewModel.setSwipeGesturesEnabled(it) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                    SettingsSwitchRow(
                        title = strings.hudTitle,
                        subtitle = strings.hudSubtitle,
                        checked = showHud,
                        onCheckedChange = { viewModel.setShowHud(it) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                    SettingsSwitchRow(
                        title = strings.highlightCorrectTitle,
                        subtitle = strings.highlightCorrectSubtitle,
                        checked = highlightCorrectTiles,
                        onCheckedChange = { viewModel.setHighlightCorrectTiles(it) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                    SettingsSwitchRow(
                        title = strings.soundTitle,
                        subtitle = strings.soundSubtitle,
                        checked = soundEnabled,
                        onCheckedChange = {
                            viewModel.setSoundEnabled(it)
                            if (it) view.playSoundEffect(SoundEffectConstants.CLICK)
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    SettingsSubLabel(strings.hapticLabel)
                    SegmentedSelectorRow(
                        items = HapticMode.entries.map { it to (strings.hapticTitles[it] ?: it.title) },
                        selected = hapticMode,
                        onSelect = { mode ->
                            viewModel.setHapticMode(mode)
                            when (mode) {
                                HapticMode.LIGHT -> haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                HapticMode.MEDIUM, HapticMode.STRONG -> haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                HapticMode.OFF -> {}
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    SettingsSubLabel(strings.animationPhysicsLabel)
                    SegmentedSelectorRow(
                        items = SpringAnimationSpeed.entries.map { it to (strings.springTitles[it] ?: it.title) },
                        selected = springAnimation,
                        onSelect = { viewModel.setSpringAnimation(it) }
                    )
                }

                // 3. SOLVER ENGINE & BENCHMARK
                SettingsSectionCard(
                    icon = Icons.Default.Memory,
                    title = strings.sectionSolver
                ) {
                    SettingsSubLabel(strings.solverAlgorithmLabel)
                    SegmentedSelectorRow(
                        items = SolverAlgorithm.entries.map { it to (strings.solverAlgoTitles[it]?.first ?: it.title) },
                        selected = solverAlgorithm,
                        onSelect = { viewModel.setSolverAlgorithm(it) }
                    )
                    Text(
                        text = strings.solverAlgoTitles[solverAlgorithm]?.second ?: solverAlgorithm.subtitle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    SettingsSubLabel(strings.heuristicLabel)
                    SegmentedSelectorRow(
                        items = HeuristicType.entries.map { it to (strings.heuristicTitles[it]?.first ?: it.title) },
                        selected = heuristicType,
                        onSelect = { viewModel.setHeuristicType(it) }
                    )
                    Text(
                        text = strings.heuristicTitles[heuristicType]?.second ?: heuristicType.subtitle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                            Text(strings.autoPlaySpeedLabel, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                        Text(
                            text = "${autoPlaySpeed}×",
                            style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 14.sp, fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = autoPlaySpeed.toFloat(),
                        onValueChange = { viewModel.setAutoPlaySpeed(it.toInt()) },
                        valueRange = 1f..20f,
                        steps = 18,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text(strings.benchmarkTitle, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text(strings.benchmarkSubtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        FilledTonalButton(
                            onClick = { viewModel.runBenchmark() },
                            enabled = !isRunningBenchmark,
                            modifier = Modifier.heightIn(min = 44.dp)
                        ) {
                            if (isRunningBenchmark) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(strings.benchmarkRun, fontSize = 13.sp)
                            }
                        }
                    }

                    benchmarkResult?.let { res ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                    Text(
                                        text = strings.benchmarkSummary(res.elapsedTimeMs, res.totalMoves),
                                        style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = strings.benchmarkNodes(res.solvedCount, res.nodesEvaluated),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.primary
                                ) {
                                    Text(
                                        text = res.rating,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. TOOLS, CLIPBOARD & DATA
                SettingsSectionCard(
                    icon = Icons.Default.Info,
                    title = strings.sectionStatsData
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(strings.gamesWonLabel, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            text = "$gamesWon",
                            style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 15.sp, fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.exportBoardToClipboard(context) },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.copyBoard, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        OutlinedButton(
                            onClick = { viewModel.importBoardFromClipboard(context) },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.pasteBoard, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showResetRecordsDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.resetRecords, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        OutlinedButton(
                            onClick = { showResetSettingsDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.resetSettings, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 12.dp)
                        ) {
                            Text(
                                text = "SlideGrid • v${BuildConfig.VERSION_NAME}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = strings.localPrivacyNote,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        FilledTonalIconButton(
                            onClick = { viewModel.openRulesSheet() },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("settings_help_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                contentDescription = strings.helpDesc,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun SettingsSectionCard(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            content()
        }
    }
}

@Composable
private fun SettingsSubLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        ) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subtitle, fontSize = 12.sp, lineHeight = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun <T> SegmentedSelectorRow(
    items: List<Pair<T, String>>,
    selected: T,
    onSelect: (T) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        items.forEach { (item, label) ->
            val isSelected = (item == selected)
            Surface(
                shape = RoundedCornerShape(9.dp),
                color = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                border = if (isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 44.dp)
                    .clickable { onSelect(item) }
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
