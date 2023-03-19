package com.oyegbite.tictactoe.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
    private var minBoardDimen: Int = 3
    private var maxBoardDimen: Int = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings)

        mSharedPreference = SharedPreference(this)
        mSharedPreference.putValue(
            Constants.KEY_SAVED_CURRENT_ACTIVITY,
            Constants.Activity.Settings
        )

        mSharedPreference.getValue(
            Int::class.java,
            Constants.KEY_BOARD_DIMENSION,
            minBoardDimen
        )?.let {  prevBoardDimension = it }

        setBindings()
    }

    private fun setBindings() {
        vibrationBinding()
        boardSizeBinding()
        difficultyLevelBinding()
    }

    private fun vibrationBinding() {
        val isVibrationActive = mSharedPreference.getValue(
            Boolean::class.java,
            Constants.KEY_VIBRATION_ACTIVE,
            false
        ) == true

        mBinding.vibrationLayout.vibrationChecked = isVibrationActive
        mBinding.vibrationLayout.vibrationSwitch.setOnCheckedChangeListener {
                _, isChecked ->
            run {
                mSharedPreference.putValue(Constants.KEY_VIBRATION_ACTIVE, isChecked)
            }
        }
    }

    private fun boardSizeBinding() {
        updateBoardDimension()

        mBinding.boardSizeLayout.subtract.setOnClickListener(View.OnClickListener {
            val boardDimen = mSharedPreference.getValue(
                Int::class.java,
                Constants.KEY_BOARD_DIMENSION,
                minBoardDimen
            )

            boardDimen?.let {
                if (boardDimen - 1 >= minBoardDimen) {
                    mSharedPreference.putValue(Constants.KEY_BOARD_DIMENSION, boardDimen - 1)
                    updateBoardDimension()
                }
            }
        })

        mBinding.boardSizeLayout.add.setOnClickListener(View.OnClickListener {
            val boardSize = mSharedPreference.getValue(
                Int::class.java,
                Constants.KEY_BOARD_DIMENSION,
                minBoardDimen
            )

            boardSize?.let {
                if (boardSize + 1 <= maxBoardDimen) {
                    mSharedPreference.putValue(Constants.KEY_BOARD_DIMENSION, boardSize + 1)
                    updateBoardDimension()
                }
            }
        })
    }

    private fun difficultyLevelBinding() {
        val difficultyLevelArray = resources.getStringArray(R.array.difficulty_levels)

        ArrayAdapter.createFromResource(
            this,
            R.array.difficulty_levels,
            android.R.layout.simple_spinner_item
        ).also { arrayAdapter ->
            // Populate the Spinner dropdown
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mBinding.difficultyLevelLayout.difficultyLevel.adapter = arrayAdapter

            // Set a starting value to show the Player
            mSharedPreference.getValue(
                keyClassType = String::class.java,
                key = Constants.KEY_DIFFICULTY_LEVEL,
                defaultValue = getString(R.string.random)
            ).let { level ->
                val levelIdx = difficultyLevelArray.indexOf(level)
                mBinding.difficultyLevelLayout.difficultyLevel.setSelection(levelIdx)
                Log.i(TAG, "Previous level = $level, Corresponding index = $levelIdx")
            }
        }

        mBinding.difficultyLevelLayout.difficultyLevel.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long)
                {
                    // An item was selected.
                    when (parent.getItemAtPosition(pos)) {
                        getString(R.string.easy) -> {
                            mSharedPreference.putValue(
                                Constants.KEY_DIFFICULTY_LEVEL,
                                getString(R.string.easy)
                            )
                        }
                        getString(R.string.medium) -> {
                            mSharedPreference.putValue(
                                Constants.KEY_DIFFICULTY_LEVEL,
                                getString(R.string.medium)
                            )
                        }
                        getString(R.string.hard) -> {
                            mSharedPreference.putValue(
                                Constants.KEY_DIFFICULTY_LEVEL,
                                getString(R.string.hard)
                            )
                        }
                        else -> {
                            mSharedPreference.putValue(
                                Constants.KEY_DIFFICULTY_LEVEL,
                                getString(R.string.random)
                            )
                        }
                    }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }
    }

    private fun updateBoardDimension() {
        val boardDimen = mSharedPreference.getValue(
            Int::class.java,
            Constants.KEY_BOARD_DIMENSION,
            minBoardDimen
        )
        mBinding.boardSizeLayout.boardDimen = boardDimen

        if (boardDimen!! == minBoardDimen) {
            mBinding.boardSizeLayout.isSubtractActive = false
            mBinding.boardSizeLayout.isAddActive = true
        } else if (boardDimen in (minBoardDimen + 1) until maxBoardDimen) {
            mBinding.boardSizeLayout.isSubtractActive = true
            mBinding.boardSizeLayout.isAddActive = true
        } else if (boardDimen == maxBoardDimen) {
            mBinding.boardSizeLayout.isSubtractActive = true
            mBinding.boardSizeLayout.isAddActive = false
        }
    }

    private fun notifyThePlayersAboutGameReset() {
        val dialog = MaterialAlertDialogBuilder(this)
            .setMessage(getString(R.string.game_reset_notification))
            .setNeutralButton(
                getString(R.string.yes)
            ) { dialog, _ ->
                resetGame()
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

    private fun resetGame() {
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

    override fun onBackPressed() {
        val updatedBoardDimension = mSharedPreference.getValue(
            Int::class.java,
            Constants.KEY_BOARD_DIMENSION,
            minBoardDimen
        )

        if (updatedBoardDimension!! != prevBoardDimension) {
            notifyThePlayersAboutGameReset()
        } else {
            navigateToPreviousActivity()
        }
    }
}