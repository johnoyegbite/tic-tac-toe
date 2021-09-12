package com.oyegbite.tictactoe.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.oyegbite.tictactoe.R
import com.oyegbite.tictactoe.databinding.ActivitySceneBinding
import com.oyegbite.tictactoe.models.GameMoves
import com.oyegbite.tictactoe.ticTacToe.TicTacToeDataListener
import com.oyegbite.tictactoe.utils.*
import java.util.*

import androidx.appcompat.app.AlertDialog
import android.os.VibrationEffect

import android.os.Build

import android.os.Vibrator
import android.view.*


class Scene : AppCompatActivity(), TicTacToeDataListener {
    companion object {
        private val TAG: String = Scene::class.java.simpleName
    }

    private lateinit var mBinding: ActivitySceneBinding
    private lateinit var mSharedPreference: SharedPreference
    private var shouldCancelCurrDialog = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_scene)
        mSharedPreference = SharedPreference(this)
        mSharedPreference.putValue(Constants.KEY_SAVED_ACTIVITY, Constants.Activity.Scene)

        mBinding.ticTacToe.initListener(this)
        setBindings()
        setStartingFields()
//        drawSavedGame()
    }

    private fun setStartingFields() {
        // Set Player1 and Player2 names
        mBinding.playerOneName = mSharedPreference.getValue(
            String::class.java,
            Constants.KEY_PLAYER_1_NAME
        )
        mBinding.playerTwoName = if (AppUtils.isTwoPlayerMode(TAG, mSharedPreference)) {
            mSharedPreference.getValue(String::class.java, Constants.KEY_PLAYER_2_NAME)
        } else {
            getString(R.string.hint_ai)
        }

        // Set Player1 and Player2 scores
        mBinding.playerOneScore = mSharedPreference.getValue(
            Int::class.java,
            Constants.KEY_PLAYER_1_SCORE,
            0
        )
        mBinding.playerTwoScore = mSharedPreference.getValue(
            Int::class.java,
            Constants.KEY_PLAYER_2_SCORE,
            0
        )

        // Set Player1 to "X" or "O"
        mBinding.isPlayerOneX = mSharedPreference.getValue(
             String::class.java,
             Constants.KEY_PLAYER_1_SIDE
         ) == Constants.VALUE_PLAYER_1_SIDE_X

        val isGameOver = mSharedPreference.getValue(
            Boolean::class.java,
            Constants.KEY_IS_GAME_OVER,
            false
        )

        mBinding.gameOver = isGameOver
        shouldCancelCurrDialog = !isGameOver!!

        mBinding.isPlayerOneTurn = mSharedPreference.getValue(
            Boolean::class.java,
            Constants.KEY_IS_PLAYER_ONE_TURN,
            true
        )
    }

    private fun setBindings() {
        mBinding.settings.setOnClickListener(View.OnClickListener {
            val settings = Intent(this, Settings::class.java)
            startActivity(settings)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        })
    }

    override fun vibrateDevice(vibrationMills: Long) {
        val isVibrationActive = mSharedPreference.getValue(
            Boolean::class.java,
            Constants.KEY_VIBRATION_ACTIVE,
            false
        ) == true

        if (isVibrationActive) {
            val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(vibrationMills, VibrationEffect.DEFAULT_AMPLITUDE))

            } else {
                //deprecated in API 26
                v.vibrate(vibrationMills)
            }
        }
    }

    override fun updateNextPlayerTurn() {
        val isPlayerOneTurn: Boolean = mSharedPreference.getValue(
            Boolean::class.java,
            Constants.KEY_IS_PLAYER_ONE_TURN,
            true
        ) == true

        val nextTurn = !isPlayerOneTurn

        Log.i(TAG, "isPlayerOneTurn = $nextTurn")

        mSharedPreference.putValue(Constants.KEY_IS_PLAYER_ONE_TURN, nextTurn)
        mBinding.isPlayerOneTurn = nextTurn
    }

    override fun updateGameOverState(isGameOver: Boolean, winnerSideOrDraw: String) {
        mBinding.gameOver = isGameOver
        fadeGameOverBoardIn()
        showContinueOrResetPopUp()

        Log.i(TAG, "winnerSideOrDraw = $winnerSideOrDraw")
        when(winnerSideOrDraw) {
            getString(R.string.draw) -> {
                mBinding.winnersName = getString(R.string.draw)
            }
            getString(R.string.first_player) -> {
                mBinding.winnersName = "${mSharedPreference.getValue(
                    String::class.java, 
                    Constants.KEY_PLAYER_1_NAME
                )} wins"
                val playerOnePrevScore = mSharedPreference.getValue(
                    Int::class.java,
                    Constants.KEY_PLAYER_1_SCORE,
                    0
                )
                mSharedPreference.putValue(Constants.KEY_PLAYER_1_SCORE, playerOnePrevScore!! + 1)
                mBinding.playerOneScore = playerOnePrevScore + 1
            }
            getString(R.string.second_player) -> {
                mBinding.winnersName = if (AppUtils.isTwoPlayerMode(TAG, mSharedPreference)) {
                    "${mSharedPreference.getValue(String::class.java, Constants.KEY_PLAYER_2_NAME)} wins"
                } else {
                    "${getString(R.string.hint_ai)} wins"
                }
                val playerTwoPrevScore = mSharedPreference.getValue(
                    Int::class.java,
                    Constants.KEY_PLAYER_2_SCORE,
                    0
                )
                mSharedPreference.putValue(Constants.KEY_PLAYER_2_SCORE, playerTwoPrevScore!! + 1)
                mBinding.playerTwoScore = playerTwoPrevScore + 1
            }
            else -> {
                TODO("Something to think of later")
            }
        }

        mSharedPreference.putValue(Constants.KEY_IS_GAME_OVER, isGameOver)
        mSharedPreference.putValue(Constants.KEY_WINNERS_SIDE_OR_DRAW, winnerSideOrDraw)
    }

    private fun showContinueOrResetPopUp() {
        val dialog = MaterialAlertDialogBuilder(this)
            .setNeutralButton(
                getString(R.string.exit)
            ) { dialog, _ ->
                shouldCancelCurrDialog = false
                showExitGameDialog()
                dialog.dismiss()
            }
            .setNegativeButton(
                getString(R.string.reset)
            ) { dialog, _ ->
                resetGameBoard()
                dialog.dismiss()
            }
            .setPositiveButton(
                getString(R.string.continue_text)
            ) { dialog, _ ->
                continuePlay()
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()

        // Display dialog at bottom of screen
        displayDialogAt(dialog, Gravity.BOTTOM)
    }

    private fun showExitGameDialog() {
        mBinding.ticTacToe.cancelAIPlay()
        Log.i(TAG, "homeButtonPressed = $shouldCancelCurrDialog")
        val dialog = MaterialAlertDialogBuilder(this)
            .setMessage(getString(R.string.confirm_exit))
            .setNeutralButton(
                getString(R.string.yes)
            ) { dialog, _ ->
                resetGameBoard()
                navigateUserToHome()
                dialog.dismiss()
            }
            .setPositiveButton(
                getString(R.string.no)
            ) { dialog, _ ->
                // Only show the showContinueOrResetPopUp() if we didn't get to
                // the showExitGameDialog() via clicking the back-button
                if (!shouldCancelCurrDialog) {
                    showContinueOrResetPopUp()
                } else {
                    shouldCancelCurrDialog = false // reset it to false
                    mBinding.ticTacToe.aiPlays()
                }
                dialog.dismiss()
            }
            .setCancelable(shouldCancelCurrDialog)
            .show()

        // Display dialog at bottom of screen
        displayDialogAt(dialog, Gravity.BOTTOM)
    }

    private fun displayDialogAt(dialog: AlertDialog, position: Int) {
        val window: Window? = dialog.window
        val wlp: WindowManager.LayoutParams? = window?.attributes
        wlp?.gravity = position
        window?.attributes = wlp
        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    private fun navigateUserToHome() {
        mSharedPreference.putValue(Constants.KEY_IS_GAME_SAVED, false)
        val choosePlayModeActivity = Intent(this, ChoosePlayMode::class.java)
        startActivity(choosePlayModeActivity)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    private fun continuePlay() {
        mSharedPreference.putValue(Constants.KEY_IS_GAME_OVER, false)
        mBinding.gameOver = false
        mBinding.ticTacToe.clear()
    }

    private fun resetGameBoard() {
        mSharedPreference.putValue(Constants.KEY_IS_GAME_OVER, false)
        mBinding.gameOver = false
        mSharedPreference.putValue(Constants.KEY_PLAYER_1_SCORE, 0)
        mBinding.playerOneScore = 0
        mSharedPreference.putValue(Constants.KEY_PLAYER_2_SCORE, 0)
        mBinding.playerTwoScore = 0
        mBinding.ticTacToe.clear()
    }

    private fun fadeGameOverBoardIn() {
        val fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        mBinding.gameOverBoard.alpha = 1F
        mBinding.gameOverBoard.startAnimation(fadeInAnim)
    }

    override fun onBackPressed() {
        val isGameOver = mSharedPreference.getValue(
            Boolean::class.java,
            Constants.KEY_IS_GAME_OVER,
            false
        ) == true
        shouldCancelCurrDialog = !isGameOver
        if (!isGameOver) {
            showExitGameDialog()
        }
    }

    private fun drawSavedGame() {
        // Make sure the Board is drawn before you add the saved moves.
        mBinding.ticTacToe.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mBinding.ticTacToe.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    mSharedPreference.getValue(
                        Boolean::class.java,
                        Constants.KEY_IS_GAME_SAVED,
                        false
                    )?.let { weHaveSavedGame ->
                        if (weHaveSavedGame) {
                            val gameMoves = mSharedPreference.getValue(
                                GameMoves::class.java,
                                Constants.KEY_GAME_MOVES,
                                GameMoves(arrayListOf())
                            )?.getGameMoves()

                            val ticTacToaBoard =  mBinding.ticTacToe

                            Log.i(TAG, "weHaveSavedGame = '$weHaveSavedGame'")
                            Log.i(TAG, "moves = ${Arrays.deepToString(gameMoves!!.toArray())}")

//                            ticTacToaBoard.setMovesPlayed(gameMoves)
                            ticTacToaBoard.invalidate()
                            ticTacToaBoard.aiPlays()
                        }
                    }
                }
            })
    }

}