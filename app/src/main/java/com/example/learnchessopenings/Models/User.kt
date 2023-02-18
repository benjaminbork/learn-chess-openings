package com.example.learnchessopenings.Models

import android.content.ContentValues
import android.provider.BaseColumns
import com.example.learnchessopenings.DbHelper
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

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
        const val COLUMN_NAME_WEEK = "week"
    }

    const val SQL_CREATE_ENTRIES = """
        CREATE TABLE IF NOT EXISTS ${User.TABLE_NAME} (
        ${BaseColumns._ID} INTEGER PRIMARY KEY,
        ${User.COLUMN_NAME_NAME} TEXT,
        ${User.COLUMN_NAME_STREAK} INTEGER,
        ${User.COLUMN_NAME_LAST_DAY_STREAK} TEXT,
        ${User.COLUMN_NAME_XP} INTEGER,
        ${User.COLUMN_NAME_WEEK} INTEGER,
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

    fun addExp(exp: Int, db: DbHelper) {
        val weekday = when(LocalDate.now().dayOfWeek.toString()) {
            "MONDAY" -> User.COLUMN_NAME_XPMO
            "TUESDAY" -> User.COLUMN_NAME_XPTU
            "WEDNESDAY" -> User.COLUMN_NAME_XPWE
            "THURSDAY" -> User.COLUMN_NAME_XPTH
            "FRIDAY" -> User.COLUMN_NAME_XPFR
            "SATURDAY" -> User.COLUMN_NAME_XPSA
            "SUNDAY" -> User.COLUMN_NAME_XPSU
            else -> {
                User.COLUMN_NAME_XPMO
            }
        }

        val readDb = db.readableDatabase
        val cursor = readDb.query(
            User.TABLE_NAME,
            arrayOf(User.COLUMN_NAME_XP, weekday),
            "${BaseColumns._ID} = 1",
            null,
            null,
            null,
            null
        )
        var currentXp = 0
        var dailyXp = 0
        with(cursor) {
            while(moveToNext()) {
                currentXp = getInt(0)
                dailyXp = if(getInt(1) != null) getInt(1) else 0
            }
        }

        val values = ContentValues().apply {
            put(User.COLUMN_NAME_XP, currentXp + exp)
            put(weekday, dailyXp + exp)
        }

        val writeDb = db.writableDatabase
        writeDb.update(
            User.TABLE_NAME,
            values,
            "${BaseColumns._ID} = 1",
            null
        )

        if(checkStreak(db) != 0.toLong()) {
            increaseStreak(db)
        }
    }

    fun checkStreak(db: DbHelper): Long {
        // Check streak
        val readDb = db.readableDatabase
        val cursor = readDb.query(
            User.TABLE_NAME,
            arrayOf(User.COLUMN_NAME_LAST_DAY_STREAK),
            "${BaseColumns._ID} = 1",
            null,
            null,
            null,
            null
        )

        var lastDay: LocalDate = LocalDate.now()

        with(cursor) {
            while(cursor.moveToNext()) {
                lastDay = LocalDate.parse(getString(0), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }
        }

        if(LocalDate.now().toEpochDay() - lastDay.toEpochDay() > 1) {
            resetStreak(db)
        }

        return LocalDate.now().toEpochDay() - lastDay.toEpochDay()
    }

    private fun increaseStreak(db: DbHelper): Int {
        val readDb = db.readableDatabase
        val cursor = readDb.query(
            User.TABLE_NAME,
            arrayOf(User.COLUMN_NAME_STREAK),
            "${BaseColumns._ID} = 1",
            null,
            null,
            null,
            null
        )

        var currentStreak = 0

        with(cursor) {
            while(moveToNext()) {
                currentStreak = getInt(0)
            }
        }

        val value = ContentValues().apply {
            put(User.COLUMN_NAME_STREAK, currentStreak + 1)
            put(User.COLUMN_NAME_LAST_DAY_STREAK, LocalDate.now().toString())
        }

        val writeDb = db.writableDatabase
        return writeDb.update(
            User.TABLE_NAME,
            value,
            "${BaseColumns._ID} = 1",
            null
        )
    }

    private fun resetStreak(db: DbHelper): Int {
        val writeDb = db.writableDatabase
        val value = ContentValues().apply {
            put(User.COLUMN_NAME_STREAK, 0)
        }

        return writeDb.update(
            User.TABLE_NAME,
            value,
            "${BaseColumns._ID} = 1",
            null
        )
    }

    fun checkWeek(db: DbHelper) {
        val readDb = db.readableDatabase
        var savedWeek = 0

        val cursor = readDb.query(
            User.TABLE_NAME,
            arrayOf(User.COLUMN_NAME_WEEK),
            "${BaseColumns._ID} = 1",
            null,
            null,
            null,
            null
        )
        while(cursor.moveToNext()) {
            savedWeek = cursor.getInt(0)
        }

        cursor.close()

        val currentWeek = Calendar.getInstance(TimeZone.getTimeZone("UTC")).get(Calendar.WEEK_OF_YEAR)
        if(savedWeek != currentWeek) {
            eraseWeek(db, currentWeek)
        }
    }

    private fun eraseWeek(db: DbHelper, currentWeek: Int): Int {
        val writeDb = db.writableDatabase
        val values = ContentValues().apply {
            put(User.COLUMN_NAME_WEEK, currentWeek)
            put(User.COLUMN_NAME_XPMO, 0)
            put(User.COLUMN_NAME_XPTU, 0)
            put(User.COLUMN_NAME_XPWE, 0)
            put(User.COLUMN_NAME_XPTH, 0)
            put(User.COLUMN_NAME_XPFR, 0)
            put(User.COLUMN_NAME_XPSA, 0)
            put(User.COLUMN_NAME_XPSU, 0)
        }

        return writeDb.update(
            User.TABLE_NAME,
            values,
            "${BaseColumns._ID} = 1",
            null
        )
    }
}