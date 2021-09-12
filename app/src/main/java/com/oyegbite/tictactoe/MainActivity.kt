package com.oyegbite.tictactoe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import com.oyegbite.tictactoe.activities.ChoosePlayMode
import com.oyegbite.tictactoe.activities.Scene
import com.oyegbite.tictactoe.databinding.ActivitySplashBinding
import com.oyegbite.tictactoe.utils.Constants
import com.oyegbite.tictactoe.utils.SharedPreference

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG: String = MainActivity::class.java.simpleName
    }

    private lateinit var mSharedPreference: SharedPreference
    private lateinit var mBinding: ActivitySplashBinding
    private var mCountDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        mSharedPreference = SharedPreference(this)

        val weHaveGameSaved = mSharedPreference.getValue(
            Boolean::class.java,
            Constants.KEY_IS_GAME_SAVED,
            false
        ) == true

        if (weHaveGameSaved) {
            navigateToScene()
            finish()
        }

//        val getSavedActivity = mSharedPreference.getValue(
//            String::class.java,
//            Constants.KEY_SAVED_ACTIVITY,
//            ChoosePlayMode::class.java
//        )

        startCountDownTimer()
    }

    private fun navigateToScene() {
        val scene = Intent(this, Scene::class.java)
        startActivity(scene)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun startCountDownTimer() {
        val choosePlayModeActivity = Intent(this, ChoosePlayMode::class.java)
        val countDownInterval: Long = 500
        val stopInterval: Long = 1000

        mCountDownTimer = object : CountDownTimer(
            stopInterval,
            countDownInterval
        ) {
            override fun onTick(millisUntilFinished: Long) {
                Log.i(TAG, "millisUntilFinished = $millisUntilFinished")

                if (millisUntilFinished < countDownInterval) {
                    fadeLogoIn()
                }
                if (millisUntilFinished >= countDownInterval) {
                    fadeGameNameIn()
                }
            }
            override fun onFinish() {
                startActivity(choosePlayModeActivity)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        }.start()
    }

    private fun fadeLogoIn() {
        val fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        mBinding.logo.alpha = 1F
        mBinding.logo.startAnimation(fadeInAnim)
    }

    private fun fadeGameNameIn() {
        val fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        mBinding.gameName.alpha = 1F
        mBinding.gameName.startAnimation(fadeInAnim)
    }

    private fun cancelTimer() {
        mCountDownTimer?.let {
            it.cancel()
            mCountDownTimer = null
        }
    }

    override fun onStop() {
        cancelTimer()
        super.onStop()
    }

    override fun onDestroy() {
        cancelTimer()
        super.onDestroy()
    }
}