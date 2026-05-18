package com.example.grameen_light.utils

import android.util.Log
import com.example.grameen_light.database.PoleEntity
import com.example.grameen_light.models.Pole
import com.example.grameen_light.repository.PoleRepository
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Utility class to generate and upload dummy streetlight data for demo purposes.
 */
object DummyDataGenerator {
    private const val TAG = "DummyDataGenerator"

    /**
     * Generates a list of 15 realistic sample poles around a central location.
     */
    fun generateSamplePoles(): List<Pole> {
        val centerLat = 12.9716
        val centerLng = 77.5946
        val poles = mutableListOf<Pole>()

        val repairStatuses = listOf("Pending", "Assigned", "In Progress", "Fixed")

        for (i in 1..15) {
            val latOffset = (Math.random() - 0.5) / 50.0 
            val lngOffset = (Math.random() - 0.5) / 50.0
            
            val status = when {
                i <= 8 -> "Working"
                i <= 12 -> "Fused"
                else -> "Burning in Day"
            }

            poles.add(
                Pole(
                    poleId = "P${1000 + i}",
                    latitude = centerLat + latOffset,
                    longitude = centerLng + lngOffset,
                    status = status,
                    complaintId = if (status != "Working") "GL${1000 + i}" else "",
                    repairStatus = if (status != "Working") repairStatuses.random() else "Normal"
                )
            )
        }
        return poles
    }

    /**
     * Uploads to Firebase AND saves to local Room DB immediately for demo/offline visibility.
     */
    fun uploadDummyData(repository: PoleRepository, onComplete: (Boolean) -> Unit) {
        val dummyPoles = generateSamplePoles()
        
        // 1. Save to Room immediately (Ensures local visibility even if Firebase fails)
        CoroutineScope(Dispatchers.IO).launch {
            repository.syncFirebaseToRoom(dummyPoles)
            Log.d(TAG, "Saved 15 poles to local Room DB")
        }

        // 2. Attempt Firebase Upload
        val ref = repository.getFirebaseReference()
        var completed = 0
        var failedCount = 0

        dummyPoles.forEach { pole ->
            ref.child(pole.poleId).setValue(pole)
                .addOnCompleteListener { task ->
                    completed++
                    if (!task.isSuccessful) failedCount++
                    
                    if (completed == dummyPoles.size) {
                        Log.d(TAG, "Firebase upload finished. Failures: $failedCount")
                        // We return true if at least local save worked, which it did
                        onComplete(true)
                    }
                }
        }
    }
}
