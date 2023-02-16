package com.example.learnchessopenings

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learnchessopenings.Adapters.dashboardAdapter
import com.example.learnchessopenings.Models.course
import com.example.learnchessopenings.Models.variation
import com.example.learnchessopenings.ViewModels.dashboardViewModel
import androidx.cardview.widget.CardView
import java.text.SimpleDateFormat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment()  {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var db: DbHelper = DbHelper(context)

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
        // Inflate the layout for this fragment
        val homeView = inflater.inflate(R.layout.fragment_home, container, false)
        
        populateRecycler(homeView)

        writeDailyDate(homeView)

        return homeView
    }


    private fun populateRecycler(homeView: View) {
        val dashboardRecycler = homeView.findViewById<RecyclerView>(R.id.dashboardRecycler)
        dashboardRecycler.layoutManager = LinearLayoutManager(context)

        val data = ArrayList<dashboardViewModel>()
        val readDb = db.readableDatabase
        val cursor = readDb.query(
            course.Course.TABLE_NAME,
            null,
            "${course.Course.COLUMN_NAME_ACTIVE} = 1",
            null,
            null,
            null,
            null
        )
        with(cursor) {
            while(cursor.moveToNext()) {
                data.add(dashboardViewModel(getString(1), getString(4), getInt(5), variation.getVariations(getString(6), db)))
            }
        }
        cursor.close()

        if(data.size == 0) {
            // Sets a text, telling the user about getting courses, to visible if they're not yet
            // in any courses
            val noCoursesText = homeView.findViewById<TextView>(R.id.noCoursesText)
            noCoursesText.visibility = View.VISIBLE
        }

        dashboardRecycler.adapter = dashboardAdapter(data)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<CardView>(R.id.dailyPuzzleCard).setOnClickListener{
            val puzzle = Intent (context, PuzzleActivity::class.java)
            puzzle.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(puzzle)
        }

    }

    private fun writeDailyDate(homeView: View) {
        val monthName = SimpleDateFormat("MMMM").format(Calendar.getInstance().time)

        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val dayText = when(day) {
            1, 21, 31 -> "${day}st"
            2, 22 -> "${day}nd"
            3, 23 -> "${day}rd"
            else -> {
                "${day}th"
            }
        }

        val dailyDateText: TextView = homeView.findViewById(R.id.dailyPuzzleDate)
        dailyDateText.text = "${dayText} of ${monthName}"
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
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}