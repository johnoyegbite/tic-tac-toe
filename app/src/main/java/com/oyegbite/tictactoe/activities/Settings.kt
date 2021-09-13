package com.oyegbite.tictactoe.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.oyegbite.tictactoe.R
import com.oyegbite.tictactoe.databinding.ActivitySettingsBinding
import com.oyegbite.tictactoe.models.GameMoves
import com.oyegbite.tictactoe.utils.AppUtils
import com.oyegbite.tictactoe.utils.Constants
import com.oyegbite.tictactoe.utils.SharedPreference

class Settings : AppCompatActivity() {
    companion object {
        private val TAG: String = Settings::class.java.simpleName
    }

    private lateinit var mBinding: ActivitySettingsBinding
    private lateinit var mSharedPreference: SharedPreference
    private var prevBoardDimension: Int = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        mSharedPreference = SharedPreference(this)
        mSharedPreference.putValue(Constants.KEY_SAVED_CURRENT_ACTIVITY, Constants.Activity.Settings)

        mSharedPreference.getValue(
            Int::class.java,
            Constants.KEY_BOARD_DIMENSION,
            3
        )?.let {  prevBoardDimension = it }

        setBindings()
    }

    private fun setBindings() {
        vibrationBinding()
        boardSizeBinding()
    }

    private fun vibrationBinding() {
        val isVibrationActive = mSharedPreference.getValue(
            Boolean::class.java,
            Constants.KEY_VIBRATION_ACTIVE,
            false
        ) == true
        mBinding.vibrationChecked = isVibrationActive
        mBinding.vibrationSwitch.setOnCheckedChangeListener {
                _, isChecked ->
            run {
                mSharedPreference.putValue(Constants.KEY_VIBRATION_ACTIVE, isChecked)
            }
        }
    }

    private fun boardSizeBinding() {
        updateBoardSize()

        val minBoardDimen = 3
        val maxBoardDimen = 6

        mBinding.subtract.setOnClickListener(View.OnClickListener {
            val boardDimen = mSharedPreference.getValue(
                Int::class.java,
                Constants.KEY_BOARD_DIMENSION,
                3
            )

            boardDimen?.let {
                if (boardDimen - 1 >= minBoardDimen) {
                    mSharedPreference.putValue(Constants.KEY_BOARD_DIMENSION, boardDimen - 1)
                    updateBoardSize()
                }
            }
        })

        mBinding.add.setOnClickListener(View.OnClickListener {
            val boardSize = mSharedPreference.getValue(
                Int::class.java,
                Constants.KEY_BOARD_DIMENSION,
                3
            )

            boardSize?.let {
                if (boardSize + 1 <= maxBoardDimen) {
                    mSharedPreference.putValue(Constants.KEY_BOARD_DIMENSION, boardSize + 1)
                    updateBoardSize()
                }
            }
        })
    }

    private fun updateBoardSize() {
        val minBoardDimen = 3
        val maxBoardDimen = 6
        val boardDimen = mSharedPreference.getValue(
            Int::class.java,
            Constants.KEY_BOARD_DIMENSION,
            3
        )
        mBinding.boardDimen = boardDimen

        if (boardDimen!! == minBoardDimen) {
            mBinding.isSubtractActive = false
            mBinding.isAddActive = true
        } else if (boardDimen in (minBoardDimen + 1) until maxBoardDimen) {
            mBinding.isSubtractActive = true
            mBinding.isAddActive = true
        } else if (boardDimen == maxBoardDimen) {
            mBinding.isSubtractActive = true
            mBinding.isAddActive = false
        }
    }

    override fun onBackPressed() {
        val updatedBoardDimension = mSharedPreference.getValue(
            Int::class.java,
            Constants.KEY_BOARD_DIMENSION,
            3
        )

        if (updatedBoardDimension!! != prevBoardDimension) {
            notifyThePlayersAboutDataClearing()
        } else {
            navigateToPreviousActivity()
        }
    }

    private fun notifyThePlayersAboutDataClearing() {
        val dialog = MaterialAlertDialogBuilder(this)
            .setMessage(getString(R.string.data_would_be_cleared))
            .setNeutralButton(
                getString(R.string.yes)
            ) { dialog, _ ->
                resetBoard()
                navigateToPreviousActivity()
                dialog.dismiss()
            }
            .setPositiveButton(
                getString(R.string.no)
            ) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()

        // Display dialog at bottom of screen
        AppUtils.displayDialogAt(dialog, Gravity.CENTER)
    }

    private fun resetBoard() {
        mSharedPreference.putValue(Constants.KEY_IS_GAME_OVER, false)
        mSharedPreference.putValue(Constants.KEY_PLAYER_1_SCORE, 0)
        mSharedPreference.putValue(Constants.KEY_PLAYER_2_SCORE, 0)
        mSharedPreference.putValue(Constants.KEY_GAME_MOVES, GameMoves(arrayListOf()))
    }

    private fun navigateToPreviousActivity() {
        val previousActivityNumber = mSharedPreference.getValue(
            Int::class.java,
            Constants.KEY_SAVED_PREVIOUS_ACTIVITY,
            Constants.Activity.Scene
        )

        when(previousActivityNumber) {
            Constants.Activity.ChoosePlayMode -> {
                startActivity(Intent(this, ChoosePlayMode::class.java))
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
            Constants.Activity.Scene -> {
                startActivity(Intent(this, Scene::class.java))
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        }
    }
}