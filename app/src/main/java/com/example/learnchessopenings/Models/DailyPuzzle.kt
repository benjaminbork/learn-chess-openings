package com.example.learnchessopenings.Models

import android.content.ContentValues
import android.provider.BaseColumns
import com.example.learnchessopenings.DbHelper
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object dailyPuzzle {
    object DailyPuzzle : BaseColumns {
        const val TABLE_NAME = "dailyPuzzle"
        const val COLUMN_NAME_LAST_PLAYED = "last_date"
    }

    const val SQL_CREATE_ENTRIES = """
        CREATE TABLE IF NOT EXISTS ${DailyPuzzle.TABLE_NAME} (
        ${BaseColumns._ID} INTEGER PRIMARY KEY,
        ${DailyPuzzle.COLUMN_NAME_LAST_PLAYED} TEXT
        )
    """

    const val SQL_DELETE_ENTRIES = """
        DROP TABLE IF EXISTS ${DailyPuzzle.TABLE_NAME}
    """

    fun setDate(db: DbHelper, date: LocalDate): Int {
        val writeDb = db.writableDatabase

        val value = ContentValues().apply {
            put(DailyPuzzle.COLUMN_NAME_LAST_PLAYED, date.toString())
        }

        return writeDb.update(
            DailyPuzzle.TABLE_NAME,
            value,
            "${BaseColumns._ID} = 1",
            null
        )
    }

    private fun getDateFormat(date: String?): LocalDate {
        return if(date != null) {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } else {
            LocalDate.parse("1970-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        }
    }

    fun getDailyDate(id: Int, db: DbHelper): LocalDate {
        val readDb = db.readableDatabase
        var date: LocalDate = LocalDate.parse("1970-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val cursor = readDb.query(
            DailyPuzzle.TABLE_NAME,
            null,
            "${BaseColumns._ID} = 1",
            null,
            null,
            null,
            null
        )
        with(cursor) {
            while(cursor.moveToNext()) {
                date = getDateFormat(getString(1))
            }
        }
        cursor.close()

        return date
    }
}