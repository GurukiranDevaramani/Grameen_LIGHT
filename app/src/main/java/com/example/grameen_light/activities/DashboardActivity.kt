package com.example.grameen_light.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.grameen_light.GrameenLightApp
import com.example.grameen_light.databinding.ActivityDashboardBinding
import com.example.grameen_light.models.Pole
import com.example.grameen_light.viewmodel.PoleViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: PoleViewModel by viewModels {
        PoleViewModel.Factory((application as GrameenLightApp).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Observe Room DB (MVVM) - This works offline
        viewModel.allPoles.observe(this) { localPoles ->
            val poles = localPoles.map { 
                Pole(it.poleId, 0.0, 0.0, it.status, it.complaintId) 
            }
            updateUI(poles)
        }

        // Also listen to Firebase for real-time online updates
        observeFirebase()
    }

    private fun observeFirebase() {
        (application as GrameenLightApp).repository.getFirebaseReference().addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val poles = mutableListOf<Pole>()
                    for (poleSnapshot in snapshot.children) {
                        val pole = poleSnapshot.getValue(Pole::class.java)
                        pole?.let { poles.add(it) }
                    }
                    updateUI(poles)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun updateUI(poles: List<Pole>) {
        val total = poles.size
        val working = poles.count { it.status == "Working" }
        
        binding.tvTotalReports.text = total.toString()
        val fixed = poles.count { it.repairStatus == "Fixed" || it.status == "Working" }
        binding.pbFixedPoles.progress = if (total > 0) (fixed * 100) / total else 0
        binding.tvEnergySaved.text = "${working * 5} kWh"
    }
}
