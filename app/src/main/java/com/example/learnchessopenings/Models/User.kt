package com.example.learnchessopenings.Models

import android.provider.BaseColumns

object user {
    object User : BaseColumns {
        const val TABLE_NAME = "user"
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_STREAK = "streak"
        const val COLUMN_NAME_LAST_DAY_STREAK = "streak_day"
        const val COLUMN_NAME_XP = "experience"
        const val COLUMN_NAME_XPMO = "exp_monday"
        const val COLUMN_NAME_XPTU = "exp_tuesday"
        const val COLUMN_NAME_XPWE = "exp_wednesday"
        const val COLUMN_NAME_XPTH = "exp_thursday"
        const val COLUMN_NAME_XPFR = "exp_friday"
        const val COLUMN_NAME_XPSA = "exp_saturday"
        const val COLUMN_NAME_XPSU = "exp_sunday"
    }

    const val SQL_CREATE_ENTRIES = """
        CREATE TABLE IF NOT EXISTS ${User.TABLE_NAME} (
        ${BaseColumns._ID} INTEGER PRIMARY KEY,
        ${User.COLUMN_NAME_NAME} TEXT,
        ${User.COLUMN_NAME_STREAK} INTEGER,
        ${User.COLUMN_NAME_LAST_DAY_STREAK} TEXT,
        ${User.COLUMN_NAME_XP} INTEGER,
        ${User.COLUMN_NAME_XPMO} INTEGER,
        ${User.COLUMN_NAME_XPTU} INTEGER,
        ${User.COLUMN_NAME_XPWE} INTEGER,
        ${User.COLUMN_NAME_XPTH} INTEGER,
        ${User.COLUMN_NAME_XPFR} INTEGER,
        ${User.COLUMN_NAME_XPSA} INTEGER,
        ${User.COLUMN_NAME_XPSU} INTEGER
        )
    """

    const val SQL_DELETE_ENTRIES = """
        DROP TABLE IF EXISTS ${User.TABLE_NAME}
    """
}