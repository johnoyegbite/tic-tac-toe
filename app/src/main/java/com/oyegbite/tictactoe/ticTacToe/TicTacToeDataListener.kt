package com.oyegbite.tictactoe.ticTacToe

interface TicTacToeDataListener {

    fun updateNextPlayerTurn()

    fun updateGameOverState(isGameOver: Boolean, winnerSideOrDraw: String)

    fun vibrateDevice(vibrationMills: Long)
}