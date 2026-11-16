package com.example.solver

object ConstructiveSolver {

    fun solve(initialBoard: List<Int>, rows: Int, cols: Int): List<Int>? {
        val board = initialBoard.toIntArray()
        val total = rows * cols
        if (PuzzleSolver.isSolved(board, rows, cols)) return emptyList()

        // For small boards <= 16 cells (e.g. 2x2, 2x3, 3x3, 4x4 with small shuffle):
        // Try optimal IDA* first with 1.5s timeout.
        if (total <= 16) {
            val idaResult = PuzzleSolver.solveOptimalIda(initialBoard, rows, cols, timeoutMs = 1500)
            if (idaResult != null) return idaResult
        }

        // If rows < 3 or cols < 3:
        if (rows < 3 || cols < 3) {
            return solveSmallBoardBfs(board, rows, cols)
        }

        // Constructive reduction solver
        val moves = mutableListOf<Int>()
        val locked = BooleanArray(total)

        // 1. Solve rows from top to bottom (row 0 .. rows - 4)
        for (r in 0 until rows - 3) {
            solveRow(r, board, rows, cols, locked, moves)
        }

        // 2. Solve columns from left to right (col 0 .. cols - 4)
        for (c in 0 until cols - 3) {
            solveCol(c, board, rows, cols, locked, moves)
        }

        // 3. Solve remaining 3x3 subgrid at the bottom-right
        solveRemaining3x3(board, rows, cols, moves)

        val optimized = optimizeMoves(initialBoard.toIntArray(), moves, rows, cols)

        // Verify the board is actually solved
        val testBoard = initialBoard.toIntArray()
        var testZ = testBoard.indexOf(0)
        for (tile in optimized) {
            val tPos = testBoard.indexOf(tile)
            testBoard[testZ] = tile
            testBoard[tPos] = 0
            testZ = tPos
        }

        return if (PuzzleSolver.isSolved(testBoard, rows, cols)) {
            optimized
        } else {
            moves
        }
    }

    private fun optimizeMoves(initialBoard: IntArray, rawMoves: List<Int>, rows: Int, cols: Int): List<Int> {
        if (rawMoves.size <= 2) return rawMoves

        val total = rows * cols
        val rnd = java.util.Random(123456789L)
        val zobrist = Array(total) { LongArray(total) { rnd.nextLong() } }

        fun hashBoard(b: IntArray): Long {
            var h = 0L
            for (i in 0 until total) {
                h = h xor zobrist[i][b[i]]
            }
            return h
        }

        var currentMoves = rawMoves
        var changed = true
        var passes = 0

        while (changed && passes < 3) {
            passes++
            changed = false

            val simBoard = initialBoard.clone()
            var z = simBoard.indexOf(0)
            var currentHash = hashBoard(simBoard)

            val stateMap = java.util.HashMap<Long, Int>()
            stateMap[currentHash] = 0

            val history = mutableListOf<Pair<Int, Long>>()

            for (tile in currentMoves) {
                val tPos = simBoard.indexOf(tile)
                if (tPos == -1) break
                simBoard[z] = tile
                simBoard[tPos] = 0

                currentHash = currentHash xor zobrist[z][0] xor zobrist[z][tile] xor zobrist[tPos][tile] xor zobrist[tPos][0]
                z = tPos

                val prevIdx = stateMap[currentHash]
                if (prevIdx != null) {
                    val removeCount = history.size - prevIdx
                    repeat(removeCount) {
                        history.removeAt(history.size - 1)
                    }
                    stateMap.clear()
                    var reHash = hashBoard(initialBoard)
                    stateMap[reHash] = 0
                    val curB = initialBoard.clone()
                    var curZ = curB.indexOf(0)
                    for (hIdx in history.indices) {
                        val hTile = history[hIdx].first
                        val hTPos = curB.indexOf(hTile)
                        curB[curZ] = hTile
                        curB[hTPos] = 0
                        reHash = history[hIdx].second
                        curZ = hTPos
                        stateMap[reHash] = hIdx + 1
                    }
                    currentHash = reHash
                    changed = true
                } else {
                    history.add(tile to currentHash)
                    stateMap[currentHash] = history.size
                }
            }

            if (changed) {
                currentMoves = history.map { it.first }
            }
        }

        return currentMoves
    }

    private fun slideBlank(nextZ: Int, board: IntArray, moves: MutableList<Int>) {
        val z = board.indexOf(0)
        val movedTile = board[nextZ]
        board[z] = movedTile
        board[nextZ] = 0
        moves.add(movedTile)
    }

    private fun moveTileToDest(
        targetTile: Int,
        destPos: Int,
        board: IntArray,
        rows: Int,
        cols: Int,
        locked: BooleanArray,
        moves: MutableList<Int>
    ): Boolean {
        val tilePos = board.indexOf(targetTile)
        val zeroPos = board.indexOf(0)
        if (tilePos == destPos) return true

        val total = rows * cols
        val visited = BooleanArray(total * total)
        val parentState = IntArray(total * total) { -1 }
        val zeroMoveTo = IntArray(total * total) { -1 }

        val queue = IntArray(total * total)
        var head = 0
        var tail = 0

        val startState = tilePos * total + zeroPos
        visited[startState] = true
        queue[tail++] = startState

        var endState = -1

        while (head < tail) {
            val curr = queue[head++]
            val cT = curr / total
            val cZ = curr % total

            if (cT == destPos) {
                endState = curr
                break
            }

            val zRow = cZ / cols
            val zCol = cZ % cols

            if (zRow > 0) {
                val nZ = cZ - cols
                if (!locked[nZ]) {
                    val nT = if (nZ == cT) cZ else cT
                    val nState = nT * total + nZ
                    if (!visited[nState]) {
                        visited[nState] = true
                        parentState[nState] = curr
                        zeroMoveTo[nState] = nZ
                        queue[tail++] = nState
                    }
                }
            }
            if (zRow < rows - 1) {
                val nZ = cZ + cols
                if (!locked[nZ]) {
                    val nT = if (nZ == cT) cZ else cT
                    val nState = nT * total + nZ
                    if (!visited[nState]) {
                        visited[nState] = true
                        parentState[nState] = curr
                        zeroMoveTo[nState] = nZ
                        queue[tail++] = nState
                    }
                }
            }
            if (zCol > 0) {
                val nZ = cZ - 1
                if (!locked[nZ]) {
                    val nT = if (nZ == cT) cZ else cT
                    val nState = nT * total + nZ
                    if (!visited[nState]) {
                        visited[nState] = true
                        parentState[nState] = curr
                        zeroMoveTo[nState] = nZ
                        queue[tail++] = nState
                    }
                }
            }
            if (zCol < cols - 1) {
                val nZ = cZ + 1
                if (!locked[nZ]) {
                    val nT = if (nZ == cT) cZ else cT
                    val nState = nT * total + nZ
                    if (!visited[nState]) {
                        visited[nState] = true
                        parentState[nState] = curr
                        zeroMoveTo[nState] = nZ
                        queue[tail++] = nState
                    }
                }
            }
        }

        if (endState == -1) return false

        val path = mutableListOf<Int>()
        var curr = endState
        while (curr != startState) {
            path.add(zeroMoveTo[curr])
            curr = parentState[curr]
        }
        path.reverse()

        for (nZ in path) {
            slideBlank(nZ, board, moves)
        }
        return true
    }

    private fun placeTwoTiles(
        tileA: Int,
        destA: Int,
        tileB: Int,
        destB: Int,
        board: IntArray,
        rows: Int,
        cols: Int,
        locked: BooleanArray,
        moves: MutableList<Int>
    ): Boolean {
        val posA = board.indexOf(tileA)
        val posB = board.indexOf(tileB)
        val pos0 = board.indexOf(0)
        if (posA == destA && posB == destB) return true

        val total = rows * cols
        val unlockedCells = mutableListOf<Int>()
        for (i in 0 until total) {
            if (!locked[i]) unlockedCells.add(i)
        }
        val U = unlockedCells.size
        val cellToIdx = IntArray(total) { -1 }
        for (i in 0 until U) {
            cellToIdx[unlockedCells[i]] = i
        }

        val startIdxA = cellToIdx[posA]
        val startIdxB = cellToIdx[posB]
        val startIdx0 = cellToIdx[pos0]
        val destIdxA = cellToIdx[destA]
        val destIdxB = cellToIdx[destB]

        val totalStates = U * U * U
        val visited = java.util.BitSet(totalStates)
        val parentState = IntArray(totalStates) { -1 }
        val zeroMoveTo = IntArray(totalStates) { -1 }
        val queue = IntArray(totalStates)
        var head = 0
        var tail = 0

        val startState = (startIdxA * U + startIdxB) * U + startIdx0
        visited.set(startState)
        queue[tail++] = startState

        // Precompute neighbors
        val neighbors = Array(U) { IntArray(4) }
        val neighborCount = IntArray(U)
        for (i in 0 until U) {
            val cell = unlockedCells[i]
            val r = cell / cols
            val c = cell % cols
            var cnt = 0
            if (r > 0) {
                val nc = cell - cols
                if (!locked[nc]) neighbors[i][cnt++] = cellToIdx[nc]
            }
            if (r < rows - 1) {
                val nc = cell + cols
                if (!locked[nc]) neighbors[i][cnt++] = cellToIdx[nc]
            }
            if (c > 0) {
                val nc = cell - 1
                if (!locked[nc]) neighbors[i][cnt++] = cellToIdx[nc]
            }
            if (c < cols - 1) {
                val nc = cell + 1
                if (!locked[nc]) neighbors[i][cnt++] = cellToIdx[nc]
            }
            neighborCount[i] = cnt
        }

        var endState = -1

        while (head < tail) {
            val curr = queue[head++]
            val c0 = curr % U
            val temp = curr / U
            val cB = temp % U
            val cA = temp / U

            if (cA == destIdxA && cB == destIdxB) {
                endState = curr
                break
            }

            val cnt = neighborCount[c0]
            val nbrs = neighbors[c0]
            for (k in 0 until cnt) {
                val n0 = nbrs[k]
                val nA = if (n0 == cA) c0 else cA
                val nB = if (n0 == cB) c0 else cB
                val nState = (nA * U + nB) * U + n0
                if (!visited.get(nState)) {
                    visited.set(nState)
                    parentState[nState] = curr
                    zeroMoveTo[nState] = unlockedCells[n0]
                    queue[tail++] = nState
                }
            }
        }

        if (endState == -1) return false

        val path = mutableListOf<Int>()
        var curr = endState
        while (curr != startState) {
            path.add(zeroMoveTo[curr])
            curr = parentState[curr]
        }
        path.reverse()

        for (nextZ in path) {
            slideBlank(nextZ, board, moves)
        }
        return true
    }

    private fun solveRow(
        r: Int,
        board: IntArray,
        rows: Int,
        cols: Int,
        locked: BooleanArray,
        moves: MutableList<Int>
    ) {
        // Place tiles c = 0 .. cols - 3
        for (c in 0 until cols - 2) {
            val targetTile = r * cols + c + 1
            val targetPos = r * cols + c
            moveTileToDest(targetTile, targetPos, board, rows, cols, locked, moves)
            locked[targetPos] = true
        }

        // Place last two tiles of row r: tile A and tile B
        val tileA = r * cols + (cols - 1) // target: (r, cols - 2)
        val tileB = r * cols + cols        // target: (r, cols - 1)
        val posTargetA = r * cols + (cols - 2)
        val posTargetB = r * cols + (cols - 1)

        placeTwoTiles(tileA, posTargetA, tileB, posTargetB, board, rows, cols, locked, moves)

        locked[posTargetA] = true
        locked[posTargetB] = true
    }

    private fun solveCol(
        c: Int,
        board: IntArray,
        rows: Int,
        cols: Int,
        locked: BooleanArray,
        moves: MutableList<Int>
    ) {
        val startRow = rows - 3
        // Place top tile of col c
        val tileTop = startRow * cols + c + 1
        val posTop = startRow * cols + c
        moveTileToDest(tileTop, posTop, board, rows, cols, locked, moves)
        locked[posTop] = true

        // Bottom two tiles: tile A (middle) and tile B (bottom)
        val tileA = (startRow + 1) * cols + c + 1 // target: (startRow + 1, c)
        val tileB = (startRow + 2) * cols + c + 1 // target: (startRow + 2, c)
        val posTargetA = (startRow + 1) * cols + c
        val posTargetB = (startRow + 2) * cols + c

        placeTwoTiles(tileA, posTargetA, tileB, posTargetB, board, rows, cols, locked, moves)

        locked[posTargetA] = true
        locked[posTargetB] = true
    }

    private fun solveRemaining3x3(
        board: IntArray,
        rows: Int,
        cols: Int,
        moves: MutableList<Int>
    ) {
        val subCells = IntArray(9)
        var idx = 0
        for (r in rows - 3 until rows) {
            for (c in cols - 3 until cols) {
                subCells[idx++] = r * cols + c
            }
        }

        val subBoard = IntArray(9)
        for (i in 0 until 9) {
            val v = board[subCells[i]]
            subBoard[i] = if (v == 0) 0 else subCells.indexOf(v - 1) + 1
        }

        // Try optimal IDA* with linear conflicts first (instant, 0 allocations, optimal moves)
        val idaResult = PuzzleSolver.solveOptimalIda(subBoard.toList(), 3, 3, timeoutMs = 3000)
        if (idaResult != null) {
            for (subTile in idaResult) {
                val bigTile = subCells[subTile - 1] + 1
                val bigPos = board.indexOf(bigTile)
                if (bigPos != -1) {
                    slideBlank(bigPos, board, moves)
                }
            }
            return
        }

        // Fallback to BFS if IDA* times out
        val subPath = solve3x3Bfs(subBoard) ?: return

        for (nextSubZero in subPath) {
            val nextBigZero = subCells[nextSubZero]
            slideBlank(nextBigZero, board, moves)
        }
    }

    private fun solve3x3Bfs(initialSubBoard: IntArray): List<Int>? {
        if (is3x3Solved(initialSubBoard)) return emptyList()

        val startZero = initialSubBoard.indexOf(0)
        fun encode(b: IntArray): Int {
            var hash = 0
            for (i in 0 until 9) {
                hash = hash * 9 + b[i]
            }
            return hash
        }

        val visited = java.util.HashSet<Int>()
        class Node(val board: IntArray, val zeroPos: Int, val parent: Node?, val zeroMovedTo: Int)

        val startNode = Node(initialSubBoard.clone(), startZero, null, -1)
        visited.add(encode(initialSubBoard))

        val queue = java.util.ArrayDeque<Node>()
        queue.add(startNode)

        var solvedNode: Node? = null

        while (queue.isNotEmpty()) {
            val curr = queue.poll() ?: break
            if (is3x3Solved(curr.board)) {
                solvedNode = curr
                break
            }

            val zr = curr.zeroPos / 3
            val zc = curr.zeroPos % 3

            val nextZs = mutableListOf<Int>()
            if (zr > 0) nextZs.add(curr.zeroPos - 3)
            if (zr < 2) nextZs.add(curr.zeroPos + 3)
            if (zc > 0) nextZs.add(curr.zeroPos - 1)
            if (zc < 2) nextZs.add(curr.zeroPos + 1)

            for (nZ in nextZs) {
                val nextBoard = curr.board.clone()
                nextBoard[curr.zeroPos] = nextBoard[nZ]
                nextBoard[nZ] = 0
                val code = encode(nextBoard)
                if (visited.add(code)) {
                    queue.add(Node(nextBoard, nZ, curr, nZ))
                }
            }
        }

        if (solvedNode == null) return null

        val path = mutableListOf<Int>()
        var curr = solvedNode
        while (curr?.parent != null) {
            path.add(curr.zeroMovedTo)
            curr = curr.parent
        }
        path.reverse()
        return path
    }

    private fun is3x3Solved(b: IntArray): Boolean {
        for (i in 0 until 8) {
            if (b[i] != i + 1) return false
        }
        return b[8] == 0
    }

    private fun solveSmallBoardBfs(initialBoard: IntArray, rows: Int, cols: Int): List<Int>? {
        val total = rows * cols
        fun encode(b: IntArray): Long {
            var hash = 0L
            for (i in 0 until total) {
                hash = hash * total + b[i]
            }
            return hash
        }

        val visited = java.util.HashSet<Long>()
        class Node(val board: IntArray, val zeroPos: Int, val parent: Node?, val movedTile: Int)

        val startZero = initialBoard.indexOf(0)
        val startNode = Node(initialBoard.clone(), startZero, null, -1)
        visited.add(encode(initialBoard))

        val queue = java.util.ArrayDeque<Node>()
        queue.add(startNode)

        var solvedNode: Node? = null
        val startTime = System.currentTimeMillis()

        while (queue.isNotEmpty()) {
            if (System.currentTimeMillis() - startTime > 3000) break
            val curr = queue.poll() ?: break
            if (PuzzleSolver.isSolved(curr.board, rows, cols)) {
                solvedNode = curr
                break
            }

            val r = curr.zeroPos / cols
            val c = curr.zeroPos % cols

            val nextZs = mutableListOf<Int>()
            if (r > 0) nextZs.add(curr.zeroPos - cols)
            if (r < rows - 1) nextZs.add(curr.zeroPos + cols)
            if (c > 0) nextZs.add(curr.zeroPos - 1)
            if (c < cols - 1) nextZs.add(curr.zeroPos + 1)

            for (nZ in nextZs) {
                val nextBoard = curr.board.clone()
                val movedTile = nextBoard[nZ]
                nextBoard[curr.zeroPos] = movedTile
                nextBoard[nZ] = 0
                val code = encode(nextBoard)
                if (visited.add(code)) {
                    queue.add(Node(nextBoard, nZ, curr, movedTile))
                }
            }
        }

        if (solvedNode == null) return null

        val path = mutableListOf<Int>()
        var curr = solvedNode
        while (curr?.parent != null) {
            path.add(curr.movedTile)
            curr = curr.parent
        }
        path.reverse()
        return path
    }
}
