package com.example.learnchessopenings

import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {
    @GET("api/puzzle/daily")
    fun getData():Call<DailyPuzzleData>
}