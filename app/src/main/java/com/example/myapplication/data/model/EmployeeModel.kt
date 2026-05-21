package com.example.myapplication.data.model

import com.google.gson.annotations.SerializedName


data class EmployeeModel(
    @SerializedName("numEmp")
    val numEmp: Int? = null,      // null = nouvel employé, non null = existant

    @SerializedName("nom")
    val nom: String,

    @SerializedName("nbr_jour")
    val nbr_jour: Int,

    @SerializedName("taux_journalier")
    val taux_journalier: Double
) {
    // Propriété calculée - non envoyée au serveur
    val salaire: Double
        get() = nbr_jour * taux_journalier

    // Validation des données
    fun isValid(): Boolean {
        return nom.isNotBlank() && nbr_jour > 0 && taux_journalier > 0
    }

    // Pour affichage dans les logs
    override fun toString(): String {
        return "EmployeeModel(numEmp=$numEmp, nom='$nom', nbr_jour=$nbr_jour, taux=$taux_journalier, salaire=$salaire)"
    }
}


data class AddResponse(
    val status: String,
    val message: String,
    val numEmp: Int? = null
)