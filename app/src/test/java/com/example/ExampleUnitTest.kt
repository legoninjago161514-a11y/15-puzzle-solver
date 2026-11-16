package com.example

import com.example.solver.ConstructiveSolver
import com.example.solver.PuzzleSolver
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testSolve3x3() {
        val board = listOf(1, 2, 3, 4, 5, 6, 0, 7, 8)
        val solution = ConstructiveSolver.solve(board, 3, 3)
        assertNotNull(solution)
        assertTrue(solution!!.isNotEmpty())
    }

    @Test
    fun testSolve10x10() {
        val rows = 10
        val cols = 10
        val total = rows * cols

        var solvedCount = 0
        val totalAttempts = 20

        for (seed in 1..totalAttempts) {
            val rnd = java.util.Random(seed.toLong() * 99991L + 13)
            val list = (0 until total).toMutableList()
            // Fisher-Yates shuffle
            for (i in total - 1 downTo 1) {
                val j = rnd.nextInt(i + 1)
                val t = list[i]
                list[i] = list[j]
                list[j] = t
            }

            // Count inversions
            var inv = 0
            for (i in 0 until total - 1) {
                for (j in i + 1 until total) {
                    if (list[i] != 0 && list[j] != 0 && list[i] > list[j]) inv++
                }
            }
            val blankIdx = list.indexOf(0)
            val blankRowFromBottom = rows - (blankIdx / cols)
            val isSolvable = if (cols % 2 != 0) {
                inv % 2 == 0
            } else {
                if (blankRowFromBottom % 2 == 0) inv % 2 != 0 else inv % 2 == 0
            }

            if (!isSolvable) {
                // Swap two non-zero tiles to make it solvable
                val idx1 = if (list[0] != 0) 0 else 2
                val idx2 = if (list[1] != 0) 1 else 2
                val tmp = list[idx1]
                list[idx1] = list[idx2]
                list[idx2] = tmp
            }

            println("Solving random permutation seed $seed...")
            val solution = ConstructiveSolver.solve(list, rows, cols)
            if (solution != null) {
                val currentBoard = list.toMutableList()
                var currentZ = currentBoard.indexOf(0)
                for (tile in solution) {
                    val tIdx = currentBoard.indexOf(tile)
                    currentBoard[currentZ] = tile
                    currentBoard[tIdx] = 0
                    currentZ = tIdx
                }
                if (PuzzleSolver.isSolved(currentBoard.toIntArray(), rows, cols)) {
                    solvedCount++
                    println("Seed $seed SOLVED in ${solution.size} moves!")
                } else {
                    println("Seed $seed FAILED to reach solved state!")
                }
            } else {
                println("Seed $seed FAILED with null solution!")
            }
        }
        println("SUMMARY: $solvedCount / $totalAttempts solved")
        assertTrue("At least 19 out of 20 must be solved", solvedCount >= 19)
    }

    @Test
    fun testSolveVariousGridSizes() {
        val sizes = listOf(
            10 to 10, 10 to 8, 8 to 10, 9 to 9, 8 to 8, 7 to 7, 6 to 6, 5 to 5, 4 to 4, 3 to 3, 10 to 5, 5 to 10
        )

        for ((rows, cols) in sizes) {
            val total = rows * cols
            val rnd = java.util.Random(rows * 31L + cols * 17L + 123)
            val list = (0 until total).toMutableList()
            // Fisher-Yates shuffle
            for (i in total - 1 downTo 1) {
                val j = rnd.nextInt(i + 1)
                val t = list[i]
                list[i] = list[j]
                list[j] = t
            }

            var inv = 0
            for (i in 0 until total - 1) {
                for (j in i + 1 until total) {
                    if (list[i] != 0 && list[j] != 0 && list[i] > list[j]) inv++
                }
            }
            val blankIdx = list.indexOf(0)
            val blankRowFromBottom = rows - (blankIdx / cols)
            val isSolvable = if (cols % 2 != 0) {
                inv % 2 == 0
            } else {
                if (blankRowFromBottom % 2 == 0) inv % 2 != 0 else inv % 2 == 0
            }

            if (!isSolvable) {
                val idx1 = if (list[0] != 0) 0 else 2
                val idx2 = if (list[1] != 0) 1 else 2
                val tmp = list[idx1]
                list[idx1] = list[idx2]
                list[idx2] = tmp
            }

            val solution = ConstructiveSolver.solve(list, rows, cols)
            assertNotNull("Solution must not be null for ${rows}x${cols}", solution)

            val currentBoard = list.toMutableList()
            var currentZ = currentBoard.indexOf(0)
            for (tile in solution!!) {
                val tIdx = currentBoard.indexOf(tile)
                assertTrue(
                    "Tile must be adjacent to blank in ${rows}x${cols}",
                    kotlin.math.abs(tIdx / cols - currentZ / cols) + kotlin.math.abs(tIdx % cols - currentZ % cols) == 1
                )
                currentBoard[currentZ] = tile
                currentBoard[tIdx] = 0
                currentZ = tIdx
            }
            val solved = PuzzleSolver.isSolved(currentBoard.toIntArray(), rows, cols)
            assertTrue("Board ${rows}x${cols} must be solved! Moves: ${solution.size}", solved)
        }
    }
}

