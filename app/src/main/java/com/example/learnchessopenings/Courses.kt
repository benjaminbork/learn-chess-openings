package com.example.learnchessopenings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learnchessopenings.Adapters.courseAdapter
import com.example.learnchessopenings.Models.course
import com.example.learnchessopenings.Models.variation
import com.example.learnchessopenings.ViewModels.courseViewModel
import com.example.learnchessopenings.ViewModels.dashboardViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Courses.newInstance] factory method to
 * create an instance of this fragment.
 */
class Courses : Fragment() {
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
        val courseView = inflater.inflate(R.layout.fragment_courses, container, false)

        populateRecycler(courseView)

        return courseView
    }

    private fun populateRecycler(courseView: View) {
        val courseRecycler = courseView.findViewById<RecyclerView>(R.id.CourseRecycler)
        courseRecycler.layoutManager = GridLayoutManager(context, 2)

        val data = ArrayList<courseViewModel>()
        val readDb = db.readableDatabase
        val cursor = readDb.query(
            course.Course.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )
        with(cursor) {
            while(cursor.moveToNext()) {
                data.add(courseViewModel(getInt(0), getString(1), getInt(5)))
            }
        }
        cursor.close()

        courseRecycler.adapter = courseAdapter(data)
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
         * @return A new instance of fragment Courses.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Courses().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}