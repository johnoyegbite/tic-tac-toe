package com.oyegbite.tictactoe.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.util.Pair
import com.google.gson.Gson
import java.lang.Exception

class SharedPreference(context: Context) {

    private val mSharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.APP_IDENTIFIER, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = mSharedPreferences.edit()
    private val gson: Gson = Gson()

    companion object {
        private val TAG: String = SharedPreference::class.java.simpleName
        private const val PUT_SUCCESS = 0
        private const val PUT_FAIL = -1
    }

    /**
     * GENERIC FUNCTION TO STORE ANYTHING TO SHARED PREFS AS LONG AS IT IS SERIALIZABLE BY THE GSON CONVERTER
     * @param key -  THE STRING KEY VALUE OF THE OBJECT TO BE STORED
     * @param value - THE VALUE TO BE STORED IN THE SHARED PREFS
     * @param <T> - THE TYPE BEING STORED
     * @return- RESULT, 0- SUCCESSFUL, 1-UNSUCCESSFUL
    </T> */
    fun <T> putValue(key: String, value: T): Int {
        when(value) {
            is String -> {
                editor.putString(key, value)
            }
            is Int -> {
                editor.putInt(key, value)
            }
            is Float -> {
                editor.putFloat(key, value)
            }
            is Long -> {
                editor.putLong(key, value)
            }
            is Boolean -> {
                editor.putBoolean(key, value)
            }
            else -> {
                try {
                    editor.putString(key, gson.toJson(value))
                } catch (e: Exception) {
                    try {
                        editor.putString(key, value.toString())
                    } catch (e: Exception) {
                        return PUT_FAIL
                    }
                }
            }
        }

        Log.i(TAG, "saved => key: $key, value: '$value'")

        editor.commit()
        return PUT_SUCCESS
    }

    fun <T> putValues(vararg keyValuePairs: Pair<String, T>): Int {
        var res = 0
        for (keyValue in keyValuePairs) {
            res += putValue(keyValue.first!!, keyValue.second!!)
        }
        return if (res < 0) PUT_FAIL else PUT_SUCCESS
    }

    fun <T> getValue(keyClassType: Class<T>, key: String): T? {
        return when (keyClassType) {
            String::class.java -> {
                val value = mSharedPreferences.getString(key, "") as? T
                value
            }
            Int::class.java -> mSharedPreferences.getInt(key, 0) as T
            Float::class.java -> mSharedPreferences.getFloat(key, 0f) as T
            Long::class.java -> mSharedPreferences.getLong(key, 0) as T
            Boolean::class.java -> mSharedPreferences.getBoolean(key, false) as T
            else -> {
                try {
                    val value = mSharedPreferences.getString(key, "")
                    if (value!!.isEmpty()) {
                        null
                    } else {
                        gson.fromJson(value, keyClassType)
                    }
                } catch (e: Exception) {
                    Log.i(
                        Constants.Developer.FULL_NAME,
                        "getValue: Exception occurred in POJO Xform " + e.message
                    )
                    null
                }
            }
        }
    }

    fun <T> getValue(keyClassType: Class<T>, key: String, defaultValue: T): T? {
        return when (keyClassType) {
            String::class.java -> {
                val value = mSharedPreferences.getString(key, defaultValue as String) as T?
//                Log.i(TAG, "getValue() => key: $key, value: '$value', default: '$defaultValue'")
                value
            }
            Int::class.java -> {
                val value = mSharedPreferences.getInt(key, defaultValue as Int) as T
//                Log.i(TAG, "getValue() => key: $key, value: $value, default: $defaultValue")
                value
            }
            Float::class.java -> mSharedPreferences.getFloat(key, defaultValue as Float) as T
            Long::class.java -> mSharedPreferences.getLong(key, defaultValue as Long) as T
            Boolean::class.java -> mSharedPreferences.getBoolean(key, defaultValue as Boolean) as T
            else -> {
                try {
                    val value = mSharedPreferences.getString(key, "")
                    if (value!!.isEmpty()) {
                        null
                    } else {
                        gson.fromJson(value, keyClassType)
                    }
                } catch (e: Exception) {
                    Log.i(
                        TAG,
                        "getValue: Exception occurred in POJO Xform " + e.message
                    )
                    null
                }
            }
        }
    }

}