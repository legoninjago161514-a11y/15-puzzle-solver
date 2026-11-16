package com.example.solver

import com.example.data.HeuristicType

object PuzzleSolver {

    fun pureManhattan(board: IntArray, rows: Int, cols: Int): Int {
        var dist = 0
        val total = rows * cols
        for (i in 0 until total) {
            val tile = board[i]
            if (tile != 0) {
                val targetRow = (tile - 1) / cols
                val targetCol = (tile - 1) % cols
                val currentRow = i / cols
                val currentCol = i % cols
                dist += kotlin.math.abs(targetRow - currentRow) + kotlin.math.abs(targetCol - currentCol)
            }
        }
        return dist
    }

    fun manhattanWithLinearConflict(board: IntArray, rows: Int, cols: Int): Int {
        val dist = pureManhattan(board, rows, cols)
        var conflict = 0
        for (r in 0 until rows) {
            for (c1 in 0 until cols - 1) {
                val tile1 = board[r * cols + c1]
                if (tile1 != 0 && (tile1 - 1) / cols == r) {
                    for (c2 in c1 + 1 until cols) {
                        val tile2 = board[r * cols + c2]
                        if (tile2 != 0 && (tile2 - 1) / cols == r && tile1 > tile2) {
                            conflict += 2
                        }
                    }
                }
            }
        }
        for (c in 0 until cols) {
            for (r1 in 0 until rows - 1) {
                val tile1 = board[r1 * cols + c]
                if (tile1 != 0 && (tile1 - 1) % cols == c) {
                    for (r2 in r1 + 1 until rows) {
                        val tile2 = board[r2 * cols + c]
                        if (tile2 != 0 && (tile2 - 1) % cols == c && tile1 > tile2) {
                            conflict += 2
                        }
                    }
                }
            }
        }
        return dist + conflict
    }

    fun solveOptimalIda(
        initialBoard: List<Int>,
        rows: Int,
        cols: Int,
        heuristicType: HeuristicType = HeuristicType.MANHATTAN_LINEAR_CONFLICT,
        timeoutMs: Long = 2500
    ): List<Int>? {
        val board = initialBoard.toIntArray()
        if (isSolved(board, rows, cols)) return emptyList()

        val limit = estimateHeuristic(board, rows, cols, heuristicType)
        var bound = limit
        val path = mutableListOf<Int>()
        val zeroPos = board.indexOf(0)
        val startTime = System.currentTimeMillis()

        while (true) {
            val t = search(
                board = board,
                g = 0,
                bound = bound,
                path = path,
                zeroPos = zeroPos,
                prevZeroPos = -1,
                rows = rows,
                cols = cols,
                heuristicType = heuristicType,
                startTime = startTime,
                timeoutMs = timeoutMs
            )
            if (t == -1) return path
            if (t == Int.MAX_VALUE) return null
            bound = t
        }
    }

    private fun estimateHeuristic(
        board: IntArray,
        rows: Int,
        cols: Int,
        heuristicType: HeuristicType
    ): Int {
        return when (heuristicType) {
            HeuristicType.MANHATTAN_LINEAR_CONFLICT -> manhattanWithLinearConflict(board, rows, cols)
            HeuristicType.PURE_MANHATTAN -> pureManhattan(board, rows, cols)
        }
    }

    private fun search(
        board: IntArray,
        g: Int,
        bound: Int,
        path: MutableList<Int>,
        zeroPos: Int,
        prevZeroPos: Int,
        rows: Int,
        cols: Int,
        heuristicType: HeuristicType,
        startTime: Long,
        timeoutMs: Long
    ): Int {
        if (System.currentTimeMillis() - startTime > timeoutMs) return Int.MAX_VALUE

        val f = g + estimateHeuristic(board, rows, cols, heuristicType)
        if (f > bound) return f
        if (isSolved(board, rows, cols)) return -1

        var min = Int.MAX_VALUE
        val r = zeroPos / cols
        val c = zeroPos % cols

        val adjacent = mutableListOf<Int>()
        if (r > 0) adjacent.add((r - 1) * cols + c)
        if (r < rows - 1) adjacent.add((r + 1) * cols + c)
        if (c > 0) adjacent.add(r * cols + (c - 1))
        if (c < cols - 1) adjacent.add(r * cols + (c + 1))

        for (nextPos in adjacent) {
            if (nextPos == prevZeroPos) continue

            val tile = board[nextPos]
            board[zeroPos] = tile
            board[nextPos] = 0
            path.add(tile)

            val t = search(
                board = board,
                g = g + 1,
                bound = bound,
                path = path,
                zeroPos = nextPos,
                prevZeroPos = zeroPos,
                rows = rows,
                cols = cols,
                heuristicType = heuristicType,
                startTime = startTime,
                timeoutMs = timeoutMs
            )
            if (t == -1) return -1
            if (t == Int.MAX_VALUE) return Int.MAX_VALUE
            if (t < min) min = t

            board[nextPos] = tile
            board[zeroPos] = 0
            path.removeAt(path.size - 1)
        }
        return min
    }

    fun isSolved(board: IntArray, rows: Int, cols: Int): Boolean {
        val total = rows * cols
        for (i in 0 until total - 1) {
            if (board[i] != i + 1) return false
        }
        return board[total - 1] == 0
    }
}
