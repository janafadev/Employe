package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.myapplication.data.dao.EmployeDao
import com.example.myapplication.data.model.EmployeeModel
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.presentation.adapter.EmployerAdapter
import com.example.myapplication.presentation.common.UiState
import com.example.myapplication.presentation.common.makeCall
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

class MainActivity : AppCompatActivity(), EmployerAdapter.IOnClickListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            initListener()
            lireEmploye("") // Charge tous les employés
        } catch (e: Exception) {
            Log.e("DEBUG_APP", "Erreur fatale au démarrage", e)
            Toast.makeText(this, "Erreur au démarrage: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!existeChangement) return
        existeChangement = false
        lireEmploye("") // Recharge tous les employés
    }

    private fun initListener() {
        // Bouton ajouter dans la toolbar
        binding.includeToolbar.ibAccion.setOnClickListener {
            startActivity(android.content.Intent(this, OperationEmployeActivity::class.java))
        }

        // Configuration du RecyclerView
        binding.rvEmployes.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = EmployerAdapter(this@MainActivity)
        }

        // Recherche en temps réel avec debounce
        binding.etRechercher.addTextChangedListener(object : android.text.TextWatcher {
            private var searchJob: Job? = null

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(300)
                    val query = s?.toString()?.trim() ?: ""
                    lireEmploye(query)
                }
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        // Recherche avec touche "Entrée" du clavier
        binding.etRechercher.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.etRechercher.text?.toString()?.trim() ?: ""
                lireEmploye(query)
                val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.hideSoftInputFromWindow(binding.etRechercher.windowToken, 0)
                true
            } else {
                false
            }
        }

        // Recherche avec clic sur l'icône
        binding.tilRechercher.setEndIconOnClickListener {
            val query = binding.etRechercher.text?.toString()?.trim() ?: ""
            lireEmploye(query)
            val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(binding.etRechercher.windowToken, 0)
        }
    }

    override fun clickEditer(employe: EmployeeModel) {
        startActivity(
            android.content.Intent(this, OperationEmployeActivity::class.java).apply {
                putExtra("numEmp", employe.numEmp)
                putExtra("nom", employe.nom)
                putExtra("nbr_jour", employe.nbr_jour)
                putExtra("taux_journalier", employe.taux_journalier)
            }
        )
    }

    override fun clickSupprimer(employe: EmployeeModel) {
        MaterialAlertDialogBuilder(this).apply {
            setTitle("Supprimer")
            setMessage("Voulez-vous supprimer l'employé : ${employe.nom} ?")
            setCancelable(false)
            setNegativeButton("NON") { dialog, _ -> dialog.dismiss() }
            setPositiveButton("OUI") { dialog, _ ->
                supprimer(employe)
                dialog.dismiss()
            }
        }.create().show()
    }

    private fun lireEmploye(dato: String) = lifecycleScope.launch {
        binding.progressBar.isVisible = true
        val result: UiState<List<EmployeeModel>> = makeCall { EmployeDao.listar(dato) }
        binding.progressBar.isVisible = false

        when (result) {
            is UiState.Success -> {
                (binding.rvEmployes.adapter as? EmployerAdapter)?.setListe(result.data)
                afficherHistogramme(result.data)
            }
            is UiState.Error -> {
                Toast.makeText(this@MainActivity, "Erreur: ${result.message}", Toast.LENGTH_LONG).show()
            }
            is UiState.Loading -> binding.progressBar.isVisible = true
        }
    }

    private fun afficherHistogramme(employes: List<EmployeeModel>) {
        if (employes.isEmpty()) {
            // Réinitialiser l'affichage si aucun employé
            binding.tvSalaireMin.text = "Salaire minimal : 0 Ar"
            binding.tvSalaireMax.text = "Salaire maximal : 0 Ar"
            binding.barChart.clear()
            return
        }

        // Calcul des salaires
        val salaires = employes.map { it.nbr_jour * it.taux_journalier }

        val minSalaire = salaires.minOrNull() ?: 0.0
        val maxSalaire = salaires.maxOrNull() ?: 0.0
        val totalSalaire = salaires.sum()

        // 🔹 Mise à jour des TextViews
        val df = DecimalFormat("#,###.00")
        binding.tvSalaireMin.text = "Salaire minimal : ${df.format(minSalaire)} Ar"
        binding.tvSalaireMax.text = "Salaire maximal : ${df.format(maxSalaire)} Ar"
        binding.tvSalaireTotal.text = "Salaire total : ${df.format(totalSalaire)} Ar"
        // 🔹 Création de l'histogramme avec 3 barres
        val barEntries = mutableListOf<BarEntry>()
        barEntries.add(BarEntry(0f, minSalaire.toFloat()))  // Barre 1: Minimum
        barEntries.add(BarEntry(1f, maxSalaire.toFloat())) // Barre 2: Maximum
        barEntries.add(BarEntry(2f, totalSalaire.toFloat())) // Barre 3: Total

        val labels = listOf("Minimum", "Maximum", "Total")

        val barDataSet = BarDataSet(barEntries, "Statistiques Salariales").apply {
            // 🎨 Couleurs différentes pour chaque barre
            colors = listOf(
                android.graphics.Color.parseColor("#FF6B6B"), // Rouge pour minimum
                android.graphics.Color.parseColor("#4ECDC4"), // Turquoise pour maximum
                android.graphics.Color.parseColor("#FFD93D")  // Jaune/Or pour total
            )
            valueTextSize = 14f
            valueTextColor = android.graphics.Color.BLACK
            valueFormatter = object : ValueFormatter() {
                private val df = DecimalFormat("#,###")
                override fun getFormattedValue(value: Float): String = "${df.format(value)} Ar"
            }
        }

        val barData = BarData(barDataSet).apply {
            barWidth = 0.6f // Barres un peu plus larges car il n'y en a que 3
        }

        binding.barChart.apply {
            data = barData
            description.isEnabled = false
            setPinchZoom(false) // Désactiver le zoom car seulement 3 barres
            setScaleEnabled(false)

            // Configuration de l'axe X (horizontal)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                labelRotationAngle = 0f // Pas de rotation car labels courts
                valueFormatter = IndexAxisValueFormatter(labels)
                textSize = 12f
                textColor = android.graphics.Color.BLACK
            }

            // Configuration de l'axe Y (vertical) gauche
            axisLeft.apply {
                valueFormatter = object : ValueFormatter() {
                    private val df = DecimalFormat("#,###")
                    override fun getFormattedValue(value: Float): String = df.format(value)
                }
                textSize = 10f
                setDrawGridLines(true)
                gridColor = android.graphics.Color.LTGRAY
            }

            // Désactiver l'axe droit
            axisRight.isEnabled = false

            // Légende
            legend.apply {
                isEnabled = true
                textSize = 12f
            }

            // Animation
            animateY(1000)
            invalidate()
        }
    }

    private fun supprimer(model: EmployeeModel) = lifecycleScope.launch {
        binding.progressBar.isVisible = true
        val result: UiState<Boolean> = makeCall { EmployeDao.delete(model.numEmp) }
        binding.progressBar.isVisible = false

        when (result) {
            is UiState.Success -> {
                if (result.data) {
                    Toast.makeText(this@MainActivity, "Employé ${model.nom} supprimé", Toast.LENGTH_SHORT).show()
                    lireEmploye("")
                } else {
                    Toast.makeText(this@MainActivity, "La suppression a échoué", Toast.LENGTH_SHORT).show()
                }
            }
            is UiState.Error -> {
                Toast.makeText(this@MainActivity, "Erreur: ${result.message}", Toast.LENGTH_LONG).show()
            }
            is UiState.Loading -> binding.progressBar.isVisible = true
        }
    }

    companion object {
        var existeChangement = false
    }
}