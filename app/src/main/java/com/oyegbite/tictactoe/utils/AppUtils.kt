package com.oyegbite.tictactoe.utils

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class AppUtils private constructor(){

    companion object {
        fun showToast(
            context: Context,
            message: String,
            toastLength: Int,
            prevToast: Toast?
        ): Toast {
            // cancel the previous prevToast message before setting a new one
            prevToast?.cancel()
            val newToast = Toast.makeText(
                context,
                message,
                toastLength
            )
            // Center align text in prevToast.
//            val view = newToast.view!!.findViewById<TextView>(R.id.message)
//            if (view != null) view.gravity = Gravity.CENTER
            // Set the prevToast view at the center of the device.
            newToast.setGravity(Gravity.CENTER, 0, 0)
            newToast.show()
            return newToast
        }

        fun isTwoPlayerMode(TAG: String, sharedPreference: SharedPreference): Boolean {
            val playMode = sharedPreference.getValue(
                String::class.java,
                Constants.KEY_PLAY_MODE,
                Constants.VALUE_PLAY_MODE_AI
            )
            Log.i(TAG, "playMode = $playMode")
            return playMode == Constants.VALUE_PLAY_MODE_FRIEND
        }

        fun displayDialogAt(dialog: AlertDialog, position: Int) {
            val window: Window? = dialog.window
            val wlp: WindowManager.LayoutParams? = window?.attributes
            wlp?.gravity = position
            window?.attributes = wlp
            window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
    }
}