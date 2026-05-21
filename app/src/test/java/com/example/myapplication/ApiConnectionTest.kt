package com.example.myapplication

import com.example.myapplication.data.api.RetrofitClient
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*

class ApiConnectionTest {
    @Test
    fun testApiConnection() = runBlocking {
        try {
            val response = RetrofitClient.instance.getAllEmployes()
            println("Status de l'API : ${response.status}")
            assertNotNull("La réponse ne devrait pas être nulle", response)
            assertTrue("L'API devrait renvoyer success ou error, mais répondre", 
                response.status == "success" || response.status == "error")
            println("Connexion à l'API PHP réussie !")
        } catch (e: Exception) {
            e.printStackTrace()
            fail("Impossible de contacter l'API PHP à l'adresse configurée : ${e.message}")
        }
    }
}