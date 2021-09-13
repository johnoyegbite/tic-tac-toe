package com.oyegbite.tictactoe.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.oyegbite.tictactoe.R
import com.oyegbite.tictactoe.databinding.ActivityChooseYourSideBinding
import com.oyegbite.tictactoe.utils.AppUtils
import com.oyegbite.tictactoe.utils.Constants
import com.oyegbite.tictactoe.utils.SharedPreference

class ChooseYourSide : AppCompatActivity() {
    companion object {
        private val TAG: String = ChooseYourSide::class.java.simpleName
    }

    private lateinit var mBinding: ActivityChooseYourSideBinding
    private lateinit var mSharedPreference: SharedPreference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_choose_your_side)
        mSharedPreference = SharedPreference(this)
        mSharedPreference.putValue(Constants.KEY_SAVED_CURRENT_ACTIVITY, Constants.Activity.ChooseYourSide)

        setBindings()
    }

    private fun setBindings() {
        mBinding.playWithAI = !AppUtils.isTwoPlayerMode(TAG, mSharedPreference)
        mBinding.playerOneName =
            mSharedPreference.getValue(String::class.java, Constants.KEY_PLAYER_1_NAME)

        // Save 1st Player as "O" if choose "O"
        mBinding.oChoose.setOnClickListener(View.OnClickListener {
            val isOChecked = mBinding.oChoose.isChecked
            Log.i(TAG, "O checked is $isOChecked")

            if (isOChecked) {
                mBinding.isSideChosen = true
                mBinding.isSideChosenX = false
                mSharedPreference.putValue(Constants.KEY_PLAYER_1_SIDE, Constants.VALUE_PLAYER_1_SIDE_O)

                val isXChecked = mBinding.xChoose.isChecked
                Log.i(TAG, "X checked was $isXChecked")
                if (isXChecked) { // Remove "X" from chosen
                    mBinding.xChoose.isChecked = false
                }
            }
        })

        // Save 1st Player as "X" if choose "X"
        mBinding.xChoose.setOnClickListener(View.OnClickListener {
            val isXChecked = mBinding.xChoose.isChecked
            Log.i(TAG, "X checked is $isXChecked")

            if (isXChecked) {
                mBinding.isSideChosen = true
                mBinding.isSideChosenX = true
                mSharedPreference.putValue(Constants.KEY_PLAYER_1_SIDE, Constants.VALUE_PLAYER_1_SIDE_X)

                val isOChecked = mBinding.oChoose.isChecked
                Log.i(TAG, "O checked was $isOChecked")
                if (isOChecked) { // Remove "O" from chosen
                    mBinding.oChoose.isChecked = false
                }
            }
        })

        mBinding.startGame.setOnClickListener(View.OnClickListener {
            resetFields()
            navigateToNextActivity()
        })
    }

    private fun resetFields() {
        mSharedPreference.putValue(Constants.KEY_IS_GAME_SAVED, true)
        mSharedPreference.putValue(Constants.KEY_IS_GAME_OVER, false)
        mSharedPreference.putValue(Constants.KEY_PLAYER_1_SCORE, 0)
        mSharedPreference.putValue(Constants.KEY_PLAYER_2_SCORE, 0)
        mSharedPreference.putValue(Constants.KEY_FIRST_PLAYER, getString(R.string.first_player))
        mSharedPreference.putValue(Constants.KEY_IS_PLAYER_ONE_TURN, true)
    }

    override fun onBackPressed() {
        navigateToPreviousActivity()
    }

    private fun navigateToNextActivity() {
        startActivity(Intent(this, Scene::class.java))
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun navigateToPreviousActivity() {
        startActivity(Intent(this, EnterPlayerName::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}