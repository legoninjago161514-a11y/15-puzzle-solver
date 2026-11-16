package com.example.data

import android.content.Context
import com.example.i18n.AppLanguage
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "fifteen_puzzle_prefs")

data class PersistedSettings(
    val appLanguage: AppLanguage = AppLanguage.SYSTEM,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val colorPalette: ColorPalette = ColorPalette.TERRA,
    val useDynamicColor: Boolean = false,
    val tileColorScheme: TileColorScheme = TileColorScheme.CERAMIC,
    val numberFormat: NumberFormat = NumberFormat.ARABIC,
    val cornerRadiusStyle: CornerRadiusStyle = CornerRadiusStyle.BALANCED,
    val springAnimation: SpringAnimationSpeed = SpringAnimationSpeed.BOUNCY,
    val autoPlaySpeed: Int = 6,
    val multiTileMove: Boolean = true,
    val swipeGesturesEnabled: Boolean = true,
    val showHud: Boolean = true,
    val highlightCorrectTiles: Boolean = true,
    val hapticMode: HapticMode = HapticMode.MEDIUM,
    val soundEnabled: Boolean = true,
    val solverAlgorithm: SolverAlgorithm = SolverAlgorithm.HYBRID,
    val heuristicType: HeuristicType = HeuristicType.MANHATTAN_LINEAR_CONFLICT,
    val gridRows: Int = 4,
    val gridCols: Int = 4,
    val gamesWon: Int = 0
)

class SettingsRepository(private val context: Context) {

    private object Keys {
        val APP_LANGUAGE = stringPreferencesKey("app_language")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val COLOR_PALETTE = stringPreferencesKey("color_palette")
        val USE_DYNAMIC_COLOR = booleanPreferencesKey("use_dynamic_color")
        val TILE_COLOR_SCHEME = stringPreferencesKey("tile_color_scheme")
        val NUMBER_FORMAT = stringPreferencesKey("number_format")
        val CORNER_RADIUS_STYLE = stringPreferencesKey("corner_radius_style")
        val SPRING_ANIMATION = stringPreferencesKey("spring_animation")
        val AUTO_PLAY_SPEED = intPreferencesKey("auto_play_speed")
        val MULTI_TILE_MOVE = booleanPreferencesKey("multi_tile_move")
        val SWIPE_GESTURES = booleanPreferencesKey("swipe_gestures")
        val SHOW_HUD = booleanPreferencesKey("show_hud")
        val HIGHLIGHT_CORRECT = booleanPreferencesKey("highlight_correct")
        val HAPTIC_MODE = stringPreferencesKey("haptic_mode")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val SOLVER_ALGORITHM = stringPreferencesKey("solver_algorithm")
        val HEURISTIC_TYPE = stringPreferencesKey("heuristic_type")
        val GRID_ROWS = intPreferencesKey("grid_rows")
        val GRID_COLS = intPreferencesKey("grid_cols")
        val GAMES_WON = intPreferencesKey("games_won")
    }

    val settingsFlow: Flow<PersistedSettings> = context.dataStore.data.map { prefs ->
        PersistedSettings(
            appLanguage = enumOrDefault(prefs[Keys.APP_LANGUAGE], AppLanguage.SYSTEM),
            themeMode = enumOrDefault(prefs[Keys.THEME_MODE], ThemeMode.SYSTEM),
            colorPalette = enumOrDefault(prefs[Keys.COLOR_PALETTE], ColorPalette.TERRA),
            useDynamicColor = prefs[Keys.USE_DYNAMIC_COLOR] ?: false,
            tileColorScheme = enumOrDefault(prefs[Keys.TILE_COLOR_SCHEME], TileColorScheme.CERAMIC),
            numberFormat = enumOrDefault(prefs[Keys.NUMBER_FORMAT], NumberFormat.ARABIC),
            cornerRadiusStyle = enumOrDefault(prefs[Keys.CORNER_RADIUS_STYLE], CornerRadiusStyle.BALANCED),
            springAnimation = enumOrDefault(prefs[Keys.SPRING_ANIMATION], SpringAnimationSpeed.BOUNCY),
            autoPlaySpeed = (prefs[Keys.AUTO_PLAY_SPEED] ?: 6).coerceIn(1, 20),
            multiTileMove = prefs[Keys.MULTI_TILE_MOVE] ?: true,
            swipeGesturesEnabled = prefs[Keys.SWIPE_GESTURES] ?: true,
            showHud = prefs[Keys.SHOW_HUD] ?: true,
            highlightCorrectTiles = prefs[Keys.HIGHLIGHT_CORRECT] ?: true,
            hapticMode = enumOrDefault(prefs[Keys.HAPTIC_MODE], HapticMode.MEDIUM),
            soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
            solverAlgorithm = enumOrDefault(prefs[Keys.SOLVER_ALGORITHM], SolverAlgorithm.HYBRID),
            heuristicType = enumOrDefault(prefs[Keys.HEURISTIC_TYPE], HeuristicType.MANHATTAN_LINEAR_CONFLICT),
            gridRows = (prefs[Keys.GRID_ROWS] ?: 4).coerceIn(2, 10),
            gridCols = (prefs[Keys.GRID_COLS] ?: 4).coerceIn(2, 10),
            gamesWon = prefs[Keys.GAMES_WON] ?: 0
        )
    }

    fun bestRecordFlow(rows: Int, cols: Int): Flow<BoardRecord?> {
        val movesKey = intPreferencesKey("best_moves_${rows}x${cols}")
        val timeKey = longPreferencesKey("best_time_${rows}x${cols}")
        return context.dataStore.data.map { prefs ->
            val moves = prefs[movesKey]
            val time = prefs[timeKey]
            if (moves != null && time != null) {
                BoardRecord(bestMoves = moves, bestTimeSeconds = time)
            } else {
                null
            }
        }
    }

    suspend fun saveSettings(settings: PersistedSettings) {
        context.dataStore.edit { prefs ->
            prefs[Keys.APP_LANGUAGE] = settings.appLanguage.name
            prefs[Keys.THEME_MODE] = settings.themeMode.name
            prefs[Keys.COLOR_PALETTE] = settings.colorPalette.name
            prefs[Keys.USE_DYNAMIC_COLOR] = settings.useDynamicColor
            prefs[Keys.TILE_COLOR_SCHEME] = settings.tileColorScheme.name
            prefs[Keys.NUMBER_FORMAT] = settings.numberFormat.name
            prefs[Keys.CORNER_RADIUS_STYLE] = settings.cornerRadiusStyle.name
            prefs[Keys.SPRING_ANIMATION] = settings.springAnimation.name
            prefs[Keys.AUTO_PLAY_SPEED] = settings.autoPlaySpeed
            prefs[Keys.MULTI_TILE_MOVE] = settings.multiTileMove
            prefs[Keys.SWIPE_GESTURES] = settings.swipeGesturesEnabled
            prefs[Keys.SHOW_HUD] = settings.showHud
            prefs[Keys.HIGHLIGHT_CORRECT] = settings.highlightCorrectTiles
            prefs[Keys.HAPTIC_MODE] = settings.hapticMode.name
            prefs[Keys.SOUND_ENABLED] = settings.soundEnabled
            prefs[Keys.SOLVER_ALGORITHM] = settings.solverAlgorithm.name
            prefs[Keys.HEURISTIC_TYPE] = settings.heuristicType.name
            prefs[Keys.GRID_ROWS] = settings.gridRows
            prefs[Keys.GRID_COLS] = settings.gridCols
        }
    }

    suspend fun recordWin(rows: Int, cols: Int, moves: Int, timeSeconds: Long): Boolean {
        val movesKey = intPreferencesKey("best_moves_${rows}x${cols}")
        val timeKey = longPreferencesKey("best_time_${rows}x${cols}")
        var isNewRecord = false
        context.dataStore.edit { prefs ->
            val currentWins = prefs[Keys.GAMES_WON] ?: 0
            prefs[Keys.GAMES_WON] = currentWins + 1

            val prevBestMoves = prefs[movesKey]
            val prevBestTime = prefs[timeKey]
            if (prevBestMoves == null || moves < prevBestMoves) {
                prefs[movesKey] = moves
                isNewRecord = true
            }
            if (prevBestTime == null || timeSeconds < prevBestTime) {
                prefs[timeKey] = timeSeconds
                isNewRecord = true
            }
        }
        return isNewRecord
    }

    suspend fun clearAllRecords() {
        context.dataStore.edit { prefs ->
            val keysToRemove = prefs.asMap().keys.filter {
                it.name.startsWith("best_moves_") || it.name.startsWith("best_time_")
            }
            keysToRemove.forEach { prefs.remove(it) }
            prefs[Keys.GAMES_WON] = 0
        }
    }

    suspend fun resetSettingsOnly() {
        context.dataStore.edit { prefs ->
            val wins = prefs[Keys.GAMES_WON] ?: 0
            val rows = prefs[Keys.GRID_ROWS] ?: 4
            val cols = prefs[Keys.GRID_COLS] ?: 4
            prefs.remove(Keys.APP_LANGUAGE)
            prefs.remove(Keys.THEME_MODE)
            prefs.remove(Keys.COLOR_PALETTE)
            prefs.remove(Keys.USE_DYNAMIC_COLOR)
            prefs.remove(Keys.TILE_COLOR_SCHEME)
            prefs.remove(Keys.NUMBER_FORMAT)
            prefs.remove(Keys.CORNER_RADIUS_STYLE)
            prefs.remove(Keys.SPRING_ANIMATION)
            prefs.remove(Keys.AUTO_PLAY_SPEED)
            prefs.remove(Keys.MULTI_TILE_MOVE)
            prefs.remove(Keys.SWIPE_GESTURES)
            prefs.remove(Keys.SHOW_HUD)
            prefs.remove(Keys.HIGHLIGHT_CORRECT)
            prefs.remove(Keys.HAPTIC_MODE)
            prefs.remove(Keys.SOUND_ENABLED)
            prefs.remove(Keys.SOLVER_ALGORITHM)
            prefs.remove(Keys.HEURISTIC_TYPE)
            prefs[Keys.GAMES_WON] = wins
            prefs[Keys.GRID_ROWS] = rows
            prefs[Keys.GRID_COLS] = cols
        }
    }

    private inline fun <reified T : Enum<T>> enumOrDefault(name: String?, default: T): T {
        if (name == null) return default
        return try {
            enumValueOf<T>(name)
        } catch (_: IllegalArgumentException) {
            default
        }
    }
}
