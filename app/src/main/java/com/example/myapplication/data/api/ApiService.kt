package com.example.myapplication.data.api

import com.example.myapplication.data.model.AddResponse
import com.example.myapplication.data.model.EmployeeModel
import com.example.myapplication.data.model.ApiResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // 🔹 Récupérer tous les employés
    @GET("api.php")
    suspend fun getAllEmployes(
        @Query("action") action: String = "get_all"
    ): Response<ApiResponse<List<EmployeeModel>>>

    // 🔹 Récupérer un employé par ID
    @GET("api.php")
    suspend fun getEmployeById(
        @Query("action") action: String = "get_one",
        @Query("id") id: Int
    ): Response<ApiResponse<EmployeeModel>>

    // 🔹 Ajouter un employé
    // L’API renvoie {status, message, numEmp}
    @POST("api.php?action=add")
    suspend fun addEmploye(
        @Body employe: EmployeeModel
    ): Response<AddResponse>

    // 🔹 Mettre à jour un employé
    // L’API renvoie {status, message}
    @POST("api.php?action=update")
    suspend fun updateEmploye(
        @Body employe: EmployeeModel
    ): Response<ApiResponse<Unit>>

    // 🔹 Supprimer un employé
    // L’API attend {"numEmp": 5} et renvoie {status, message}
    @POST("api.php?action=delete")
    suspend fun deleteEmploye(
        @Body request: Map<String, Int>
    ): Response<ApiResponse<Unit>>

    // 🔹 Récupérer les statistiques
    // L’API renvoie {status, statistiques: {...}}
    @GET("api.php")
    suspend fun getStats(
        @Query("action") action: String = "stats"
    ): Response<ApiResponse<Unit>>
}
