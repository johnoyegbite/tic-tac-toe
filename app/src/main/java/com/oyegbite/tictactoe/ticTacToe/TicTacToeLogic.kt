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

    /**
     * @param firstPlayer either "Player 1" or "Player 2"
     * @see R.string.first_player
     * @see R.string.second_player
     */
    fun updateWhoPlaysFirst(firstPlayer: String) {
        mSharedPreference.putValue(Constants.KEY_FIRST_PLAYER, firstPlayer)

        Log.i(TAG, "updateWhoPlaysFirst( $firstPlayer )")
        Log.i(TAG, "Previous: (mFirstPlayerID = $mFirstPlayerID, mSecondPlayerID = $mSecondPlayerID)")

        if (firstPlayer == mContext.getString(R.string.first_player)) {
            mFirstPlayerID = mContext.getString(R.string.first_player)
            mSecondPlayerID = mContext.getString(R.string.second_player)
        } else {
            mFirstPlayerID = mContext.getString(R.string.second_player)
            mSecondPlayerID = mContext.getString(R.string.first_player)
        }
        Log.i(TAG, "Current: (mFirstPlayerID = $mFirstPlayerID, mSecondPlayerID = $mSecondPlayerID)")
    }

    fun updateBoardDimension(boardDim: Int) {
        mBoardDimension = boardDim
    }

    fun getWinDirection(): String = mWinDirection

    fun getFirstWinMove(): IntArray = mWinMoveCell

    fun getMovesPlayed(): ArrayList<IntArray> = mMoves

    fun setMovesPlayed(moves: ArrayList<IntArray>) {
         mSharedPreference.getValue(
            String::class.java,
            Constants.KEY_FIRST_PLAYER,
            mContext.getString(R.string.first_player)
        ).let {
             mFirstPlayerID = it!!
         }
        mMoves = moves
        Log.i(TAG, "setMovesPlayed(${Arrays.deepToString(mMoves.toArray())})")
    }

    fun findAIBestMoveIfAvailable(): IntArray {
        mSharedPreference.getValue(
            String::class.java,
            Constants.KEY_DIFFICULTY_LEVEL,
            mContext.getString(R.string.random)
        )?.let {
            return when(it) {
                mContext.getString(R.string.easy) -> findAIBestMoveEasy()
                mContext.getString(R.string.medium) -> findAIBestMoveMedium()
                mContext.getString(R.string.hard) -> findAIBestMoveHard()
                else -> findMoveRandom()
            }
        }
        return findAIBestMoveHard()
    }

    private fun findMoveRandom(): IntArray {
        val availableCells = arrayListOf<IntArray>()
        for (row in 0 until mBoardDimension) {
            for (col in 0 until mBoardDimension) {
                val cell = getHashID(row, col)
                if (!mVisitedCells.contains(cell)) {
                    availableCells.add(intArrayOf(row, col))
                }
            }
        }
        // Pick a random cell
        return availableCells.random()
    }

    private fun findAIBestMoveEasy(): IntArray {
        return findAIBestMoveHelper(mContext.getString(R.string.easy))
    }

    private fun findAIBestMoveMedium(): IntArray {
        return findAIBestMoveHelper(mContext.getString(R.string.medium))
    }

    private fun findAIBestMoveHard(): IntArray {
        return findAIBestMoveHelper(mContext.getString(R.string.hard))
    }

    private fun findAIBestMoveHelper(difficultyLevel: String): IntArray {
        val firstPlayer = mSharedPreference.getValue(
            keyClassType = String::class.java,
            key = Constants.KEY_FIRST_PLAYER,
            defaultValue = mContext.getString(R.string.first_player)
        )

        var aiID = mContext.getString(R.string.second_player)
        var opponentID = mContext.getString(R.string.first_player)

        if (firstPlayer != mContext.getString(R.string.first_player)) {
            aiID = mContext.getString(R.string.first_player)
            opponentID = mContext.getString(R.string.second_player)
        }

        return AILogic(
            boardDimension = mBoardDimension,
            moves = getMovesPlayed(),
            aiID = aiID,
            opponentID = opponentID,
            context = mContext
        ).findBestMove(difficultyLevel)
    }



    fun boardHasEmptyCell(): Boolean {
        return (mBoardDimension * mBoardDimension) - getMovesPlayed().size > 0
    }

    private fun getHashID(row: Int, col: Int): String = "$row$col"

    fun clearBoard() {
        mMoves.clear()
        mVisitedCells.clear()
        mWinDirection = mContext.getString(R.string.win_direction_none)
        mWinMoveCell = intArrayOf(0, 0)
        mSharedPreference.putValue(Constants.KEY_GAME_MOVES, GameMoves(arrayListOf()))
    }

    /**
     * Add only to [mMoves] if board is not filled and cell is empty
     */
    fun canAddToMoves(cell: IntArray): Boolean {
        // If board is filled
        if (mMoves.size >= mBoardDimension * mBoardDimension) return false

        val (row, col) = cell

//        var moveWasAdded = false
//
//        val firstPlayer = mSharedPreference.getValue(
//            keyClassType = String::class.java,
//            key = Constants.KEY_FIRST_PLAYER,
//            defaultValue = mContext.getString(R.string.first_player)
//        )
//
//        val isPlayerOneTurn = mSharedPreference.getValue(
//            keyClassType = Boolean::class.java,
//            key = Constants.KEY_IS_PLAYER_ONE_TURN,
//            defaultValue = true
//        ) == true
//
//        var cellValue = ""
//
//        if (isPlayerOneTurn) {
//            cellValue = if (firstPlayer == mContext.getString(R.string.first_player)) {
//                firstPlayer == mContext.getString(R.string.first_player)
//            } else {
//                mContext.getString(R.string.second_player)
//            }
//        } else {
//            cellValue = if (firstPlayer == mContext.getString(R.string.first_player)) {
//                mContext.getString(R.string.second_player)
//            } else {
//                mContext.getString(R.string.first_player)
//            }
//        }
//
//        if (mBoard[row][col] == mEmptyCellID) {
//            moveWasAdded = true
//            mBoard[row][col] = cellValue
//        }
//
//        return moveWasAdded

        val moveWasAdded = mVisitedCells.add(getHashID(row, col))

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
        val boardDim = mBoardDimension // Dimension of the board
        val minLenToGetAWin = 2 * boardDim - 1
        if (moves.size < minLenToGetAWin) {
            return mContext.getString(R.string.pending)
        }

        val firstPlayerMoves = ArrayList<IntArray>()
        val secondPlayerMoves = ArrayList<IntArray>()
        // Get all the moves for first and second player
        Log.i(TAG, "mFirstPlayer = $mFirstPlayerID")
        for (i in moves.indices) {
            if (mFirstPlayerID == mContext.getString(R.string.first_player)) {
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