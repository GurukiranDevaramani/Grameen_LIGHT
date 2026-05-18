package com.example.grameen_light.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grameen_light.GrameenLightApp
import com.example.grameen_light.adapters.TrackerAdapter
import com.example.grameen_light.databinding.ActivityTrackerBinding
import com.example.grameen_light.viewmodel.PoleViewModel

class TrackerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackerBinding
    private lateinit var adapter: TrackerAdapter
    private val viewModel: PoleViewModel by viewModels {
        PoleViewModel.Factory((application as GrameenLightApp).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = TrackerAdapter(emptyList())
        binding.rvTracker.layoutManager = LinearLayoutManager(this)
        binding.rvTracker.adapter = adapter

        // Observe Room DB - Works even if Firebase fails
        viewModel.allPoles.observe(this) { poles ->
            val complaintPoles = poles.filter { it.complaintId.isNotEmpty() }
            adapter.updateData(complaintPoles)
        }
    }
}
