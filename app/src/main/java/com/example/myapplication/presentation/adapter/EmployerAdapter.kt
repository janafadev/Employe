package com.example.myapplication.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.model.EmployeeModel
import com.example.myapplication.databinding.ItemEmployerBinding
import java.util.Locale

class EmployerAdapter(
    private val onClickListener: IOnClickListener
) : RecyclerView.Adapter<EmployerAdapter.EmployerViewHolder>() {

    private var liste = emptyList<EmployeeModel>()

    interface IOnClickListener {
        fun clickEditer(employe: EmployeeModel)
        fun clickSupprimer(employe: EmployeeModel)
    }

    inner class EmployerViewHolder(private val binding: ItemEmployerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun lier(employe: EmployeeModel) {
            Log.d("DEBUG_ADAPTER", "📝 Affichage employé: numEmp=${employe.numEmp}, nom=${employe.nom}")

            // Affichage des données
            binding.tvNom.text = employe.nom
            binding.tvCode.text = employe.numEmp.toString()
            binding.tvJours.text = employe.nbr_jour.toString()
            binding.tvTaux.text = String.format(Locale.US, "%.2f", employe.taux_journalier)

            val salaireTotal = employe.nbr_jour * employe.taux_journalier
            binding.tvSalaire.text = String.format(Locale.US, "%.2f", salaireTotal)

            // Gestion des clics
            binding.ibEditer.setOnClickListener {
                Log.d("DEBUG_ADAPTER", "✏️ Clic éditer sur employé numEmp=${employe.numEmp}")
                onClickListener.clickEditer(employe)
            }

            binding.ibSupprimer.setOnClickListener {
                Log.d("DEBUG_ADAPTER", "🗑️ Clic supprimer sur employé numEmp=${employe.numEmp}")
                onClickListener.clickSupprimer(employe)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployerViewHolder {
        val binding = ItemEmployerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EmployerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        Log.d("DEBUG_ADAPTER", "📊 Nombre d'items: ${liste.size}")
        return liste.size
    }

    override fun onBindViewHolder(holder: EmployerViewHolder, position: Int) {
        if (position < liste.size) {
            holder.lier(liste[position])
        } else {
            Log.e("DEBUG_ADAPTER", "❌ Position invalide: $position, taille liste: ${liste.size}")
        }
    }

    fun setListe(listeEmployes: List<EmployeeModel>) {
        Log.d("DEBUG_ADAPTER", "📋 Mise à jour de la liste: ${listeEmployes.size} employé(s)")

        // ✅ Utilisation de DiffUtil pour des animations fluides
        val diffCallback = EmployeDiffCallback(this.liste, listeEmployes)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.liste = listeEmployes
        diffResult.dispatchUpdatesTo(this)

        // Log de la nouvelle liste
        listeEmployes.forEachIndexed { index, emp ->
            Log.d("DEBUG_ADAPTER", "  [$index] numEmp=${emp.numEmp}, nom=${emp.nom}")
        }
    }

    // ✅ DiffUtil pour optimiser les mises à jour
    private class EmployeDiffCallback(
        private val oldList: List<EmployeeModel>,
        private val newList: List<EmployeeModel>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].numEmp == newList[newItemPosition].numEmp
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.numEmp == newItem.numEmp &&
                    oldItem.nom == newItem.nom &&
                    oldItem.nbr_jour == newItem.nbr_jour &&
                    oldItem.taux_journalier == newItem.taux_journalier
        }
    }
}