package com.example.learnchessopenings

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.example.learnchessopenings.Models.user

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Profile.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("UNCHECKED_CAST")
class Profile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var db = DbHelper(context)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        db = DbHelper(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val profileView = inflater.inflate(R.layout.fragment_profile, container, false)
        setData(profileView)
        // Inflate the layout for this fragment
        return profileView
    }

    private fun setData(profileView: View) {
        val userData = getUserData()

        // Profile card
        val usernameField = profileView.findViewById<TextView>(R.id.username)
        usernameField.text = userData["name"].toString()

        val userRankField = profileView.findViewById<TextView>(R.id.currentRank)
        val nextRankField = profileView.findViewById<TextView>(R.id.xpNextRank)
        val rankBar = profileView.findViewById<ProgressBar>(R.id.rankBar)
        val allRanks: Map<String, Int> = mapOf(
            "Newbie" to 0,
            "Beginner" to 50,
            "Novice" to 120,
            "Master" to 340
        )
        allRanks.forEach { (title, exp) ->
            if(userData["experience"] as Int >= exp) {
                userRankField.text = "Rank: $title"
            }
            else if(nextRankField.text == "Highest Rank Achieved") {
                nextRankField.text = "${(exp - userData["experience"] as Int).toString()} XP to $title"
                rankBar.max = exp
                rankBar.progress = userData["experience"] as Int
            }
        }

        val xpCountField = profileView.findViewById<TextView>(R.id.totalXpCount)
        xpCountField.text = userData["experience"].toString()

        val streakCountField = profileView.findViewById<TextView>(R.id.totalStreakCount)
        streakCountField.text = userData["streak"].toString()

        // Activity card
        val weeklyXpField = profileView.findViewById<TextView>(R.id.weekly_xp_count)
        var highest = 0
        var totalXp = 0
        for(day in userData["exp_by_day"] as Array<Int?>) {
            if(day != null) {
                if(day > highest) {
                    highest = day
                }
                totalXp += day
            }
        }
        weeklyXpField.text = totalXp.toString()

        var iteration = 0
        for(day in userData["exp_by_day"] as Array<Int?>) {
            setBar(iteration, day, highest, profileView)
            iteration += 1
        }
    }

    private fun setBar(weekday: Int, expDay: Int?, highestXp: Int, profileView: View) {
        val weekValues: Array<Array<Int>> = arrayOf(
            arrayOf(R.id.monday_bar, R.id.monday_count),
            arrayOf(R.id.tuesday_bar, R.id.tuesday_count),
            arrayOf(R.id.wednesday_bar, R.id.wednesday_count),
            arrayOf(R.id.thursday_bar, R.id.thursday_count),
            arrayOf(R.id.friday_bar, R.id.friday_count),
            arrayOf(R.id.saturday_bar, R.id.saturday_count),
            arrayOf(R.id.sunday_bar, R.id.sunday_count),
        )
        val bar = profileView.findViewById<ProgressBar>(weekValues[weekday][0])
        val txt = profileView.findViewById<TextView>(weekValues[weekday][1])

        bar.max = highestXp
        bar.progress = expDay ?: 0

        txt.text = expDay.toString()
    }

    fun getUserData(): Map<String, Any> {
        val dbCon = db.readableDatabase
        val cursor = dbCon.query(
            user.User.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )
        cursor.moveToNext()
        val data: Map<String, Any> = mapOf<String, Any>(
            "name" to cursor.getString(1),
            "streak" to cursor.getInt(2),
            "streak_day" to cursor.getString(3),
            "experience" to cursor.getInt(4),
            "exp_by_day" to arrayOf<Int?>(
                cursor.getInt(5),
                cursor.getInt(6),
                cursor.getInt(7),
                cursor.getInt(8),
                cursor.getInt(9),
                cursor.getInt(10),
                cursor.getInt(11),
            )
        )
        cursor.close()
        return data
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Profile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Profile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}