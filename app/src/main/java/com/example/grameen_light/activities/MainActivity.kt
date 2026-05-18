package com.example.grameen_light.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.grameen_light.GrameenLightApp
import com.example.grameen_light.databinding.ActivityMainBinding
import com.example.grameen_light.utils.DummyDataGenerator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = (application as GrameenLightApp).repository

        binding.btnOpenMap.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        binding.btnRepairTracker.setOnClickListener {
            startActivity(Intent(this, TrackerActivity::class.java))
        }

        binding.btnDashboard.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        // Updated button to use local + firebase save
        binding.btnLoadDummyData.setOnClickListener {
            DummyDataGenerator.uploadDummyData(repository) { success ->
                if (success) {
                    Toast.makeText(this, "Sample data loaded locally! (Firebase sync pending)", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}
