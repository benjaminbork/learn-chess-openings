package com.example.learnchessopenings.ViewModels

data class dashboardViewModel(
    val id: Int,
    val header: String,
    val description: String,
    val imageId: Int,
    val variations: ArrayList<Map<String, Any>>
)