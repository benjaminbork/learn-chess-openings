package com.example.learnchessopenings.ViewModels

data class dashboardViewModel(
    val header: String,
    val description: String,
    val imageId: Int,
    val variations: ArrayList<Map<String, Any>>
) {}