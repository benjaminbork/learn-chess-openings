package com.example.learnchessopenings

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api : ApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl("https://lichess.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }
}