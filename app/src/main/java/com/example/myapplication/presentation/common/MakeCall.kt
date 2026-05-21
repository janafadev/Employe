package com.example.myapplication.presentation.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import android.util.Log

suspend fun <T> makeCall(call: suspend () -> T): UiState<T> {
    return withContext(Dispatchers.IO) {
        try {
            UiState.Success(call())
        } catch (e: UnknownHostException) {
            UiState.Error("Pas de connexion internet. Vérifiez votre Wi-Fi.", e)
        } catch (e: Throwable) { 
            // On capture Throwable pour éviter le crash si le pilote Postgres échoue violemment
            Log.e("DATABASE_ERROR", "Erreur critique interceptée", e)
            UiState.Error(e.message ?: "Erreur imprévue du pilote de base de données", e)
        }
    }
}
