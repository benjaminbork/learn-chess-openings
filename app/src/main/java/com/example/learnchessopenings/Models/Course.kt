package com.example.learnchessopenings.Models

import android.content.ContentValues
import android.provider.BaseColumns
import com.example.learnchessopenings.DbHelper

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
        ${Course.COLUMN_NAME_VARIATIONS} TEXT
        )
    """

    const val SQL_DELETE_ENTRIES = """
        DROP TABLE IF EXISTS ${Course.TABLE_NAME}
    """

    fun setActive(db: DbHelper, id: Int): Int {
        val writeDb = db.writableDatabase

        val value = ContentValues().apply {
            put(Course.COLUMN_NAME_ACTIVE, 1)
        }

        return writeDb.update(
            course.Course.TABLE_NAME,
            value,
            "${BaseColumns._ID} = $id",
            null
        )
    }

    fun getCourse(id: Int, db: DbHelper): Map<String, Any> {
        val readDb = db.readableDatabase
        val data = mutableMapOf<String, Any>()

        val cursor = readDb.query(
            course.Course.TABLE_NAME,
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
                data[Course.COLUMN_NAME_TITLE] = getString(1)
                data[Course.COLUMN_NAME_ACTIVE] = getInt(2)
                data[Course.COLUMN_NAME_BLACK] = getInt(3)
                data[Course.COLUMN_NAME_DESCRIPTION] = getString(4)
                data[Course.COLUMN_NAME_IMAGE_ID] = getInt(5)
                data[Course.COLUMN_NAME_VARIATIONS] = getString(6)
            }
        }
        cursor.close()

        return data.toMap()
    }
}