package com.oyegbite.tictactoe.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.material.textfield.TextInputEditText
import com.oyegbite.tictactoe.R
import com.oyegbite.tictactoe.databinding.ActivityEnterPlayerNameBinding
import com.oyegbite.tictactoe.utils.AppUtils
import com.oyegbite.tictactoe.utils.Constants
import com.oyegbite.tictactoe.utils.SharedPreference

class EnterPlayerName : AppCompatActivity() {
    companion object {
        private val TAG: String = EnterPlayerName::class.java.simpleName
    }

    private lateinit var mBinding: ActivityEnterPlayerNameBinding
    private lateinit var mSharedPreference: SharedPreference
    private var mToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "EnterPlayerName activity.")
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_enter_player_name)

        mSharedPreference = SharedPreference(this)
        mSharedPreference.putValue(
            Constants.KEY_SAVED_CURRENT_ACTIVITY,
            Constants.Activity.EnterPlayerName
        )

        setBindingsAndListeners()
        setDefaultPlayerNames()
    }

    private fun setBindingsAndListeners() {
        mBinding.isTwoPlayer = AppUtils.isTwoPlayerMode(TAG, mSharedPreference)
        mBinding.chooseSide.setOnClickListener(View.OnClickListener {
            if (playerNamesAreSet()) {
                navigateToChooseYourSideActivity()
            }
        })
    }

    private fun playerNamesAreSet(): Boolean {
        val playerOneName = mBinding.playerOneName.text.toString().trim()
        var namesAreSet = isPlayerNameSet(playerOneName, Constants.KEY_PLAYER_1_NAME)

        if (AppUtils.isTwoPlayerMode(TAG, mSharedPreference)) {
            val playerTwoName = mBinding.playerTwoName.text.toString().trim()
            namesAreSet = namesAreSet && isPlayerNameSet(playerTwoName, Constants.KEY_PLAYER_2_NAME)
        }

        return namesAreSet
    }

    private fun isPlayerNameSet(playerName: String, playerKey: String): Boolean {
        if (!TextUtils.isEmpty(playerName)) {
            if (playerName.lowercase() != getString(R.string.hint_ai).lowercase()) {
                mSharedPreference.putValue(playerKey, playerName)
            } else {
                mToast = AppUtils.showToast(
                    this,
                    getString(R.string.choose_another_name),
                    Toast.LENGTH_SHORT,
                    mToast
                )
                return false
            }
        } else {
            if (playerKey == Constants.KEY_PLAYER_1_NAME) {
                mSharedPreference.putValue(playerKey, getString(R.string.hint_player_1))
            } else {
                mSharedPreference.putValue(playerKey, getString(R.string.hint_player_2))
            }
        }
        return true
    }

    private fun setDefaultPlayerNames() {
        setDefaultPlayerName(
            mBinding.playerOneName,
            Constants.KEY_PLAYER_1_NAME,
            R.string.hint_player_1
        )

        if (AppUtils.isTwoPlayerMode(TAG, mSharedPreference)) {
            Log.i(TAG, "setDefaultPlayerNames() => Two Player Mode")
            setDefaultPlayerName(
                mBinding.playerTwoName,
                Constants.KEY_PLAYER_2_NAME,
                R.string.hint_player_2
            )
        }
    }

    private fun setDefaultPlayerName(
        playerNameInput: TextInputEditText,
        prevPlayerNameKey: String,
        defaultPlayerNameRString: Int
    ) {
        val prevPlayerName: String? = mSharedPreference.getValue(
            String::class.java,
            prevPlayerNameKey,
            getString(defaultPlayerNameRString)
        )
        Log.i(TAG, "setDefaultPlayerName() => prevPlayerName: $prevPlayerName")

        mSharedPreference.putValue(prevPlayerNameKey, prevPlayerName)
        playerNameInput.setText(
            if (prevPlayerName == getString(defaultPlayerNameRString)) {
                ""
            } else {
                prevPlayerName
            }
        )
    }

    private fun navigateToChooseYourSideActivity() {
        startActivity(Intent(this, ChooseYourSide::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun navigateToChoosePlayModeActivity() {
        startActivity(Intent(this, ChoosePlayMode::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun onBackPressed() {
        navigateToChoosePlayModeActivity()
    }
}