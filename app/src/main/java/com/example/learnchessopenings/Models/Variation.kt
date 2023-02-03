package com.example.learnchessopenings.Models

import android.provider.BaseColumns

object variation {
    object Variation : BaseColumns {
        const val TABLE_NAME = "course"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_STREAK = "streak"
        const val COLUMN_NAME_LEARNED = "learned"
        const val COLUMN_NAME_FEN = "fen"
        const val COLUMN_NAME_COMMENTS = "comments"
    }

    const val SQL_CREATE_ENTRIES = """
        CREATE TABLE IF NOT EXISTS ${Variation.TABLE_NAME} (
        ${BaseColumns._ID} INTEGER PRIMARY KEY,
        ${Variation.COLUMN_NAME_TITLE} TEXT,
        ${Variation.COLUMN_NAME_STREAK} INTEGER,
        ${Variation.COLUMN_NAME_LEARNED} INTEGER,
        ${Variation.COLUMN_NAME_FEN} BLOB,
        ${Variation.COLUMN_NAME_COMMENTS} BLOB
        )
    """

    const val SQL_DELETE_ENTRIES = """
        DROP TABLE IF EXISTS ${Variation.TABLE_NAME}
    """
}