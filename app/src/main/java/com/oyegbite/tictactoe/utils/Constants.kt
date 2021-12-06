package com.oyegbite.tictactoe.utils

import android.os.Environment
import com.oyegbite.tictactoe.MainActivity
import com.oyegbite.tictactoe.activities.*
import java.util.*
import kotlin.collections.HashMap

class Constants private constructor() {

    companion object { //
        // Identifier for shared preference.
        private const val PREFERENCE_FILE_KEY = "TicTacToe"
        private const val PACKAGE_NAME = "com.oyegbite.tictactoe"
        const val APP_IDENTIFIER = "$PACKAGE_NAME.$PREFERENCE_FILE_KEY"

        const val KEY_PLAY_MODE = "PLAY_MODE"
        const val VALUE_PLAY_MODE_AI = "PLAY_MODE_AI"
        const val VALUE_PLAY_MODE_FRIEND = "PLAY_MODE_FRIEND"

        const val KEY_PLAYER_1_NAME = "PLAYER_1_NAME"
        const val KEY_PLAYER_2_NAME = "PLAYER_2_NAME"

        const val KEY_PLAYER_1_SIDE = "PLAYER_1_SIDE"
        const val VALUE_PLAYER_1_SIDE_O = "0"
        const val VALUE_PLAYER_1_SIDE_X = "X"

        const val KEY_IS_PLAYER_ONE_TURN = "PLAYER_ONE_TURN"
        const val KEY_IS_GAME_OVER = "GAME_OVER"
        const val KEY_WINNERS_SIDE_OR_DRAW = "WINNERS_SIDE_OR_DRAW" // "Draw", "Player 1" or "Player 2"

        const val KEY_PLAYER_1_SCORE = "PLAYER_1_SCORE"
        const val KEY_PLAYER_2_SCORE = "PLAYER_2_SCORE"

        const val KEY_FIRST_PLAYER = "FIRST_PLAYER"

        const val KEY_GAME_MOVES = "GAME_MOVES"
        const val KEY_IS_GAME_SAVED = "IS_GAME_SAVED"

        const val KEY_SAVED_PREVIOUS_ACTIVITY = "SAVED_PREVIOUS_ACTIVITY"
        const val KEY_SAVED_CURRENT_ACTIVITY = "SAVED_CURRENT_ACTIVITY"

        const val KEY_VIBRATION_ACTIVE = "VIBRATION_ACTIVE"

        const val KEY_BOARD_DIMENSION = "BOARD_DIMENSION"

        const val KEY_DIFFICULTY_LEVEL = "DIFFICULTY_LEVEL"

        const val CONCATENATION_DELIMITER = "___" // For Database concatenation.

        const val APP_DEFAULT_DATE = "2021-02-02 00:00:00"

        // You can add the audio file extension that you want the user to be able to play here
        val audioFileExtension: Set<String> = HashSet(
            listOf("mp3", "mp4", "wav", "wma", "aac", "flac", "m4a")
        )

        // You can add the image file extension that you want the user to be able to download here
        val imageExtension: Set<String> = HashSet(listOf("png", "jpg", "jpeg"))

        val secondsAbbrev: Set<String> = HashSet(listOf("s", "sec", "secs", "second", "seconds"))

        val minuteAbbrev: Set<String> = HashSet(listOf("m", "min", "mins", "minute", "minutes"))

        val hoursAbbrev: Set<String> = HashSet(listOf("h", "hr", "hrs", "hour", "hours"))

        val daysAbbrev: Set<String> = HashSet(listOf("d", "dy", "dys", "day", "days"))

        val monthsAbbrev: Set<String> = HashSet(listOf("mo", "mos", "mth", "mths", "month", "months"))

        val yearsAbbrev: Set<String> = HashSet(listOf("y", "yr", "yrs", "year", "years"))
    }

    object Activity {
        const val MainActivity = 1
        const val ChoosePlayMode = 1
        const val EnterPlayerName = 2
        const val ChooseYourSide = 3
        const val Scene = 4
        const val Settings = 5
    }

    object Developer {
        const val FULL_NAME: String = "JOHN OYEGBITE"
        const val LINKEDIN_ACCOUNT: String = "https://www.linkedin.com/in/john-oyegbite/"
    }

    object DateTimeFormat {
        const val OYEGBITE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
        const val OYEGBITE_DATE_FORMAT_2 = "yyyy-MM-dd HH:mm"

        const val DATE_TIME_FORMAT = "dd/MM/yyyy hh:mm:ss"
        const val DATE_FORMAT = "dd/MM/yyyy"
        const val TIME_FORMAT = "hh:mm a"
        const val FULL_DATE_TIME_FORMAT = "EEEE, dd MMMM yyyy hh:mm a"
        const val FULL_DATE_FORMAT = "EEEE, MMM d, yyyy"
        const val SHORT_DATE_FORMAT = "E, MMM d, yyyy"
    }

    object Languages {
        // Remember to populate "langAbbrToFull" and "langFullToAbbr" in similar pattern
        // after adding new languages and their abbreviations.
        const val KEY_APP_LANG = "APP_LANG"
        private const val ENGLISH = "English"
        private const val ENGLISH_CODE = "en"
        val appLanguages = arrayOf(ENGLISH)
        val langCodeToTitle: HashMap<String, String> = hashMapOf(ENGLISH_CODE to ENGLISH)
        val langTitleToCode: HashMap<String, String> =
            hashMapOf(ENGLISH to ENGLISH_CODE, ENGLISH.lowercase() to ENGLISH_CODE)
    }
}