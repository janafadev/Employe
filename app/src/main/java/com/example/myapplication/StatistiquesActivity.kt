package com.example.myapplication

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.data.dao.EmployeDao
import com.example.myapplication.data.model.EmployeeModel
import com.example.myapplication.databinding.ActivityStatistiquesBinding
import com.example.myapplication.presentation.common.UiState
import com.example.myapplication.presentation.common.makeCall
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class StatistiquesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatistiquesBinding
    private var employesList = mutableListOf<EmployeeModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatistiquesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar
        binding.toolbar.apply {
            setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
            title = "Histogramme des salaires"
        }

        chargerDonnees()
    }

    private fun chargerDonnees() = lifecycleScope.launch {
        binding.progressBar.isVisible = true
        val result = makeCall { EmployeDao.listar("") }
        binding.progressBar.isVisible = false

        when (result) {
            is UiState.Success -> {
                employesList = result.data.toMutableList()
                afficherHistogramme()
            }
            is UiState.Error -> {
                Toast.makeText(this@StatistiquesActivity, "Erreur: ${result.message}", Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    private fun afficherHistogramme() {
        if (employesList.isEmpty()) {
            Toast.makeText(this, "Aucune donnée à afficher", Toast.LENGTH_SHORT).show()
            return
        }

        val barEntries = mutableListOf<BarEntry>()
        val noms = mutableListOf<String>()

        employesList.forEachIndexed { index, employe ->
            val salaire = employe.nbr_jour * employe.taux_journalier
            barEntries.add(BarEntry(index.toFloat(), salaire.toFloat()))
            noms.add(employe.nom)
        }

        val barDataSet = BarDataSet(barEntries, "Salaires (Ar)").apply {
            colors = listOf(
                Color.parseColor("#FF6B6B"),
                Color.parseColor("#4ECDC4"),
                Color.parseColor("#45B7D1"),
                Color.parseColor("#96CEB4"),
                Color.parseColor("#FFEAA7")
            )
            valueTextSize = 12f
            valueFormatter = object : ValueFormatter() {
                private val df = DecimalFormat("#,###")
                override fun getFormattedValue(value: Float): String = "${df.format(value)} Ar"
            }
        }

        val barData = BarData(barDataSet).apply { barWidth = 0.8f }

        binding.barChart.apply {
            data = barData
            description.isEnabled = false
            setPinchZoom(true)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                labelRotationAngle = -45f
                valueFormatter = IndexAxisValueFormatter(noms)
            }
            axisLeft.valueFormatter = object : ValueFormatter() {
                private val df = DecimalFormat("#,###")
                override fun getFormattedValue(value: Float): String = df.format(value)
            }
            axisRight.isEnabled = false
            animateY(1000)
            invalidate()
        }
    }
}
