package com.example.myapplication.data.model

data class ApiResponse<T>(
    val status: String,
    val message: String? = null,
    val data: T? = null,
    val statistiques: Statistiques? = null
)


data class Statistiques(
    val salaireTotal: Double,
    val salaireMin: Double,
    val salaireMax: Double,
    val salaireMoyen: Double,
    val totalEmployes: Int
)