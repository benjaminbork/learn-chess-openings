package com.example.learnchessopenings

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.learnchessopenings.databinding.ChessBinding


private const val TAG = "ChessActivity"
class ChessActivity : AppCompatActivity() {
    var chessModel = ChessModel()
    private lateinit var binding : ChessBinding
    private lateinit var mainAppBar : Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG,chessModel.toString())
    }

}