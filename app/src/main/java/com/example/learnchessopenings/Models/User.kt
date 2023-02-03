package com.example.learnchessopenings.Models

import android.provider.BaseColumns

object user {
    object User : BaseColumns {
        const val TABLE_NAME = "user"
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_STREAK = "streak"
        const val COLUMN_NAME_XP = "experience"
    }

    const val SQL_CREATE_ENTRIES = """
        CREATE TABLE IF NOT EXISTS ${User.TABLE_NAME} (
        ${BaseColumns._ID} INTEGER PRIMARY KEY,
        ${User.COLUMN_NAME_NAME} TEXT,
        ${User.COLUMN_NAME_STREAK} INTEGER,
        ${User.COLUMN_NAME_XP} INTEGER
        )
    """

    const val SQL_DELETE_ENTRIES = """
        DROP TABLE IF EXISTS ${User.TABLE_NAME}
    """
}