package com.oyegbite.tictactoe.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
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
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_enter_player_name)
        mSharedPreference = SharedPreference(this)
        mSharedPreference.putValue(Constants.KEY_SAVED_ACTIVITY, EnterPlayerName::class.java)

        mBinding.isTwoPlayer = AppUtils.isTwoPlayerMode(TAG, mSharedPreference)
        setDefaultPlayerNames()
        setBindings()
    }

    private fun setBindings() {
        mBinding.continueToChooseSide.setOnClickListener(View.OnClickListener {
            if (playerNamesAreSet()) {
                val chooseYourSide = Intent(this, ChooseYourSide::class.java)
                startActivity(chooseYourSide)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        })
    }

    private fun playerNamesAreSet(): Boolean {
        var namesAreSet: Boolean = true

        val playerOneName = mBinding.playerOneName.text.toString().trim()
        namesAreSet = isPlayerSet(playerOneName, Constants.KEY_PLAYER_1_NAME)

        if (AppUtils.isTwoPlayerMode(TAG, mSharedPreference)) {
            val playerTwoName = mBinding.playerTwoName.text.toString().trim()
            namesAreSet = namesAreSet && isPlayerSet(playerTwoName, Constants.KEY_PLAYER_2_NAME)
        }

        return namesAreSet
    }

    private fun isPlayerSet(playerName: String, playerKey: String): Boolean {
        if (!TextUtils.isEmpty(playerName)) {
            //TODO User should not be able to enter name "AI" since we are using that for our computer name.
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
        val prevPlayerOneName = mSharedPreference.getValue(
            String::class.java,
            Constants.KEY_PLAYER_1_NAME,
            getString(R.string.hint_player_1)
        )
        mSharedPreference.putValue(Constants.KEY_PLAYER_1_NAME, prevPlayerOneName)
        mBinding.playerOneName.setText(if (prevPlayerOneName == getString(R.string.hint_player_1)) {
            ""
        } else {
            prevPlayerOneName
        })

        if (AppUtils.isTwoPlayerMode(TAG, mSharedPreference)) {
            var prevPlayerTwoName: String? = mSharedPreference.getValue(
                String::class.java,
                Constants.KEY_PLAYER_2_NAME,
                getString(R.string.hint_player_2)
            )

            Log.i(TAG, "setDefaultPlayerNames() => prevPlayerTwoName: $prevPlayerTwoName")
            Log.i(TAG, "setDefaultPlayerNames() => Two Player Mode")

            mSharedPreference.putValue(Constants.KEY_PLAYER_2_NAME, prevPlayerTwoName)
            mBinding.playerTwoName.setText(if (prevPlayerTwoName == getString(R.string.hint_player_2)) {
                ""
            } else {
                prevPlayerTwoName
            })

        }
    }

    override fun onBackPressed() {
        val choosePlayMode = Intent(this, ChoosePlayMode::class.java)
        startActivity(choosePlayMode)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}