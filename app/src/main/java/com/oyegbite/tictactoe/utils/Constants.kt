package com.oyegbite.tictactoe.utils

import android.os.Environment
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

        const val KEY_SAVED_ACTIVITY = "SAVED_ACTIVITY"

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

    object Developer {
        const val FULL_NAME: String = "JOHN OYEGBITE"
    }

    object DateTimeFormat {
        const val OYEGBITE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
        const val OYEGBITE_DATE_FORMAT_2 = "yyyy-MM-dd HH:mm"
        const val MY_DATE_FORMAT = "dd/MM/yyyy hh:mm:ss"
        const val DATE_STAMP_FORMAT = "dd/MM/yyyy"
        const val FULL_DATE_FORMAT = "EEEE, dd MMMM yyyy hh:mm a"
        const val SHORT_DATE_FORMAT = "EEEE, MMM d, yyyy"
        const val DATE_FORMAT = "E, MMM d, yyyy"
        const val APP_DEFAULT_DATE = "2021-05-28 00:00:00"
        const val LAST_SYNC_DATE_TIME_FORMAT = "dd/MMMM/yyyy - hh:mm a"
        const val LAST_SYNC_DATE_FORMAT = "dd/MMMM/yyyy"
        const val TIME_FORMAT = "hh:mm a"
        const val SYNC_SUMMARY_DATE_FORMAT = "dd/MM/yyyy"
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

    // Database References ###############################################
    // All the fields in the Database classes are CONSTANTS and SHOULD NOT be modified.
    object SyncUnitTable {
        const val TABLE_NAME = "SyncUnit"

        object ColumnNames {
            const val ID = "id" // PK
            const val STAFF_ID = "staff_id" // PK
            const val TABLE_NAME = "table_name"
            const val TABLE_ENDPOINT_TYPE = "endpoint_type" // Download or Upload
            const val SYNC_STATE = "sync_state"
            const val MESSAGE = "message"
            const val LAST_SYNC_TIME = "last_sync_time"
        }
    }
    // END Database Reference ############################################
}