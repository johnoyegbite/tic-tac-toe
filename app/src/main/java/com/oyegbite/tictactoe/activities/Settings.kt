package com.oyegbite.tictactoe.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import com.oyegbite.tictactoe.R
import com.oyegbite.tictactoe.databinding.ActivitySceneBinding
import com.oyegbite.tictactoe.databinding.ActivitySettingsBinding
import com.oyegbite.tictactoe.utils.Constants
import com.oyegbite.tictactoe.utils.SharedPreference

class Settings : AppCompatActivity() {
    companion object {
        private val TAG: String = Settings::class.java.simpleName
    }

    private lateinit var mBinding: ActivitySettingsBinding
    private lateinit var mSharedPreference: SharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        mSharedPreference = SharedPreference(this)
        mSharedPreference.putValue(Constants.KEY_SAVED_ACTIVITY, Constants.Activity.Settings)

        setBindings()
    }

    private fun setBindings() {
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

    override fun onBackPressed() {
        val scene = Intent(this, Scene::class.java)
        startActivity(scene)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}