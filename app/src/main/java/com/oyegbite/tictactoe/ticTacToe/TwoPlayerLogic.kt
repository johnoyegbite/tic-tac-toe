package com.oyegbite.tictactoe.ticTacToe

import android.content.Context
import android.util.Log
import com.oyegbite.tictactoe.R
import com.oyegbite.tictactoe.utils.SharedPreference

class TwoPlayerLogic (context: Context, dimension: Int) {
    companion object {
        private val TAG: String = TicTacToeLogic::class.java.simpleName
    }

    private var mSharedPreference: SharedPreference = SharedPreference(context)

    private var mMoves: ArrayList<IntArray> = arrayListOf()
    private var mVisitedCells: MutableSet<String> = mutableSetOf()

    private var mBoardDimension: Int = dimension
    private var mContext: Context = context
    private var mWinDirection: String = mContext.getString(R.string.win_direction_none)
    private var mWinMoveCell: IntArray = intArrayOf(0, 0)
    private var mBoard: Array<Array<String>> = arrayOf()
    private var mFirstPlayerID = mContext.getString(R.string.first_player)
    private var mSecondPlayerID = mContext.getString(R.string.second_player)
    private var mEmptyCellID = mContext.getString(R.string.empty)
    private var mPendingID = mContext.getString(R.string.pending)
    private var mDrawID = mContext.getString(R.string.draw)

    init {
        generateEmptyBoard()
    }


    private fun generateEmptyBoard() {
        val rows = Array<String>(mBoardDimension) { _ -> mEmptyCellID }
        mBoard = Array<Array<String>>(mBoardDimension) { _ -> rows.copyOf()}
    }

    private fun fillBoardWithMoves(moves: Array<IntArray>) {
        for (i in moves.indices) {
            val (row, col) = moves[i]

            if ((i % 2) == 0) { // 1st Player must be stored in this cell.
                mBoard[row][col] = mFirstPlayerID

            } else { // 2nd player must be stored in this cell.
                mBoard[row][col] = mSecondPlayerID
            }
        }
        Log.i(TAG, " fillBoardWithMoves() => mBoard = ${mBoard.contentDeepToString()}")
    }

    private fun isMovesLeft(): Boolean {
        for (row in 0 until mBoardDimension) {
            for (col in 0 until mBoardDimension) {
                if (mBoard[row][col] == mEmptyCellID) return true
            }
        }
        return false
    }

    private fun evaluateBoardForWinner(): String {
        // Checking rows for AI or Opponent victory
        for (row in 0 until mBoardDimension) {
            var sameInRow = true
            val cellVal = mBoard[row][0]
            var winMoveCell = intArrayOf(-1, -1)
            for (col in 1 until mBoardDimension) {
                sameInRow = mBoard[row][col] == cellVal
                winMoveCell = intArrayOf(row, col)
                if (!sameInRow) break
            }

            if (sameInRow) { // It might be the empty cell that we are checking and we don't want that
                if (cellVal == mFirstPlayerID) return mFirstPlayerID
                if (cellVal == mSecondPlayerID) return mSecondPlayerID
                if (cellVal == mFirstPlayerID || cellVal == mSecondPlayerID) {
                    mWinMoveCell = winMoveCell
                    mWinDirection = mContext.getString(R.string.win_direction_row)
                }
            }
        }

        // Checking columns for AI or Opponent victory
        for (col in 0 until mBoardDimension) {
            var sameInCol = true
            val cellVal = mBoard[0][col]
            var winMoveCell = intArrayOf(-1, -1)
            for (row in 1 until mBoardDimension) {
                sameInCol = mBoard[row][col] == cellVal
                winMoveCell = intArrayOf(row, col)
                if (!sameInCol) break
            }

            if (sameInCol) { // It might be the empty cell that we are checking and we don't want that
                if (cellVal == mFirstPlayerID) return mFirstPlayerID
                if (cellVal == mSecondPlayerID) return mSecondPlayerID
                if (cellVal == mFirstPlayerID || cellVal == mSecondPlayerID) {
                    mWinMoveCell = winMoveCell
                    mWinDirection = mContext.getString(R.string.win_direction_col)
                }
            }
        }

        // Checking leading diagonals for AI or Opponent victory
        val cellValDiagonal = mBoard[0][0]
        var sameOnLeadingDiagonal = true
        var winMoveCellDiagonal = intArrayOf(-1, -1)
        for (leadingDiagonal in 1 until mBoardDimension) {
            sameOnLeadingDiagonal = mBoard[leadingDiagonal][leadingDiagonal] == cellValDiagonal
            winMoveCellDiagonal = intArrayOf(leadingDiagonal, leadingDiagonal)
            if (!sameOnLeadingDiagonal) break
        }
        if (sameOnLeadingDiagonal) { // It might be the empty cell that we are checking and we don't want that
            if (cellValDiagonal == mFirstPlayerID) return mFirstPlayerID
            if (cellValDiagonal == mSecondPlayerID) return mSecondPlayerID
            if (cellValDiagonal == mFirstPlayerID || cellValDiagonal == mSecondPlayerID) {
                mWinMoveCell = winMoveCellDiagonal
                mWinDirection = mContext.getString(R.string.win_direction_lead_diagonal)
            }
        }

        // Checking opposite leading diagonals for AI or Opponent victory
        // Opposite leading diagonal cell coordinate for a 3 x 3 => 0,2 | 2,2 | 2,0
        // Opposite leading diagonal cell coordinate for a 4 x 4 => 0,3 | 1,2 | 2,1 | 3,0
        val cellValOppDiagonal = mBoard[0][mBoardDimension - 1]
        var sameOnOppLeadingDiagonal = true
        var winMoveCellOppDiagonal = intArrayOf(-1, -1)
        for (oppLeadingDiagonal in 1 until mBoardDimension) {
            sameOnOppLeadingDiagonal =
                mBoard[oppLeadingDiagonal][mBoardDimension - 1 - oppLeadingDiagonal] == cellValOppDiagonal
            winMoveCellOppDiagonal = intArrayOf(oppLeadingDiagonal, oppLeadingDiagonal)
            if (!sameOnOppLeadingDiagonal) break
        }
        if (sameOnOppLeadingDiagonal) { // It might be the empty cell that we are checking and we don't want that
            if (cellValOppDiagonal == mFirstPlayerID) return mFirstPlayerID
            if (cellValOppDiagonal == mSecondPlayerID) return mSecondPlayerID
            if (cellValOppDiagonal == mFirstPlayerID || cellValOppDiagonal == mSecondPlayerID) {
                mWinMoveCell = winMoveCellOppDiagonal
                mWinDirection = mContext.getString(R.string.win_direction_opp_lead_diagonal)
            }
        }

        return mEmptyCellID // No winner
    }

    fun findWinner(moves: Array<IntArray>): String {
        generateEmptyBoard()
        fillBoardWithMoves(moves)

        val winner = evaluateBoardForWinner()

        if (winner == mFirstPlayerID) return mFirstPlayerID
        if (winner == mSecondPlayerID) return mSecondPlayerID

        return if (isMovesLeft()) mPendingID else mDrawID
    }
}