package com.oyegbite.tictactoe.ticTacToe

import android.content.Context
import android.util.Log
import com.oyegbite.tictactoe.R
import com.oyegbite.tictactoe.utils.Constants
import com.oyegbite.tictactoe.utils.SharedPreference
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max
import kotlin.math.min

class AILogic constructor(
    boardDimension: Int = 3,
    moves: ArrayList<IntArray>,
    aiID: String,
    opponentID: String,
    emptyID: String = "_",
    context: Context
) {
    companion object {
        private val TAG: String = AILogic::class.java.simpleName
    }

    object Rating {
        const val EASY: Int = 1
        const val MEDIUM: Int = 2
        const val HARD: Int = 3
    }

    private val mBoardDimension: Int = boardDimension
    private val mMoves: ArrayList<IntArray> = moves
    private val mAiID: String = aiID
    private val mOpponentID: String = opponentID
    private val mEmptyCellID: String = emptyID
    private val mContext: Context = context
    private val mSharedPreference: SharedPreference = SharedPreference(context)
    private var mBoard: Array<Array<String>> = arrayOf()
    private val mScore: Int = 10

    init {
        Log.i(TAG, "mAiID = $mAiID")
        Log.i(TAG, "mOpponentID = $mOpponentID")
        generateEmptyBoard()
        fillBoardWithMoves()
    }

    private fun generateEmptyBoard() {
        val rows = Array(mBoardDimension) { _ -> mEmptyCellID }
        mBoard = Array(mBoardDimension) { _ -> rows.copyOf()}

        Log.i(TAG, "rows = ${rows.contentToString()}")
        Log.i(TAG, "generateEmptyBoard() => mBoard after generating empty = ${mBoard.contentDeepToString()}")
    }

    private fun isMovesLeft(): Boolean {
        for (row in 0 until mBoardDimension) {
            for (col in 0 until mBoardDimension) {
                if (mBoard[row][col] == mEmptyCellID) return true
            }
        }
        return false
    }

    private fun countEmptyCell(): Int {
        var count = 0
        for (row in 0 until mBoardDimension) {
            for (col in 0 until mBoardDimension) {
                if (mBoard[row][col] == mEmptyCellID) count += 1
            }
        }
        return count
    }

    private fun fillBoardWithMoves() {
        val firstPlayer = mSharedPreference.getValue(
            keyClassType = String::class.java,
            key = Constants.KEY_FIRST_PLAYER,
            defaultValue = mContext.getString(R.string.first_player)
        )

        Log.i(TAG, "firstPlayer =  $firstPlayer")
        Log.i(TAG, "mMoves =  ${Arrays.deepToString(mMoves.toArray())}")
        Log.i(TAG, "mBoard = ${mBoard.contentDeepToString()}")

        for (i in 0 until mMoves.size) {
            val (row, col) = mMoves[i]

            if ((i % 2) == 0) { // 1st Player must be stored in this cell.
                mBoard[row][col] = if (firstPlayer == mContext.getString(R.string.first_player)) {
                    mContext.getString(R.string.first_player)
                } else {
                    mContext.getString(R.string.second_player)
                }

            } else { // 2nd player must be stored in this cell.
                mBoard[row][col] = if (firstPlayer == mContext.getString(R.string.first_player)) {
                    mContext.getString(R.string.second_player)
                } else {
                    mContext.getString(R.string.first_player)
                }
            }
        }

        Log.i(TAG, "fillBoardWithMoves() => mBoard = ${mBoard.contentDeepToString()}")
    }

    private fun evaluateBoardForWinner(depth: Int): Int {
        // Checking rows for AI or Opponent victory
        for (row in 0 until mBoardDimension) {
            var sameInRow = true
            val cellVal = mBoard[row][0]
            for (col in 1 until mBoardDimension) {
                sameInRow = mBoard[row][col] == cellVal
                if (!sameInRow) break
            }

            if (sameInRow) { // It might be the empty cell that we are checking and we don't want that
                if (cellVal == mAiID) return mScore
                if (cellVal == mOpponentID) return -mScore
            }
        }


        // Checking columns for AI or Opponent victory
        for (col in 0 until mBoardDimension) {
            var sameInCol = true
            val cellVal = mBoard[0][col]
            for (row in 1 until mBoardDimension) {
                sameInCol = mBoard[row][col] == cellVal
                if (!sameInCol) break
            }

            if (sameInCol) { // It might be the empty cell that we are checking and we don't want that
                if (cellVal == mAiID) return mScore
                if (cellVal == mOpponentID) return -mScore
            }
        }

        // Checking leading diagonals for AI or Opponent victory
        val cellValDiagonal = mBoard[0][0]
        var sameOnLeadingDiagonal = true
        for (leadingDiagonal in 1 until mBoardDimension) {
            sameOnLeadingDiagonal = mBoard[leadingDiagonal][leadingDiagonal] == cellValDiagonal
            if (!sameOnLeadingDiagonal) break
        }
        if (sameOnLeadingDiagonal) { // It might be the empty cell that we are checking and we don't want that
            if (cellValDiagonal == mAiID) return mScore
            if (cellValDiagonal == mOpponentID) return -mScore
        }

        // Checking opposite leading diagonals for AI or Opponent victory
        // Opposite leading diagonal cell coordinate for a 3 x 3 => 0,2 | 2,2 | 2,0
        // Opposite leading diagonal cell coordinate for a 4 x 4 => 0,3 | 1,2 | 2,1 | 3,0
        val cellValOppDiagonal = mBoard[0][mBoardDimension - 1]
        var sameOnOppLeadingDiagonal = true
        for (oppLeadingDiagonal in 1 until mBoardDimension) {
            sameOnOppLeadingDiagonal =
                mBoard[oppLeadingDiagonal][mBoardDimension - 1 - oppLeadingDiagonal] == cellValOppDiagonal
            if (!sameOnOppLeadingDiagonal) break
        }
        if (sameOnOppLeadingDiagonal) { // It might be the empty cell that we are checking and we don't want that
            if (cellValOppDiagonal == mAiID) return mScore
            if (cellValOppDiagonal == mOpponentID) return -mScore
        }

        return 0 // No winner
    }

    private fun minMaxAlgorithm(depth: Int, isMaximizer: Boolean): Int {
        val score: Int = evaluateBoardForWinner(depth)
        // Maximizer has won the game return his/her
        // evaluated score
        if (score == mScore) return score

        // Minimizer has won the game return his/her
        // evaluated score
        if (score == -mScore) return score

        // If there are no more moves and no winner then
        // it is a tie
        if (!isMovesLeft()) return 0

        // If this is the maximizer's move
        if (isMaximizer) {
            var best = Int.MIN_VALUE

            for (row in 0 until mBoardDimension) {
                for (col in 0 until mBoardDimension) {
                    // Check if cell is empty
                    if (mBoard[row][col] == mEmptyCellID) {
                        // Make the move
                        mBoard[row][col] = mAiID

                        // Try and choose the maximum value recursively.
                        best = max(best, minMaxAlgorithm(depth + 1, !isMaximizer))

                        // Undo the move
                        mBoard[row][col] = mEmptyCellID
                    }
                }
            }

            return best

        } else { // If this is the minimizer's move
            var best = Int.MAX_VALUE

            for (row in 0 until mBoardDimension) {
                for (col in 0 until mBoardDimension) {
                    // Check if cell is empty
                    if (mBoard[row][col] == mEmptyCellID) {
                        // Make the move
                        mBoard[row][col] = mOpponentID

                        // Try and choose the minimum value.
                        best = min(best, minMaxAlgorithm(depth + 1, !isMaximizer))

                        // Undo the move
                        mBoard[row][col] = mEmptyCellID
                    }
                }
            }

            return best
        }
    }

    fun findBestMove(difficultyLevel: String): IntArray {
        var bestVal = Int.MIN_VALUE
        var bestMove = arrayOf(-1, -1)

        val totalDifficultyRate = 3
        val difficultyRate: Int = when (difficultyLevel) {
            mContext.getString(R.string.easy) -> Rating.EASY // Rating for EASY
            mContext.getString(R.string.medium) -> Rating.MEDIUM // Rating for MEDIUM
            else -> Rating.HARD // Rating for HARD
        }

        Log.i(TAG, "difficultyRate = $difficultyRate/$totalDifficultyRate")

        // Traverse all cells, evaluate minMaxAlgorithm function for
        // all empty cells. And return the cell with optimal
        // value.
        // For Rating: Evaluate only (difficultyRate / totalDifficultyRate) of the length of empty cell
        val numEmptyCells = countEmptyCell()
        var numMoveToEvaluate: Int = (difficultyRate * numEmptyCells) / totalDifficultyRate
        numMoveToEvaluate = max(1, numMoveToEvaluate) // Evaluate at least one move no matter what.

        Log.i(TAG, "(numEmptyCells = $numEmptyCells, numMoveToEvaluate = $numMoveToEvaluate)")

        for (row in 0 until mBoardDimension) {
            for (col in 0 until mBoardDimension) {
                // Check if cell is empty
                if (mBoard[row][col] == mEmptyCellID) {
                    // Make the move
                    mBoard[row][col] = mAiID

                    val moveVal = minMaxAlgorithm(0, false)

//                    Log.i(TAG, "($row, $col) => moveVal = $moveVal")

                    // Undo the move
                    mBoard[row][col] = mEmptyCellID

                    // If the value of the current move is
                    // more than the best value, then update
                    // best.
                    if (moveVal > bestVal) {
                        bestVal = moveVal
                        bestMove = arrayOf(row, col)
                    }

                    numMoveToEvaluate -= 1

                    if (numMoveToEvaluate == 0) break
                }
            }
            if (numMoveToEvaluate == 0) break
        }

        Log.i(TAG, "numMoveToEvaluate = $numMoveToEvaluate")
        Log.i(TAG, "Best Move = ${bestMove.contentDeepToString()}")
        return bestMove.toIntArray()
    }

}