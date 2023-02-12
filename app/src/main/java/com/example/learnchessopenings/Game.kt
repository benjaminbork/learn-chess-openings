package com.example.learnchessopenings

data class Game(
    val clock: String,
    val id: String,
    val perf: Perf,
    val pgn: String,
    val players: List<Player>,
    val rated: Boolean
)