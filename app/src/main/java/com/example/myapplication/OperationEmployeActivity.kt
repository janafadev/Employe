package com.example.myapplication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.myapplication.data.dao.EmployeDao
import com.example.myapplication.data.model.EmployeeModel
import com.example.myapplication.databinding.ActivityOperationEmployeBinding
import com.example.myapplication.presentation.common.UiState
import com.example.myapplication.presentation.common.makeCall
import java.text.DecimalFormat

class OperationEmployeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOperationEmployeBinding
    private var isEditMode = false
    private var originalNumEmp = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOperationEmployeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Déterminer le mode
        isEditMode = intent.extras != null

        initListener()
        setupSalaireCalculation()

        if (isEditMode) {
            obtenirEmploye()
        }
    }

    private fun initListener() {

        // Toolbar
        binding.includeToolbar.toolbar.apply {

            setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            title = if (isEditMode) {
                "Modifier Employé"
            } else {
                "Nouvel Employé"
            }

            navigationIcon = AppCompatResources.getDrawable(
                this@OperationEmployeActivity,
                androidx.appcompat.R.drawable.abc_ic_ab_back_material
            )
        }

        // Masquer bouton +
        binding.includeToolbar.ibAccion.isVisible = false

        // Cacher code en insertion
        if (!isEditMode) {
            binding.tilCode.visibility = View.GONE
        }

        // Bouton enregistrer
        binding.btnEnregistrer.setOnClickListener {

            if (validateInputs()) {

                val employe = EmployeeModel(
                    numEmp = if (isEditMode) {
                        binding.etCode.text.toString().toInt()
                    } else {
                        0
                    },

                    nom = binding.etNom.text.toString().trim(),

                    nbr_jour = binding.etJours.text.toString().toInt(),

                    // Taux en pourcentage
                    taux_journalier = binding.etTaux.text.toString().toDouble()
                )

                enregistrer(employe)
            }
        }
    }

    /**
     * Calcul automatique
     */
    private fun setupSalaireCalculation() {

        val textWatcher = object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable?) {
                updateSalaireCalcul()
            }
        }

        binding.etJours.addTextChangedListener(textWatcher)
        binding.etTaux.addTextChangedListener(textWatcher)
    }

    /**
     * Mise à jour du calcul
     */
    private fun updateSalaireCalcul() {

        try {

            val jours =
                binding.etJours.text.toString().toIntOrNull() ?: 0

            val taux =
                binding.etTaux.text.toString().toDoubleOrNull() ?: 0.0

            // Calcul avec pourcentage
            val salaire = jours * (taux / 100)

            val df = DecimalFormat("#,###.00")

            if (jours > 0 && taux > 0) {

                binding.tvCalculSalaire.text =
                    "Salaire = $jours jours × ${df.format(taux)} %"

                binding.tvSalaireTotal.text =
                    "Total : ${df.format(salaire)} AR"

            } else {

                binding.tvCalculSalaire.text =
                    "Salaire = Nombre de jours × Pourcentage"

                binding.tvSalaireTotal.text =
                    "Total : 0 %"
            }

        } catch (e: Exception) {

            binding.tvCalculSalaire.text =
                "Salaire = Nombre de jours × Pourcentage"

            binding.tvSalaireTotal.text =
                "Total : 0 %"
        }
    }

    /**
     * Validation
     */
    private fun validateInputs(): Boolean {

        var isValid = true

        // Validation code
        if (isEditMode) {

            if (binding.etCode.text.isNullOrBlank()) {

                binding.tilCode.error = "Code requis"
                isValid = false

            } else {

                binding.tilCode.error = null
            }
        }

        // Validation nom
        if (binding.etNom.text.isNullOrBlank()) {

            binding.tilNom.error = "Nom requis"
            binding.etNom.requestFocus()
            isValid = false

        } else if (binding.etNom.text.toString().trim().length < 3) {

            binding.tilNom.error =
                "Le nom doit contenir au moins 3 caractères"

            binding.etNom.requestFocus()
            isValid = false

        } else {

            binding.tilNom.error = null
        }

        // Validation jours
        val joursStr =
            binding.etJours.text.toString().trim()

        val jours =
            joursStr.toIntOrNull()

        when {

            joursStr.isEmpty() -> {

                binding.tilJours.error =
                    "Nombre de jours requis"

                if (isValid) {
                    binding.etJours.requestFocus()
                }

                isValid = false
            }

            jours == null || jours <= 0 -> {

                binding.tilJours.error =
                    "Le nombre de jours doit être supérieur à 0"

                if (isValid) {
                    binding.etJours.requestFocus()
                }

                isValid = false
            }

            jours > 31 -> {

                binding.tilJours.error =
                    "Maximum 31 jours"

                if (isValid) {
                    binding.etJours.requestFocus()
                }

                isValid = false
            }

            else -> {
                binding.tilJours.error = null
            }
        }

        // Validation taux %
        val tauxStr =
            binding.etTaux.text.toString().trim()

        val taux =
            tauxStr.toDoubleOrNull()

        when {

            tauxStr.isEmpty() -> {

                binding.tilTaux.error =
                    "Pourcentage requis"

                if (isValid) {
                    binding.etTaux.requestFocus()
                }

                isValid = false
            }

            taux == null || taux <= 0 -> {

                binding.tilTaux.error =
                    "Le pourcentage doit être supérieur à 0"

                if (isValid) {
                    binding.etTaux.requestFocus()
                }

                isValid = false
            }

            taux > 100 -> {

                binding.tilTaux.error =
                    "Le pourcentage ne peut pas dépasser 100%"

                if (isValid) {
                    binding.etTaux.requestFocus()
                }

                isValid = false
            }

            else -> {
                binding.tilTaux.error = null
            }
        }

        return isValid
    }

    /**
     * Charger données employé
     */
    private fun obtenirEmploye() {

        originalNumEmp =
            intent.extras?.getInt("numEmp", 0) ?: 0

        val nom =
            intent.extras?.getString("nom") ?: ""

        val nbrJour =
            intent.extras?.getInt("nbr_jour", 0) ?: 0

        val tauxJournalier =
            intent.extras?.getDouble("taux_journalier", 0.0) ?: 0.0

        binding.etNom.setText(nom)
        binding.etCode.setText(originalNumEmp.toString())
        binding.etJours.setText(nbrJour.toString())
        binding.etTaux.setText(tauxJournalier.toString())

        // Empêcher modification du code
        binding.etCode.isEnabled = false

        updateSalaireCalcul()
    }

    /**
     * Enregistrer
     */
    private fun enregistrer(employe: EmployeeModel) =
        lifecycleScope.launch {

            binding.btnEnregistrer.isEnabled = false
            binding.progressBar.isVisible = true

            val call = if (isEditMode) {

                makeCall {
                    EmployeDao.update(employe)
                }

            } else {

                makeCall {
                    EmployeDao.insert(employe)
                }
            }

            binding.progressBar.isVisible = false
            binding.btnEnregistrer.isEnabled = true

            when (call) {

                is UiState.Error -> {

                    showAlert(
                        "❌ ERREUR",
                        call.message
                    )
                }

                is UiState.Success -> {

                    val message = if (isEditMode) {

                        "✅ Employé ${employe.nom} modifié"

                    } else {

                        "✅ Employé ${employe.nom} ajouté"
                    }

                    Toast.makeText(
                        this@OperationEmployeActivity,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()

                    MainActivity.existeChangement = true

                    if (isEditMode) {

                        finish()

                    } else {

                        cleanFields(binding.root)

                        binding.etNom.requestFocus()

                        updateSalaireCalcul()
                    }
                }

                is UiState.Loading -> {
                    binding.progressBar.isVisible = true
                }
            }
        }

    /**
     * Alert
     */
    private fun showAlert(
        titre: String,
        message: String
    ) {

        AlertDialog.Builder(this)
            .setTitle(titre)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    /**
     * Nettoyage champs
     */
    private fun cleanFields(view: View) {

        if (view is EditText) {

            view.setText("")
            view.error = null

        } else if (view is ViewGroup) {

            for (i in 0 until view.childCount) {

                cleanFields(view.getChildAt(i))
            }
        }
    }
}