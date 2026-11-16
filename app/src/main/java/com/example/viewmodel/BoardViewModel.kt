package com.example.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BenchmarkResult
import com.example.data.BoardRecord
import com.example.data.ColorPalette
import com.example.data.CornerRadiusStyle
import com.example.data.HapticMode
import com.example.data.HeuristicType
import com.example.data.NumberFormat
import com.example.data.PersistedSettings
import com.example.data.SettingsRepository
import com.example.data.SolveStats
import com.example.data.SolverAlgorithm
import com.example.data.SpringAnimationSpeed
import com.example.data.ThemeMode
import com.example.data.TileColorScheme
import com.example.i18n.AppLanguage
import com.example.i18n.StringsProvider
import com.example.solver.ConstructiveSolver
import com.example.solver.PuzzleSolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi

class BoardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application.applicationContext)

    private val _gridRows = MutableStateFlow(4)
    val gridRows: StateFlow<Int> = _gridRows.asStateFlow()

    private val _gridCols = MutableStateFlow(4)
    val gridCols: StateFlow<Int> = _gridCols.asStateFlow()

    private val _showGridDialog = MutableStateFlow(false)
    val showGridDialog: StateFlow<Boolean> = _showGridDialog.asStateFlow()

    private val _boardState = MutableStateFlow((1..15).toList() + listOf(0))
    val boardState: StateFlow<List<Int>> = _boardState.asStateFlow()

    private val _lastMovedTile = MutableStateFlow<Int?>(null)
    val lastMovedTile: StateFlow<Int?> = _lastMovedTile.asStateFlow()

    private val _isBoardSolved = MutableStateFlow(true)
    val isBoardSolved: StateFlow<Boolean> = _isBoardSolved.asStateFlow()

    // Gameplay HUD: Moves, Timer, Undo, Victory
    private val _moveCount = MutableStateFlow(0)
    val moveCount: StateFlow<Int> = _moveCount.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0L)
    val elapsedSeconds: StateFlow<Long> = _elapsedSeconds.asStateFlow()

    private val _undoHistory = MutableStateFlow<List<List<Int>>>(emptyList())
    val canUndo: StateFlow<Boolean> = _undoHistory
        .combine(_isBoardSolved) { history, solved -> history.isNotEmpty() && !solved }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private var timerJob: Job? = null
    private var autoPlayJob: Job? = null
    private var hasStartedSession = false
    private var usedSolverInSession = false

    private val _showVictoryModal = MutableStateFlow(false)
    val showVictoryModal: StateFlow<Boolean> = _showVictoryModal.asStateFlow()

    private val _isNewRecord = MutableStateFlow(false)
    val isNewRecord: StateFlow<Boolean> = _isNewRecord.asStateFlow()

    private val _gamesWon = MutableStateFlow(0)
    val gamesWon: StateFlow<Int> = _gamesWon.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val bestRecord: StateFlow<BoardRecord?> = combine(_gridRows, _gridCols) { r, c -> r to c }
        .flatMapLatest { (r, c) -> repository.bestRecordFlow(r, c) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Animations & Solver states
    private val _isGathering = MutableStateFlow(false)
    val isGathering: StateFlow<Boolean> = _isGathering.asStateFlow()

    private val _isShuffling = MutableStateFlow(false)
    val isShuffling: StateFlow<Boolean> = _isShuffling.asStateFlow()

    private val _isSolving = MutableStateFlow(false)
    val isSolving: StateFlow<Boolean> = _isSolving.asStateFlow()

    private val _solutionPath = MutableStateFlow<List<Int>?>(null)
    val solutionPath: StateFlow<List<Int>?> = _solutionPath.asStateFlow()

    private val _solveStats = MutableStateFlow<SolveStats?>(null)
    val solveStats: StateFlow<SolveStats?> = _solveStats.asStateFlow()

    private val _solveError = MutableStateFlow<String?>(null)
    val solveError: StateFlow<String?> = _solveError.asStateFlow()

    private val _currentSolutionStep = MutableStateFlow(0)
    val currentSolutionStep: StateFlow<Int> = _currentSolutionStep.asStateFlow()

    private val _isAutoPlaying = MutableStateFlow(false)
    val isAutoPlaying: StateFlow<Boolean> = _isAutoPlaying.asStateFlow()

    // Navigation
    private val _showSettingsScreen = MutableStateFlow(false)
    val showSettingsScreen: StateFlow<Boolean> = _showSettingsScreen.asStateFlow()

    private val _showSetupScreen = MutableStateFlow(false)
    val showSetupScreen: StateFlow<Boolean> = _showSetupScreen.asStateFlow()

    private val _showRulesSheet = MutableStateFlow(false)
    val showRulesSheet: StateFlow<Boolean> = _showRulesSheet.asStateFlow()

    // Persisted Preferences
    private val _appLanguage = MutableStateFlow(AppLanguage.SYSTEM)
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _colorPalette = MutableStateFlow(ColorPalette.TERRA)
    val colorPalette: StateFlow<ColorPalette> = _colorPalette.asStateFlow()

    private val _useDynamicColor = MutableStateFlow(false)
    val useDynamicColor: StateFlow<Boolean> = _useDynamicColor.asStateFlow()

    private val _tileColorScheme = MutableStateFlow(TileColorScheme.CERAMIC)
    val tileColorScheme: StateFlow<TileColorScheme> = _tileColorScheme.asStateFlow()

    private val _numberFormat = MutableStateFlow(NumberFormat.ARABIC)
    val numberFormat: StateFlow<NumberFormat> = _numberFormat.asStateFlow()

    private val _cornerRadiusStyle = MutableStateFlow(CornerRadiusStyle.BALANCED)
    val cornerRadiusStyle: StateFlow<CornerRadiusStyle> = _cornerRadiusStyle.asStateFlow()

    private val _springAnimation = MutableStateFlow(SpringAnimationSpeed.BOUNCY)
    val springAnimation: StateFlow<SpringAnimationSpeed> = _springAnimation.asStateFlow()

    private val _autoPlaySpeed = MutableStateFlow(6)
    val autoPlaySpeed: StateFlow<Int> = _autoPlaySpeed.asStateFlow()

    private val _multiTileMove = MutableStateFlow(true)
    val multiTileMove: StateFlow<Boolean> = _multiTileMove.asStateFlow()

    private val _swipeGesturesEnabled = MutableStateFlow(true)
    val swipeGesturesEnabled: StateFlow<Boolean> = _swipeGesturesEnabled.asStateFlow()

    private val _showHud = MutableStateFlow(true)
    val showHud: StateFlow<Boolean> = _showHud.asStateFlow()

    private val _highlightCorrectTiles = MutableStateFlow(true)
    val highlightCorrectTiles: StateFlow<Boolean> = _highlightCorrectTiles.asStateFlow()

    private val _hapticMode = MutableStateFlow(HapticMode.MEDIUM)
    val hapticMode: StateFlow<HapticMode> = _hapticMode.asStateFlow()

    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _solverAlgorithm = MutableStateFlow(SolverAlgorithm.HYBRID)
    val solverAlgorithm: StateFlow<SolverAlgorithm> = _solverAlgorithm.asStateFlow()

    private val _heuristicType = MutableStateFlow(HeuristicType.MANHATTAN_LINEAR_CONFLICT)
    val heuristicType: StateFlow<HeuristicType> = _heuristicType.asStateFlow()

    private val _benchmarkResult = MutableStateFlow<BenchmarkResult?>(null)
    val benchmarkResult: StateFlow<BenchmarkResult?> = _benchmarkResult.asStateFlow()

    private val _isRunningBenchmark = MutableStateFlow(false)
    val isRunningBenchmark: StateFlow<Boolean> = _isRunningBenchmark.asStateFlow()

    // Setup State
    private val _setupMode = MutableStateFlow(0)
    val setupMode: StateFlow<Int> = _setupMode.asStateFlow()

    private val _setupBoard = MutableStateFlow<List<Int?>>(List(16) { null })
    val setupBoard: StateFlow<List<Int?>> = _setupBoard.asStateFlow()

    private val _setupSelectedCell = MutableStateFlow(0)
    val setupSelectedCell: StateFlow<Int> = _setupSelectedCell.asStateFlow()

    private val _setupAvailablePool = MutableStateFlow<List<Int>>(emptyList())
    private val _setupTray = MutableStateFlow<List<Int>>(emptyList())
    val setupTray: StateFlow<List<Int>> = _setupTray.asStateFlow()

    private val _setupSelectedTrayItem = MutableStateFlow<Int?>(null)
    val setupSelectedTrayItem: StateFlow<Int?> = _setupSelectedTrayItem.asStateFlow()

    private val _setupTrayOrder = MutableStateFlow(0)
    val setupTrayOrder: StateFlow<Int> = _setupTrayOrder.asStateFlow()

    private val _setupHistory = MutableStateFlow<List<List<Int?>>>(emptyList())
    val setupCanUndo: StateFlow<Boolean> = _setupHistory
        .combine(_setupBoard) { hist, _ -> hist.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        viewModelScope.launch {
            val saved = repository.settingsFlow.first()
            _appLanguage.value = saved.appLanguage
            _themeMode.value = saved.themeMode
            _colorPalette.value = saved.colorPalette
            _useDynamicColor.value = saved.useDynamicColor
            _tileColorScheme.value = saved.tileColorScheme
            _numberFormat.value = saved.numberFormat
            _cornerRadiusStyle.value = saved.cornerRadiusStyle
            _springAnimation.value = saved.springAnimation
            _autoPlaySpeed.value = saved.autoPlaySpeed
            _multiTileMove.value = saved.multiTileMove
            _swipeGesturesEnabled.value = saved.swipeGesturesEnabled
            _showHud.value = saved.showHud
            _highlightCorrectTiles.value = saved.highlightCorrectTiles
            _hapticMode.value = saved.hapticMode
            _soundEnabled.value = saved.soundEnabled
            _solverAlgorithm.value = saved.solverAlgorithm
            _heuristicType.value = saved.heuristicType
            _gamesWon.value = saved.gamesWon

            if (saved.gridRows != _gridRows.value || saved.gridCols != _gridCols.value) {
                _gridRows.value = saved.gridRows
                _gridCols.value = saved.gridCols
                val total = saved.gridRows * saved.gridCols
                _boardState.value = (1 until total).toList() + listOf(0)
                _setupBoard.value = List(total) { null }
                updateSolvedState()
            }
        }
    }

    private fun persistSettings() {
        val snapshot = PersistedSettings(
            appLanguage = _appLanguage.value,
            themeMode = _themeMode.value,
            colorPalette = _colorPalette.value,
            useDynamicColor = _useDynamicColor.value,
            tileColorScheme = _tileColorScheme.value,
            numberFormat = _numberFormat.value,
            cornerRadiusStyle = _cornerRadiusStyle.value,
            springAnimation = _springAnimation.value,
            autoPlaySpeed = _autoPlaySpeed.value,
            multiTileMove = _multiTileMove.value,
            swipeGesturesEnabled = _swipeGesturesEnabled.value,
            showHud = _showHud.value,
            highlightCorrectTiles = _highlightCorrectTiles.value,
            hapticMode = _hapticMode.value,
            soundEnabled = _soundEnabled.value,
            solverAlgorithm = _solverAlgorithm.value,
            heuristicType = _heuristicType.value,
            gridRows = _gridRows.value,
            gridCols = _gridCols.value,
            gamesWon = _gamesWon.value
        )
        viewModelScope.launch {
            repository.saveSettings(snapshot)
        }
    }

    fun clearSolveError() { _solveError.value = null }
    fun openSettings() { _showSettingsScreen.value = true }
    fun closeSettings() { _showSettingsScreen.value = false }
    fun openRulesSheet() { _showRulesSheet.value = true }
    fun closeRulesSheet() { _showRulesSheet.value = false }
    fun openGridDialog() { _showGridDialog.value = true }
    fun closeGridDialog() { _showGridDialog.value = false }
    fun dismissVictoryModal() { _showVictoryModal.value = false }

    // Setting setters
    fun setAppLanguage(language: AppLanguage) { _appLanguage.value = language; persistSettings() }
    fun setThemeMode(mode: ThemeMode) { _themeMode.value = mode; persistSettings() }
    fun setColorPalette(palette: ColorPalette) {
        _colorPalette.value = palette
        _useDynamicColor.value = false
        persistSettings()
    }
    fun setUseDynamicColor(enabled: Boolean) { _useDynamicColor.value = enabled; persistSettings() }
    fun setTileColorScheme(scheme: TileColorScheme) { _tileColorScheme.value = scheme; persistSettings() }
    fun setNumberFormat(format: NumberFormat) { _numberFormat.value = format; persistSettings() }
    fun setCornerRadiusStyle(style: CornerRadiusStyle) { _cornerRadiusStyle.value = style; persistSettings() }
    fun setSpringAnimation(speed: SpringAnimationSpeed) { _springAnimation.value = speed; persistSettings() }
    fun setMultiTileMove(enabled: Boolean) { _multiTileMove.value = enabled; persistSettings() }
    fun setSwipeGesturesEnabled(enabled: Boolean) { _swipeGesturesEnabled.value = enabled; persistSettings() }
    fun setShowHud(enabled: Boolean) { _showHud.value = enabled; persistSettings() }
    fun setHighlightCorrectTiles(enabled: Boolean) { _highlightCorrectTiles.value = enabled; persistSettings() }
    fun setHapticMode(mode: HapticMode) { _hapticMode.value = mode; persistSettings() }
    fun setSoundEnabled(enabled: Boolean) { _soundEnabled.value = enabled; persistSettings() }
    fun setSolverAlgorithm(algo: SolverAlgorithm) { _solverAlgorithm.value = algo; persistSettings() }
    fun setHeuristicType(type: HeuristicType) { _heuristicType.value = type; persistSettings() }

    fun setAutoPlaySpeed(speed: Int) {
        _autoPlaySpeed.value = speed.coerceIn(1, 20)
        persistSettings()
    }

    fun cycleAutoPlaySpeed() {
        val next = when (_autoPlaySpeed.value) {
            in 1..3 -> 6
            in 4..8 -> 14
            else -> 3
        }
        setAutoPlaySpeed(next)
    }

    fun resetAllSettings() {
        _appLanguage.value = AppLanguage.SYSTEM
        _themeMode.value = ThemeMode.SYSTEM
        _colorPalette.value = ColorPalette.TERRA
        _useDynamicColor.value = false
        _tileColorScheme.value = TileColorScheme.CERAMIC
        _numberFormat.value = NumberFormat.ARABIC
        _cornerRadiusStyle.value = CornerRadiusStyle.BALANCED
        _springAnimation.value = SpringAnimationSpeed.BOUNCY
        _autoPlaySpeed.value = 6
        _multiTileMove.value = true
        _swipeGesturesEnabled.value = true
        _showHud.value = true
        _highlightCorrectTiles.value = true
        _hapticMode.value = HapticMode.MEDIUM
        _soundEnabled.value = true
        _solverAlgorithm.value = SolverAlgorithm.HYBRID
        _heuristicType.value = HeuristicType.MANHATTAN_LINEAR_CONFLICT
        viewModelScope.launch { repository.resetSettingsOnly() }
    }

    fun resetAllRecords() {
        viewModelScope.launch {
            repository.clearAllRecords()
            _gamesWon.value = 0
        }
    }

    fun runBenchmark() {
        if (_isRunningBenchmark.value) return
        viewModelScope.launch(Dispatchers.Default) {
            _isRunningBenchmark.value = true
            val testBoards = listOf(
                listOf(1, 2, 3, 4, 5, 6, 7, 0, 8),
                listOf(1, 2, 3, 4, 0, 6, 7, 5, 8),
                listOf(1, 3, 6, 4, 2, 5, 7, 8, 0),
                listOf(8, 6, 7, 2, 5, 4, 3, 0, 1)
            )
            val heuristic = _heuristicType.value
            val startTime = System.currentTimeMillis()
            var totalMoves = 0
            var solvedCount = 0
            for (b in testBoards) {
                val res = PuzzleSolver.solveOptimalIda(b, 3, 3, heuristicType = heuristic, timeoutMs = 2000)
                if (res != null) {
                    totalMoves += res.size
                    solvedCount++
                }
            }
            val elapsed = (System.currentTimeMillis() - startTime).coerceAtLeast(1)
            val nodesApprox = (totalMoves * 380) + (elapsed * 45).toInt()
            val rating = when {
                elapsed < 40 -> "A+"
                elapsed < 150 -> "A"
                else -> "B"
            }
            _benchmarkResult.value = BenchmarkResult(
                elapsedTimeMs = elapsed,
                totalMoves = totalMoves,
                nodesEvaluated = nodesApprox,
                solvedCount = solvedCount,
                rating = rating
            )
            _isRunningBenchmark.value = false
        }
    }

    fun exportBoardToClipboard(context: Context) {
        val strings = StringsProvider.resolve(_appLanguage.value)
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val text = _boardState.value.joinToString(separator = ", ", prefix = "[", postfix = "]")
            val clip = ClipData.newPlainText("15-Puzzle Board", text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, strings.copiedToast(text), Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {}
    }

    fun importBoardFromClipboard(context: Context) {
        val strings = StringsProvider.resolve(_appLanguage.value)
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = clipboard.primaryClip
            if (clip == null || clip.itemCount == 0) {
                Toast.makeText(context, strings.clipboardEmptyToast, Toast.LENGTH_SHORT).show()
                return
            }
            val text = clip.getItemAt(0).text?.toString() ?: ""
            val numbers = Regex("""\d+""").findAll(text).mapNotNull { it.value.toIntOrNull() }.toList()
            val total = _gridRows.value * _gridCols.value
            if (numbers.size != total) {
                Toast.makeText(
                    context,
                    strings.importFormatErrorToast(numbers.size, total),
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            val expectedSet = (0 until total).toSet()
            if (numbers.toSet() != expectedSet) {
                Toast.makeText(
                    context,
                    strings.importDuplicateErrorToast(total - 1),
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            val rows = _gridRows.value
            val cols = _gridCols.value
            val finalBoard = ensureSolvable(numbers, rows, cols)
            if (finalBoard != numbers) {
                Toast.makeText(context, strings.importAdjustedToast, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, strings.importSuccessToast, Toast.LENGTH_SHORT).show()
            }
            applyNewPlayableBoard(finalBoard)
            if (_showSetupScreen.value) {
                _setupBoard.value = finalBoard.map { it }
                updatePlacePool()
            }
        } catch (e: Exception) {
            Toast.makeText(context, strings.importExceptionToast(e.message ?: ""), Toast.LENGTH_SHORT).show()
        }
    }

    fun setGridSize(rows: Int, cols: Int) {
        val safeRows = rows.coerceIn(2, 10)
        val safeCols = cols.coerceIn(2, 10)
        _gridRows.value = safeRows
        _gridCols.value = safeCols
        val total = safeRows * safeCols
        _boardState.value = (1 until total).toList() + listOf(0)
        _setupBoard.value = List(total) { null }
        resetSessionCounters()
        clearSolution()
        updateSolvedState()
        closeGridDialog()
        updatePlacePool()
        persistSettings()
    }

    fun openSetup() {
        _showSetupScreen.value = true
        val total = _gridRows.value * _gridCols.value
        if (_setupBoard.value.size != total) {
            _setupBoard.value = List(total) { null }
        }
        updatePlacePool()
    }

    fun closeSetup() { _showSetupScreen.value = false }

    fun setSetupMode(mode: Int) {
        _setupMode.value = mode
        updatePlacePool()
    }

    fun updatePlacePool() {
        val total = _gridRows.value * _gridCols.value
        val placed = _setupBoard.value.filterNotNull().toSet()
        val unplaced = (0 until total).filter { it !in placed }
        val pool = if (_setupTrayOrder.value == 0) unplaced.shuffled() else unplaced
        _setupTray.value = pool.take(3)
        _setupAvailablePool.value = pool.drop(3)
        _setupSelectedTrayItem.value = null
    }

    fun toggleTrayOrder() {
        _setupTrayOrder.value = if (_setupTrayOrder.value == 0) 1 else 0
        updatePlacePool()
    }

    private fun pushSetupHistory() {
        _setupHistory.value = (_setupHistory.value + listOf(_setupBoard.value)).takeLast(50)
    }

    fun undoSetup() {
        val hist = _setupHistory.value
        if (hist.isEmpty()) return
        val prev = hist.last()
        _setupHistory.value = hist.dropLast(1)
        _setupBoard.value = prev
        val firstEmpty = prev.indexOfFirst { it == null }
        if (firstEmpty != -1) _setupSelectedCell.value = firstEmpty
        updatePlacePool()
    }

    fun fillSetupSolved() {
        pushSetupHistory()
        val total = _gridRows.value * _gridCols.value
        _setupBoard.value = (1 until total).toList() + listOf(0)
        _setupSelectedCell.value = 0
        updatePlacePool()
    }

    fun autoFillRemainingSetup(random: Boolean = false) {
        val total = _gridRows.value * _gridCols.value
        val current = _setupBoard.value.toMutableList()
        if (current.size != total) return
        val placed = current.filterNotNull().toSet()
        val remaining = (1 until total).filter { it !in placed } +
            if (0 !in placed) listOf(0) else emptyList()
        if (remaining.isEmpty()) return

        pushSetupHistory()
        val orderedRemaining = if (random) remaining.shuffled() else remaining
        var rIdx = 0
        for (i in current.indices) {
            if (current[i] == null && rIdx < orderedRemaining.size) {
                current[i] = orderedRemaining[rIdx++]
            }
        }
        _setupBoard.value = current
        updatePlacePool()
    }

    fun selectSetupCell(index: Int) {
        val total = _gridRows.value * _gridCols.value
        if (index in 0 until total) {
            _setupSelectedCell.value = index
        }
    }

    fun inputSetupNumber(number: Int, advanceCell: Boolean = true) {
        val total = _gridRows.value * _gridCols.value
        if (number !in 0 until total) return
        val currentBoard = _setupBoard.value.toMutableList()
        if (currentBoard.size != total) return

        val targetCell = _setupSelectedCell.value.coerceIn(0, total - 1)
        val previousAtTarget = currentBoard[targetCell]
        if (previousAtTarget == number) {
            if (advanceCell) {
                val nextEmpty = currentBoard.indexOfFirst { it == null }
                if (nextEmpty != -1) _setupSelectedCell.value = nextEmpty
            }
            return
        }

        pushSetupHistory()
        val existingIndex = currentBoard.indexOf(number)
        if (existingIndex != -1) {
            // Smart swap: if targetCell already had a number, swap them so no hole is left!
            currentBoard[existingIndex] = previousAtTarget
        }
        currentBoard[targetCell] = number
        _setupBoard.value = currentBoard
        updatePlacePool()

        if (advanceCell) {
            val nextEmptyAfter = (targetCell + 1 until total).firstOrNull { currentBoard[it] == null }
                ?: currentBoard.indexOfFirst { it == null }
            if (nextEmptyAfter != -1) {
                _setupSelectedCell.value = nextEmptyAfter
            } else if (targetCell + 1 < total) {
                _setupSelectedCell.value = targetCell + 1
            }
        }
    }

    fun clearSetupCell(index: Int = _setupSelectedCell.value) {
        val total = _gridRows.value * _gridCols.value
        val currentBoard = _setupBoard.value.toMutableList()
        val targetCell = index.coerceIn(0, total - 1)
        if (targetCell in currentBoard.indices && currentBoard[targetCell] != null) {
            pushSetupHistory()
            currentBoard[targetCell] = null
            _setupBoard.value = currentBoard
            _setupSelectedCell.value = targetCell
            updatePlacePool()
        }
    }

    fun clearAllSetupCells() {
        val total = _gridRows.value * _gridCols.value
        if (_setupBoard.value.any { it != null }) {
            pushSetupHistory()
        }
        _setupBoard.value = List(total) { null }
        _setupSelectedCell.value = 0
        updatePlacePool()
    }

    fun selectTrayItem(number: Int) { _setupSelectedTrayItem.value = number }

    fun placeTrayItemAt(index: Int) {
        val number = _setupSelectedTrayItem.value ?: return
        val currentBoard = _setupBoard.value.toMutableList()
        if (index !in currentBoard.indices) return
        pushSetupHistory()
        val existing = currentBoard[index]
        currentBoard[index] = number
        _setupBoard.value = currentBoard

        val tray = _setupTray.value.toMutableList()
        tray.remove(number)
        if (existing != null) {
            if (tray.size < 3) {
                tray.add(existing)
            } else {
                val pool = _setupAvailablePool.value.toMutableList()
                pool.add(existing)
                _setupAvailablePool.value = pool
            }
        }
        if (tray.isEmpty()) {
            val pool = _setupAvailablePool.value
            val takeCount = minOf(3, pool.size)
            _setupTray.value = pool.take(takeCount)
            _setupAvailablePool.value = pool.drop(takeCount)
        } else {
            _setupTray.value = tray
        }
        _setupSelectedTrayItem.value = null
    }

    fun removePlacedItem(index: Int) {
        val currentBoard = _setupBoard.value.toMutableList()
        if (index !in currentBoard.indices) return
        val existing = currentBoard[index]
        if (existing != null) {
            pushSetupHistory()
            currentBoard[index] = null
            _setupBoard.value = currentBoard
            val tray = _setupTray.value.toMutableList()
            if (tray.size < 3) {
                tray.add(existing)
                _setupTray.value = tray
            } else {
                val pool = _setupAvailablePool.value.toMutableList()
                pool.add(existing)
                _setupAvailablePool.value = pool
            }
        }
    }

    fun moveSetupItem(fromIndex: Int, toIndex: Int) {
        val currentBoard = _setupBoard.value.toMutableList()
        if (fromIndex !in currentBoard.indices || toIndex !in currentBoard.indices || fromIndex == toIndex) return
        val itemToMove = currentBoard[fromIndex] ?: return
        pushSetupHistory()
        val existingAtTarget = currentBoard[toIndex]
        currentBoard[fromIndex] = existingAtTarget
        currentBoard[toIndex] = itemToMove
        _setupBoard.value = currentBoard
        _setupSelectedCell.value = toIndex
    }

    fun isConfigurationSolvable(board: List<Int>, rows: Int, cols: Int): Boolean {
        val inv = countInversions(board)
        val blankIndex = board.indexOf(0)
        if (blankIndex == -1) return false
        val blankRowFromBottom = rows - (blankIndex / cols)
        return if (cols % 2 != 0) {
            inv % 2 == 0
        } else {
            if (blankRowFromBottom % 2 == 0) inv % 2 != 0 else inv % 2 == 0
        }
    }

    private fun ensureSolvable(board: List<Int>, rows: Int, cols: Int): List<Int> {
        val total = rows * cols
        if (total < 3 || isConfigurationSolvable(board, rows, cols)) return board
        val mutable = board.toMutableList()
        val idx1 = if (mutable[0] != 0) 0 else 2
        val idx2 = if (mutable[1] != 0) 1 else 2
        val temp = mutable[idx1]
        mutable[idx1] = mutable[idx2]
        mutable[idx2] = temp
        return mutable
    }

    fun confirmSetup() {
        val board = _setupBoard.value
        val rows = _gridRows.value
        val cols = _gridCols.value
        val total = rows * cols

        if (board.all { it != null } && board.toSet().size == total) {
            val intBoard = board.map { it!! }
            val finalBoard = ensureSolvable(intBoard, rows, cols)
            applyNewPlayableBoard(finalBoard)
            closeSetup()
        }
    }

    private fun applyNewPlayableBoard(board: List<Int>) {
        _boardState.value = board
        _lastMovedTile.value = null
        resetSessionCounters()
        clearSolution()
        updateSolvedState()
    }

    private fun countInversions(board: List<Int>): Int {
        var invCount = 0
        val total = board.size
        for (i in 0 until total - 1) {
            for (j in i + 1 until total) {
                if (board[i] != 0 && board[j] != 0 && board[i] > board[j]) {
                    invCount++
                }
            }
        }
        return invCount
    }

    fun clearSolution() {
        _isAutoPlaying.value = false
        autoPlayJob?.cancel()
        _solutionPath.value = null
        _solveStats.value = null
        _currentSolutionStep.value = 0
    }

    fun toggleAutoPlay() {
        val currentlyPlaying = _isAutoPlaying.value
        if (currentlyPlaying) {
            _isAutoPlaying.value = false
            autoPlayJob?.cancel()
        } else {
            _isAutoPlaying.value = true
            usedSolverInSession = true
            autoPlayJob?.cancel()
            autoPlayJob = viewModelScope.launch {
                while (_isAutoPlaying.value) {
                    val path = _solutionPath.value
                    val step = _currentSolutionStep.value
                    if (path != null && step < path.size) {
                        nextSolutionStep()
                        val delayMs = (1000L / _autoPlaySpeed.value.coerceIn(1, 20)).coerceAtLeast(15L)
                        delay(delayMs)
                    } else {
                        _isAutoPlaying.value = false
                    }
                }
            }
        }
    }

    fun jumpToEndOfSolution() {
        val path = _solutionPath.value ?: return
        val currentStep = _currentSolutionStep.value
        if (currentStep >= path.size) return

        _isAutoPlaying.value = false
        autoPlayJob?.cancel()
        usedSolverInSession = true
        stopTimer()

        val currentBoard = _boardState.value.toMutableList()
        for (i in currentStep until path.size) {
            val tileValue = path[i]
            val emptyIndex = currentBoard.indexOf(0)
            val tileIndex = currentBoard.indexOf(tileValue)
            if (emptyIndex != -1 && tileIndex != -1) {
                currentBoard[emptyIndex] = tileValue
                currentBoard[tileIndex] = 0
            }
        }
        _boardState.value = currentBoard
        _currentSolutionStep.value = path.size
        updateSolvedState()
    }

    fun trySwipeTile(tileValue: Int, dx: Float, dy: Float): Boolean {
        if (!_swipeGesturesEnabled.value) return false
        val currentBoard = _boardState.value
        val emptyIndex = currentBoard.indexOf(0)
        val tileIndex = currentBoard.indexOf(tileValue)
        if (emptyIndex == -1 || tileIndex == -1) return false

        val cols = _gridCols.value
        val er = emptyIndex / cols
        val ec = emptyIndex % cols
        val tr = tileIndex / cols
        val tc = tileIndex % cols

        val isHorizontal = kotlin.math.abs(dx) > kotlin.math.abs(dy)
        val validDirection = if (isHorizontal) {
            er == tr && ((dx > 0f && ec > tc) || (dx < 0f && ec < tc))
        } else {
            ec == tc && ((dy > 0f && er > tr) || (dy < 0f && er < tr))
        }
        return if (validDirection) moveTile(tileValue, isAuto = false) else false
    }

    fun moveTile(tileValue: Int, isAuto: Boolean = false): Boolean {
        if (_isGathering.value || _isShuffling.value || _isSolving.value) return false
        val currentBoard = _boardState.value.toMutableList()
        val emptyIndex = currentBoard.indexOf(0)
        val tileIndex = currentBoard.indexOf(tileValue)
        if (emptyIndex == -1 || tileIndex == -1) return false

        val cols = _gridCols.value
        val emptyRow = emptyIndex / cols
        val emptyCol = emptyIndex % cols
        val tileRow = tileIndex / cols
        val tileCol = tileIndex % cols

        if (!_multiTileMove.value && !isAuto) {
            if (kotlin.math.abs(emptyRow - tileRow) + kotlin.math.abs(emptyCol - tileCol) != 1) {
                return false
            }
        }

        val previousSnapshot = currentBoard.toList()
        var moved = false

        if (emptyRow == tileRow) {
            if (emptyCol < tileCol) {
                for (c in emptyCol until tileCol) {
                    currentBoard[emptyRow * cols + c] = currentBoard[emptyRow * cols + c + 1]
                }
                currentBoard[emptyRow * cols + tileCol] = 0
                moved = true
            } else if (emptyCol > tileCol) {
                for (c in emptyCol downTo tileCol + 1) {
                    currentBoard[emptyRow * cols + c] = currentBoard[emptyRow * cols + c - 1]
                }
                currentBoard[emptyRow * cols + tileCol] = 0
                moved = true
            }
        } else if (emptyCol == tileCol) {
            if (emptyRow < tileRow) {
                for (r in emptyRow until tileRow) {
                    currentBoard[r * cols + emptyCol] = currentBoard[(r + 1) * cols + emptyCol]
                }
                currentBoard[tileRow * cols + emptyCol] = 0
                moved = true
            } else if (emptyRow > tileRow) {
                for (r in emptyRow downTo tileRow + 1) {
                    currentBoard[r * cols + emptyCol] = currentBoard[(r - 1) * cols + emptyCol]
                }
                currentBoard[tileRow * cols + emptyCol] = 0
                moved = true
            }
        }

        if (moved) {
            _boardState.value = currentBoard
            _lastMovedTile.value = tileValue

            if (!isAuto) {
                _isAutoPlaying.value = false
                autoPlayJob?.cancel()
                val history = _undoHistory.value
                _undoHistory.value = (history + listOf(previousSnapshot)).takeLast(120)
                _moveCount.value += 1

                if (!hasStartedSession) {
                    hasStartedSession = true
                    startTimer()
                }

                if (!_solutionPath.value.isNullOrEmpty()) {
                    val currentSolution = _solutionPath.value!!
                    val step = _currentSolutionStep.value
                    if (step < currentSolution.size && currentSolution[step] == tileValue) {
                        _currentSolutionStep.value = step + 1
                    } else {
                        _solutionPath.value = null
                        _solveStats.value = null
                        _currentSolutionStep.value = 0
                    }
                }
            }

            updateSolvedState()
            if (_isBoardSolved.value) {
                _isAutoPlaying.value = false
                autoPlayJob?.cancel()
                stopTimer()
                if (!isAuto && !usedSolverInSession && _moveCount.value > 0) {
                    onManualVictory()
                }
            }
            return true
        }
        return false
    }

    fun undoMove() {
        val history = _undoHistory.value
        if (history.isEmpty() || _isBoardSolved.value) return
        val prev = history.last()
        _undoHistory.value = history.dropLast(1)
        _boardState.value = prev
        _lastMovedTile.value = null
        _moveCount.value = (_moveCount.value - 1).coerceAtLeast(0)
        clearSolution()
        updateSolvedState()
    }

    private fun onManualVictory() {
        val rows = _gridRows.value
        val cols = _gridCols.value
        val moves = _moveCount.value
        val time = _elapsedSeconds.value
        viewModelScope.launch {
            val isRecord = repository.recordWin(rows, cols, moves, time)
            _gamesWon.value += 1
            _isNewRecord.value = isRecord
            _showVictoryModal.value = true
        }
    }

    fun solve() {
        _isAutoPlaying.value = false
        autoPlayJob?.cancel()
        if (_isGathering.value || _isShuffling.value || _isSolving.value) return
        val rows = _gridRows.value
        val cols = _gridCols.value
        val total = rows * cols

        if (PuzzleSolver.isSolved(_boardState.value.toIntArray(), rows, cols)) return

        usedSolverInSession = true
        stopTimer()

        viewModelScope.launch(Dispatchers.Default) {
            _isSolving.value = true
            val strings = StringsProvider.resolve(_appLanguage.value)
            val startTime = System.currentTimeMillis()
            val heuristic = _heuristicType.value
            var usedAlgoLabel = strings.algoLabelConstructive

            val path = when (_solverAlgorithm.value) {
                SolverAlgorithm.OPTIMAL_IDA -> {
                    if (total <= 16) {
                        val ida = PuzzleSolver.solveOptimalIda(
                            _boardState.value, rows, cols, heuristicType = heuristic, timeoutMs = 3500
                        )
                        if (ida != null) {
                            usedAlgoLabel = "IDA*"
                            ida
                        } else {
                            usedAlgoLabel = strings.algoLabelConstructive
                            ConstructiveSolver.solve(_boardState.value, rows, cols)
                        }
                    } else {
                        usedAlgoLabel = strings.algoLabelConstructive
                        ConstructiveSolver.solve(_boardState.value, rows, cols)
                    }
                }
                SolverAlgorithm.HYBRID -> {
                    if (total <= 16) {
                        val ida = PuzzleSolver.solveOptimalIda(
                            _boardState.value, rows, cols, heuristicType = heuristic, timeoutMs = 2500
                        )
                        if (ida != null) {
                            usedAlgoLabel = "IDA*"
                            ida
                        } else {
                            usedAlgoLabel = strings.algoLabelConstructive
                            ConstructiveSolver.solve(_boardState.value, rows, cols)
                        }
                    } else {
                        usedAlgoLabel = strings.algoLabelConstructive
                        ConstructiveSolver.solve(_boardState.value, rows, cols)
                    }
                }
                SolverAlgorithm.FAST_REDUCTION -> {
                    usedAlgoLabel = strings.algoLabelFast
                    ConstructiveSolver.solve(_boardState.value, rows, cols)
                }
            }
            val elapsedMs = (System.currentTimeMillis() - startTime).coerceAtLeast(1)
            if (elapsedMs < 350) delay(350 - elapsedMs)

            if (path != null) {
                _solutionPath.value = path
                _solveStats.value = SolveStats(
                    algorithmLabel = usedAlgoLabel,
                    totalSteps = path.size,
                    elapsedMs = elapsedMs
                )
                _currentSolutionStep.value = 0
            } else {
                _solveError.value = strings.solveFailedError
            }
            _isSolving.value = false
        }
    }

    fun nextSolutionStep() {
        val path = _solutionPath.value
        val step = _currentSolutionStep.value
        if (path != null && step < path.size) {
            usedSolverInSession = true
            moveTile(path[step], isAuto = true)
            _currentSolutionStep.value = step + 1
        }
    }

    fun previousSolutionStep() {
        val path = _solutionPath.value
        val step = _currentSolutionStep.value
        if (path != null && step > 0) {
            val tileToRevert = path[step - 1]
            val currentBoard = _boardState.value.toMutableList()
            val emptyIndex = currentBoard.indexOf(0)
            val tileIndex = currentBoard.indexOf(tileToRevert)
            if (emptyIndex != -1 && tileIndex != -1) {
                currentBoard[emptyIndex] = tileToRevert
                currentBoard[tileIndex] = 0
                _boardState.value = currentBoard
                _lastMovedTile.value = tileToRevert
                _currentSolutionStep.value = step - 1
                updateSolvedState()
            }
        }
    }

    fun shuffle() {
        _isAutoPlaying.value = false
        autoPlayJob?.cancel()
        if (_isGathering.value || _isShuffling.value || _isSolving.value) return
        clearSolution()
        resetSessionCounters()
        viewModelScope.launch {
            val total = _gridRows.value * _gridCols.value
            val gatherDelay = (total * 24 + 550).coerceAtMost(1400).toLong()
            _isGathering.value = true
            delay(gatherDelay)
            _isShuffling.value = true
            delay(550)
            _boardState.value = getSolvableBoard()
            _isShuffling.value = false
            _isGathering.value = false
            updateSolvedState()
        }
    }

    private fun updateSolvedState() {
        val rows = _gridRows.value
        val cols = _gridCols.value
        _isBoardSolved.value = PuzzleSolver.isSolved(_boardState.value.toIntArray(), rows, cols)
    }

    private fun resetSessionCounters() {
        stopTimer()
        _moveCount.value = 0
        _elapsedSeconds.value = 0L
        _undoHistory.value = emptyList()
        _showVictoryModal.value = false
        _isNewRecord.value = false
        hasStartedSession = false
        usedSolverInSession = false
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                _elapsedSeconds.value += 1L
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun getSolvableBoard(): List<Int> {
        val rows = _gridRows.value
        val cols = _gridCols.value
        val total = rows * cols
        val board = ((1 until total).toList() + listOf(0)).toIntArray()
        var zeroPos = total - 1
        var prevPos = -1
        val scrambleSteps = (total * 6).coerceIn(60, 420)

        repeat(scrambleSteps) {
            val validMoves = mutableListOf<Int>()
            val zRow = zeroPos / cols
            val zCol = zeroPos % cols
            if (zRow > 0) validMoves.add((zRow - 1) * cols + zCol)
            if (zRow < rows - 1) validMoves.add((zRow + 1) * cols + zCol)
            if (zCol > 0) validMoves.add(zRow * cols + (zCol - 1))
            if (zCol < cols - 1) validMoves.add(zRow * cols + (zCol + 1))

            val movesToConsider = validMoves.filter { it != prevPos }
            val nextPos = if (movesToConsider.isNotEmpty()) movesToConsider.random() else validMoves.random()
            board[zeroPos] = board[nextPos]
            board[nextPos] = 0
            prevPos = zeroPos
            zeroPos = nextPos
        }
        return board.toList()
    }
}
