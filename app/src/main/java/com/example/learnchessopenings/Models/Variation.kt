package com.example.learnchessopenings.Models

import android.content.ContentValues
import android.provider.BaseColumns
import com.example.learnchessopenings.DbHelper
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object variation {
    object Variation : BaseColumns {
        const val TABLE_NAME = "variation"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_STREAK = "streak"
        const val COLUMN_NAME_LEARNED = "learned"
        const val COLUMN_NAME_LAST_LEARNED = "last_date"
        const val COLUMN_NAME_FEN = "fen"
        const val COLUMN_NAME_COMMENTS = "comments"
    }

    const val SQL_CREATE_ENTRIES = """
        CREATE TABLE IF NOT EXISTS ${Variation.TABLE_NAME} (
        ${BaseColumns._ID} INTEGER PRIMARY KEY,
        ${Variation.COLUMN_NAME_TITLE} TEXT,
        ${Variation.COLUMN_NAME_STREAK} INTEGER,
        ${Variation.COLUMN_NAME_LEARNED} INTEGER,
        ${Variation.COLUMN_NAME_LAST_LEARNED} STRING,
        ${Variation.COLUMN_NAME_FEN} STRING,
        ${Variation.COLUMN_NAME_COMMENTS} STRING
        )
    """

    const val SQL_DELETE_ENTRIES = """
        DROP TABLE IF EXISTS ${Variation.TABLE_NAME}
    """

    fun setDate(id: Int, db: DbHelper, date: LocalDate): Int {
        val writeDb = db.writableDatabase

        val value = ContentValues().apply {
            put(Variation.COLUMN_NAME_LAST_LEARNED, date.toString())
        }

        return writeDb.update(
            Variation.TABLE_NAME,
            value,
            "${BaseColumns._ID} = ?",
            arrayOf(id.toString())
        )
    }

    private fun getDate(date: String?): LocalDate {
        return if(date != null) {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        } else {
            LocalDate.parse("01-01-1970", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        }
    }

    fun getVariation(id: Int, db: DbHelper): Map<String, Any> {
        val readDb = db.readableDatabase
        val data = mutableMapOf<String, Any>()

        val cursor = readDb.query(
            Variation.TABLE_NAME,
            null,
            "${BaseColumns._ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        with(cursor) {
            while(cursor.moveToNext()) {
                data[BaseColumns._ID] = getInt(0)
                data[Variation.COLUMN_NAME_TITLE] = getString(1)
                data[Variation.COLUMN_NAME_STREAK] = getInt(2)
                data[Variation.COLUMN_NAME_LEARNED] = getInt(3)
                data[Variation.COLUMN_NAME_LAST_LEARNED] = getDate(getString(4))
                data[Variation.COLUMN_NAME_FEN] = getString(5).split(",-,")
                data[Variation.COLUMN_NAME_COMMENTS] = getString(6).split(",-,")
            }
        }
        cursor.close()

        return data.toMap()
    }

    fun getVariations(variationString: String, db: DbHelper): ArrayList<Map<String, Any>> {
        val variationIds = variationString.split(", ")
        val data = ArrayList<Map<String, Any>>()

        for(variation in variationIds) {
            data.add(getVariation(variation.toInt(), db))
        }

        return data
    }
}