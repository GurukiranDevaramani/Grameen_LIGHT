package com.example.grameen_light.activities

import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.grameen_light.GrameenLightApp
import com.example.grameen_light.databinding.ActivityReportBinding
import com.example.grameen_light.viewmodel.PoleViewModel

class ReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportBinding
    private val viewModel: PoleViewModel by viewModels {
        PoleViewModel.Factory((application as GrameenLightApp).repository)
    }
    private var poleId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        poleId = intent.getStringExtra("POLE_ID")
        binding.tvPoleId.text = "Pole ID: $poleId"

        binding.btnSubmitReport.setOnClickListener {
            submitReport()
        }
    }

    /**
     * STEP 6: Upload Complaint Feature
     */
    private fun submitReport() {
        if (poleId == null) return

        val selectedId = binding.rgStatus.checkedRadioButtonId
        if (selectedId == -1) {
            Toast.makeText(this, "Please select a status", Toast.LENGTH_SHORT).show()
            return
        }
        val radioButton = findViewById<RadioButton>(selectedId)
        val status = radioButton.text.toString()

        // Generate Complaint ID format: GL + last 5 digits of current timestamp
        val complaintId = "GL" + (System.currentTimeMillis() % 100000).toString().padStart(5, '0')

        // Submit to Repository (MVVM)
        viewModel.submitReport(poleId!!, status, complaintId) { success, error ->
            if (success) {
                // STEP 8: Success Handling
                Toast.makeText(this, "Report Submitted! ID: $complaintId", Toast.LENGTH_LONG).show()
                finish()
            } else {
                // STEP 8: Write failure handling
                Toast.makeText(this, "Failed: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
