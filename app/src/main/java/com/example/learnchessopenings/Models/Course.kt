package com.example.learnchessopenings.Models

import android.provider.BaseColumns

object course {
    object Course : BaseColumns {
        const val TABLE_NAME = "course"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_ACTIVE = "active"
        const val COLUMN_NAME_BLACK = "black"
        const val COLUMN_NAME_DESCRIPTION = "description"
        const val COLUMN_NAME_IMAGE_ID = "image"
        const val COLUMN_NAME_VARIATIONS = "variations"
    }

    const val SQL_CREATE_ENTRIES = """
        CREATE TABLE IF NOT EXISTS ${Course.TABLE_NAME} (
        ${BaseColumns._ID} INTEGER PRIMARY KEY,
        ${Course.COLUMN_NAME_TITLE} TEXT,
        ${Course.COLUMN_NAME_ACTIVE} INTEGER,
        ${Course.COLUMN_NAME_BLACK} INTEGER,
        ${Course.COLUMN_NAME_DESCRIPTION} TEXT,
        ${Course.COLUMN_NAME_IMAGE_ID} INT,
        ${Course.COLUMN_NAME_VARIATIONS} BLOB
        )
    """

    const val SQL_DELETE_ENTRIES = """
        DROP TABLE IF EXISTS ${Course.TABLE_NAME}
    """
}