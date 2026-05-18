package com.example.grameen_light.firebase

import android.util.Log
import com.example.grameen_light.models.Pole
import com.google.firebase.database.*

class FirebaseRepository {
    private val TAG = "FirebaseRepository"
    
    // Reference to "StreetLights" node in Firebase
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("StreetLights")

    init {
        // Enable Firebase Offline Persistence for this reference
        database.keepSynced(true)
    }

    /**
     * Fetch all poles once or listen for real-time updates
     */
    fun getAllPoles(onDataChange: (List<Pole>) -> Unit, onError: (String) -> Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val poleList = mutableListOf<Pole>()
                Log.d(TAG, "Firebase onDataChange called. Snapshot exists: ${snapshot.exists()}, count: ${snapshot.childrenCount}")
                
                if (snapshot.exists()) {
                    for (poleSnapshot in snapshot.children) {
                        try {
                            val pole = poleSnapshot.getValue(Pole::class.java)
                            if (pole != null) {
                                poleList.add(pole)
                            } else {
                                Log.w(TAG, "Pole data was null at ${poleSnapshot.key}")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing pole data: ${e.message}")
                        }
                    }
                }
                Log.d(TAG, "Returning ${poleList.size} poles to listener")
                onDataChange(poleList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Firebase read cancelled: ${error.message}")
                onError(error.message)
            }
        })
    }

    /**
     * Submit a report for a specific pole
     */
    fun submitReport(poleId: String, status: String, complaintId: String, onResult: (Boolean, String?) -> Unit) {
        val updates = mapOf(
            "status" to status,
            "complaintId" to complaintId,
            "repairStatus" to "Assigned"
        )

        Log.d(TAG, "Updating pole $poleId in Firebase with status $status")
        database.child(poleId).updateChildren(updates)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully updated pole $poleId in Firebase")
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to update pole $poleId in Firebase: ${e.message}")
                onResult(false, e.message)
            }
    }

    fun getReference(): DatabaseReference = database
}
