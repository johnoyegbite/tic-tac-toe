package com.oyegbite.tictactoe.ticTacToe

import android.content.Context
import android.util.Log
import com.oyegbite.tictactoe.R
import com.oyegbite.tictactoe.utils.Constants
import com.oyegbite.tictactoe.models.GameMoves
import com.oyegbite.tictactoe.utils.SharedPreference
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow

class TicTacToeLogic(context: Context, dimension: Int) {
    companion object {
        private val TAG: String = TicTacToeLogic::class.java.simpleName
    }

    private var mSharedPreference: SharedPreference = SharedPreference(context)

    private var mGameDimension: Int = dimension
    private var mMoves: ArrayList<IntArray> = arrayListOf()
    private var mVisitedCells: MutableSet<String> = mutableSetOf()
    private var mContext: Context = context
    private var mWinDirection: String = mContext.getString(R.string.win_direction_none)
    private var mWinMoveCell: IntArray = intArrayOf(0, 0)
    private var mFirstPlayer = context.getString(R.string.first_player)

    /**
     * @param firstPlayer either "Player 1" or "Player 2"
     * @see R.string.first_player
     * @see R.string.second_player
     */
    fun updateWhoPlaysFirst(firstPlayer: String) {
        Log.i(TAG, "mFirstPlayer updated to $mFirstPlayer")
        mSharedPreference.putValue(Constants.KEY_FIRST_PLAYER, firstPlayer)
        mFirstPlayer = firstPlayer
    }

    fun getWinDirection(): String {
        return mWinDirection
    }

    fun getFirstWinMove(): IntArray {
        return mWinMoveCell
    }

    fun getMovesPlayed(): ArrayList<IntArray> {
        return mMoves
    }

    fun setMovesPlayed(moves: ArrayList<IntArray>) {
         mSharedPreference.getValue(
            String::class.java,
            Constants.KEY_FIRST_PLAYER,
            mContext.getString(R.string.first_player)
        ).let {
            mFirstPlayer = it!!
         }
        mMoves = moves
    }

    fun getAvailableMove(): IntArray {
        val availableCells = arrayListOf<IntArray>()
        for (row in 0 until mGameDimension) {
            for (col in 0 until mGameDimension) {
                val cell = getKey(row, col)
                if (!mVisitedCells.contains(cell)) {
                    availableCells.add(intArrayOf(row, col))
                }
            }
        }
        // Pick a random cell
        return availableCells.random()
    }

    fun boardHasEmptyCell(): Boolean {
        return (mGameDimension * mGameDimension) - getMovesPlayed().size > 0
    }

    private fun getKey(row: Int, col: Int): String {
        return "$row$col"
    }

    fun clearBoard() {
        mMoves.clear()
        mVisitedCells.clear()
        mWinDirection = mContext.getString(R.string.win_direction_none)
        mWinMoveCell = intArrayOf(0, 0)
    }

    /**
     * Add only to [mMoves] if board is not filled and cell is empty
     */
    fun addToMoves(cell: IntArray): Boolean {
        if (mMoves.size >= mGameDimension * mGameDimension) return false

        val (row, col) = cell
        val moveWasAdded = mVisitedCells.add(getKey(row, col))

        if (moveWasAdded) {
            mMoves.add(cell)
            val gameMoves = GameMoves(mMoves)
            mSharedPreference.putValue(Constants.KEY_GAME_MOVES, gameMoves)
            Log.i(TAG, "moves = ${Arrays.deepToString(mMoves.toArray())}")
        }
        Log.i(TAG, "moveWasAdded = $moveWasAdded")
        return moveWasAdded
    }

    /**
     * Players take turns placing characters into empty squares (" ").
     * The first player A always places "X" characters, while the second player B always places "O" characters.
     * "X" and "O" characters are always placed into empty squares, never on filled ones.
     * The game ends when there are 3 of the same (non-empty) character filling any row, column, or diagonal.
     * The game also ends if all squares are non-empty.
     * No more moves can be played if the game is over
     *
     * Example 1:
     *  Input: moves = [[0,0],[1,1]]
     *  Output: "Pending"
     *  Explanation: The game has not finished yet.
     *      "X  "
     *      " O "
     *      "   "
     *
     * Example 2:
     *  Input: moves = [[0,0],[2,0],[1,1],[2,1],[2,2]]
     *  Output: "A"
     *  Explanation: "A" wins, he always plays first.
     *      "X  "    "X  "    "X  "    "X  "    "X  "
     *      "   " -> "   " -> " X " -> " X " -> " X "
     *      "   "    "O  "    "O  "    "OO "    "OOX"
     */
    fun getGameWinState(moves: ArrayList<IntArray>): String {
        // Minimum number of play (or minimum length of moves) to have a win
        // 5  (2 x 3 - 1) = A B A B A = 3 X 3 (for a 3 by 3 board)
        // 7  (2 x 4 - 1) = A B A B A B A = 4 X 4 (for a 4 by 4 board)
        // 9  (2 x 5 - 1) = A B A B A B A B A = 5 X 5 (for a 5 by 5 board)
        // 11 (2 x 6 - 1) = A B A B A B A B A B A = 6 X 6 (for a 6 by 6 board)
        //     ...        =   ...
        val boardDim = mGameDimension // Dimension of the board
        val minLenToGetAWin = 2 * boardDim - 1
        if (moves.size < minLenToGetAWin) {
            return mContext.getString(R.string.pending)
        }

        val firstPlayerMoves = ArrayList<IntArray>()
        val secondPlayerMoves = ArrayList<IntArray>()
        // Get all the moves for first and second player
        Log.i(TAG, "mFirstPlayer = $mFirstPlayer")
        for (i in moves.indices) {
            if (mFirstPlayer == mContext.getString(R.string.first_player)) {
                if (i % 2 == 0) {  // moves for first player
                    firstPlayerMoves.add(moves[i])
                } else {  // moves for second player
                    secondPlayerMoves.add(moves[i])
                }
            } else {
                if (i % 2 == 0) {  // moves for first player
                    secondPlayerMoves.add(moves[i])
                } else {  // moves for second player
                    firstPlayerMoves.add(moves[i])
                }
            }
        }

        // Check if 1st player wins (from all possible "boardDim" combinations)
        val firstPlayerWins = winMoves(firstPlayerMoves, boardDim)
        if (firstPlayerWins.size> 0) {
            return mContext.getString(R.string.first_player)
        }

        // Check if 2nd player wins (from all possible "boardDim" combinations)
        val secondPlayerWins = winMoves(secondPlayerMoves, boardDim)
        if (secondPlayerWins.size > 0) {
            return mContext.getString(R.string.second_player)
        }

        // If we can't find a winner and
        // 1. both players hasn't exhausted all moves then it is Pending.
        // 2. both players has exhausted all moves then it is a Draw.
        return if (moves.size != boardDim * boardDim) mContext.getString(R.string.pending) else mContext.getString(
            R.string.draw
        )
    }


    private fun winMoves(playerMoves: ArrayList<IntArray>, boardDim: Int): ArrayList<IntArray> {
        // Check all possible boardDim combinations in playerMoves.
        val totalMoves: Int = playerMoves.size
        val power = 2.0.pow(totalMoves.toDouble()).toInt() // size of all possible subsets
        for (n in 1 until power) {
            val binString = Integer.toBinaryString(n)
            // I only want only the subset whose length is equal to boardDim
            if (countOnesIn(binString) == boardDim) {
                // using the knowledge of binary conversion to the advantage here
                // if s = "abcd" => power = 2^len(s) = 16
                // say n = 2; 2 == 0010 base 2.
                // hence pick the item at index 3
                // say n = 7; 7 == 0111 base 2.
                // hence pick only the last 3 items
                val moves = ArrayList<IntArray>()
                var idx = totalMoves - 1 // index to pick the item from
                var num = n
                while (num > 0) {
                    val rem = num % 2
                    if (rem == 1) {
                        moves.add(playerMoves[idx])
                    }
                    idx -= 1
                    num /= 2
                }
                if (didPlayerWin(moves, boardDim)) {
                    return moves
                }
            }
        }

        return ArrayList()
    }

    private fun didPlayerWin(moves: ArrayList<IntArray>, boardDim: Int): Boolean {
        /** Say boardDim is 3.
         * [0,0] [0,1] [0,2]
         * [1,0] [1,1] [1,2]
         * [2,0] [2,1] [2,2]
         *
         * Ap are the combinations of A moves that won.
         * Ap     B     Ap    B     A     B     Ap
         * [[1,2],[2,1],[1,0],[0,0],[0,1],[2,0],[1,1]]
         * [B] [A] [ ]
         * [A] [A] [A]
         * [B] [B] [ ]
         *
         */
        val moveStart = moves[0]

        // Check if all moves are on the same row.
        var onSameRow = true
        // Check if all moves are on the same column
        var onSameCol = true
        // Check if all moves are on the leading diagonal
        var onLeadingDiagonal = true
        // Check if all moves are on the opposite leading diagonal
        var onOppLeadingDiagonal = true

        for (i in 0 until moves.size) {
            val curr = moves[i]
            if (curr[0] != moveStart[0]) {
                onSameRow = false
            }
            if (curr[1] != moveStart[1]) {
                onSameCol = false
            }
            if (curr[0] != curr[1]) {
                onLeadingDiagonal = false
            }
            if (curr[0] + curr[1] != boardDim - 1) {
                onOppLeadingDiagonal = false
            }
        }

        if (onSameRow) {
            mWinDirection = mContext.getString(R.string.win_direction_row)
            mWinMoveCell = moves[0].copyOf()
        }
        if (onSameCol) {
            mWinDirection = mContext.getString(R.string.win_direction_col)
            mWinMoveCell = moves[0].copyOf()
        }
        if (onLeadingDiagonal) {
            mWinDirection = mContext.getString(R.string.win_direction_lead_diagonal)
            mWinMoveCell = moves[0].copyOf()
        }
        if (onOppLeadingDiagonal) {
            mWinDirection = mContext.getString(R.string.win_direction_opp_lead_diagonal)
            mWinMoveCell = moves[0].copyOf()
        }

        return onSameRow || onSameCol || onLeadingDiagonal || onOppLeadingDiagonal
    }

    private fun countOnesIn(str: String): Int {
        var count = 0
        for (element in str) {
            if (element == '1') {
                count += 1
            }
        }
        return count
    }
}