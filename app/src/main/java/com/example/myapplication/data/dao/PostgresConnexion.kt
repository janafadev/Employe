package com.example.myapplication.data.dao

import java.sql.Connection
import java.sql.DriverManager
import java.util.Properties
import android.util.Log

object PostgresConnexion {
    private const val URL = "jdbc:postgresql://192.168.1.33:5432/androiddb"
    private const val USER = "postgres"
    private const val PASS = "azerty"

    fun getConnection(): Connection {
        try {
            Class.forName("org.postgresql.Driver")
            
            val props = Properties()
            props.setProperty("user", USER)
            props.setProperty("password", PASS)
            
            // Paramètres de base pour la connexion
            props.setProperty("ssl", "false")
            props.setProperty("sslmode", "disable")
            props.setProperty("connectTimeout", "10")
            props.setProperty("socketTimeout", "10")

            return DriverManager.getConnection(URL, props)
        } catch (e: Throwable) {
            Log.e("POSTGRES", "Erreur de connexion: ${e.message}", e)
            throw Exception(e.message ?: "Impossible de se connecter au serveur PostgreSQL")
        }
    }
}