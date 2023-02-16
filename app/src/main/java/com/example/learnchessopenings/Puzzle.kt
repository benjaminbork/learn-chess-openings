package com.example.learnchessopenings

data class Puzzle(
    val id: String,
    val initialPly: Int,
    val plays: Int,
    val rating: Int,
    val solution: List<String>,
    val themes: List<String>
)