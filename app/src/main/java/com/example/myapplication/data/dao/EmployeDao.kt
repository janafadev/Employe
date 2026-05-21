package com.example.myapplication.data.dao

import com.example.myapplication.data.api.RetrofitClient
import com.example.myapplication.data.model.EmployeeModel
import com.example.myapplication.data.model.ApiResponse
import retrofit2.Response

object EmployeDao {

    suspend fun listar(search: String = ""): List<EmployeeModel> {
        val response = RetrofitClient.instance.getAllEmployes()
        val body = response.body()

        val list = if (body?.status == "success") body.data ?: emptyList() else emptyList()

        // 🔎 Filtrage côté Android
        return if (search.isNotEmpty()) {
            list.filter { it.nom.contains(search, ignoreCase = true) }
        } else {
            list
        }
    }

    suspend fun insert(employe: EmployeeModel): Boolean {
        val response = RetrofitClient.instance.addEmploye(employe)
        return response.body()?.status == "success"
    }

    suspend fun update(employe: EmployeeModel): Boolean {
        val response = RetrofitClient.instance.updateEmploye(employe)
        return response.body()?.status == "success"
    }

    suspend fun delete(numEmp: Int?): Boolean {
        if (numEmp == null) return false
        val response = RetrofitClient.instance.deleteEmploye(mapOf("numEmp" to numEmp))
        return response.body()?.status == "success"
    }


    suspend fun getEmployeById(id: Int): EmployeeModel? {
        val response = RetrofitClient.instance.getEmployeById(id = id)
        val body = response.body()
        return if (body?.status == "success") body.data else null
    }
}
