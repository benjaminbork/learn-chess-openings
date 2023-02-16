package com.example.learnchessopenings

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {
    @GET("api/puzzle/daily")
    suspend fun getData():Response<DailyPuzzleData>
}