package com.example.learnchessopenings

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.learnchessopenings.Models.user
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    private val db = DbHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val readDb = db.readableDatabase
        val cursor = readDb.query(
            user.User.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        // Checks if a user already exists and reroutes to the homepage if it's true
        if(cursor.count < 1) {
            setContentView(R.layout.login_screen)
        }
        else {
            val overview = Intent (applicationContext, OverviewActivity::class.java)
            overview.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(overview)
            finish()
        }
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }

    fun createUser(view: View) {
        val writeDb = db.writableDatabase
        val textInput = findViewById<View>(R.id.textInputLayout_username) as TextInputLayout
        val name = textInput.editText?.text
        if(name != null) {
            val values = ContentValues().apply {
                put(user.User.COLUMN_NAME_NAME, name.toString())
                put(user.User.COLUMN_NAME_STREAK, 0)
                put(user.User.COLUMN_NAME_XP, 0)
            }
            val newRowId = writeDb.insert(user.User.TABLE_NAME, null, values)

            if(newRowId != (-1).toLong()) {
                val overview = Intent (applicationContext, OverviewActivity::class.java)
                overview.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(overview)
                finish()
            }
        }
    }
}