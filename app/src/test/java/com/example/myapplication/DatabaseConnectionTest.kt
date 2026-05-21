package com.example.myapplication

import com.example.myapplication.data.dao.PostgresConnexion
import org.junit.Test
import org.junit.Assert.*

class DatabaseConnectionTest {
    @Test
    fun testConnection() {
        try {
            val connection = PostgresConnexion.getConnection()
            assertNotNull("La connexion ne devrait pas être nulle", connection)
            assertFalse("La connexion devrait être ouverte", connection.isClosed)
            println("Connexion réussie à PostgreSQL !")
            connection.close()
        } catch (e: Exception) {
            e.printStackTrace()
            fail("Erreur lors de la connexion à la base de données : ${e.message}")
        }
    }
}