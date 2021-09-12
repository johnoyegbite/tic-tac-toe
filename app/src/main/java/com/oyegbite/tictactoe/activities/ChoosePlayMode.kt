package com.oyegbite.tictactoe.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.oyegbite.tictactoe.MainActivity
import com.oyegbite.tictactoe.R
import com.oyegbite.tictactoe.databinding.ActivityChoosePlayModeBinding
import com.oyegbite.tictactoe.utils.AppUtils
import com.oyegbite.tictactoe.utils.Constants
import com.oyegbite.tictactoe.utils.SharedPreference

class ChoosePlayMode : AppCompatActivity() {
    companion object {
        private val TAG: String = ChoosePlayMode::class.java.simpleName
    }

    private lateinit var mBinding: ActivityChoosePlayModeBinding
    private lateinit var mSharedPreference: SharedPreference
    private var mCountDownTimer: CountDownTimer? = null
    private var mIsBackBtnDoubleClicked = false
    private var mToast: Toast? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_choose_play_mode)
        mSharedPreference = SharedPreference(this)
        mSharedPreference.putValue(Constants.KEY_SAVED_ACTIVITY, ChoosePlayMode::class.java)

        setBindings()
    }

    private fun setBindings() {
        mBinding.playAi.setOnClickListener(View.OnClickListener {
            savePlayMode(Constants.VALUE_PLAY_MODE_AI)
        })

        mBinding.playFriend.setOnClickListener(View.OnClickListener {
            savePlayMode(Constants.VALUE_PLAY_MODE_FRIEND)
        })
    }

    private fun savePlayMode(playMode: String) {
        Log.i(TAG, "playMode = $playMode")
        mSharedPreference.putValue(Constants.KEY_PLAY_MODE, playMode)

        val enterPlayerName = Intent(this, EnterPlayerName::class.java)
        startActivity(enterPlayerName)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun onBackPressed() {
        if (mIsBackBtnDoubleClicked) {
            super.onBackPressed()
            stopTimer()
            mSharedPreference.putValue(Constants.KEY_SAVED_ACTIVITY, MainActivity::class.java)
            finishAffinity()
            return
        }

        mIsBackBtnDoubleClicked = true
        mToast = AppUtils.showToast(
            this,
            getString(R.string.touch_to_exit),
            Toast.LENGTH_SHORT,
            mToast
        )

        val countDownInterval: Long = 1000
        val stopInterval: Long = 2000

        mCountDownTimer = object : CountDownTimer(
            stopInterval,
            countDownInterval
        ) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                mIsBackBtnDoubleClicked = false
                stopTimer()
            }
        }.start()
    }

    private fun stopTimer() {
        mCountDownTimer?.let {
            it.cancel()
            mCountDownTimer = null
        }
    }

    override fun onStop() {
        stopTimer()
        super.onStop()
    }

    override fun onDestroy() {
        stopTimer()
        super.onDestroy()
    }

}