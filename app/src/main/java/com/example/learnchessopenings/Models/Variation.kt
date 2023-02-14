package com.example.learnchessopenings.Models

import android.provider.BaseColumns
import com.example.learnchessopenings.DbHelper

object variation {
    object Variation : BaseColumns {
        const val TABLE_NAME = "variation"
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
        ${Variation.COLUMN_NAME_FEN} STRING,
        ${Variation.COLUMN_NAME_COMMENTS} STRING
        )
    """

    const val SQL_DELETE_ENTRIES = """
        DROP TABLE IF EXISTS ${Variation.TABLE_NAME}
    """

    fun getVariations(variationString: String, db: DbHelper): ArrayList<Map<String, Any>> {
        val variationIds = variationString.split(", ")
        val readDb = db.readableDatabase
        val data = ArrayList<Map<String, Any>>()

        for(variation in variationIds) {
            val cursor = readDb.query(
                Variation.TABLE_NAME,
                null,
                "${BaseColumns._ID} = ?",
                arrayOf(variation),
                null,
                null,
                null
            )
            with(cursor) {
                while(cursor.moveToNext()) {
                    data.add(mapOf(
                        BaseColumns._ID to getInt(0),
                        Variation.COLUMN_NAME_TITLE to getString(1),
                        Variation.COLUMN_NAME_STREAK to getInt(2),
                        Variation.COLUMN_NAME_LEARNED to getInt(3),
                        Variation.COLUMN_NAME_FEN to getString(4).split(",-,"),
                        Variation.COLUMN_NAME_COMMENTS to getString(5).split(",-,")
                    ))
                }
            }
            cursor.close()
        }

        return data
    }
}