package com.example.grameen_light.firebase

import com.example.grameen_light.models.Pole
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseHelper {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("StreetLights")

    fun savePole(pole: Pole, onComplete: (Boolean) -> Unit) {
        database.child(pole.poleId).setValue(pole)
            .addOnCompleteListener { onComplete(it.isSuccessful) }
    }

    fun updatePoleStatus(poleId: String, status: String, complaintId: String, onComplete: (Boolean) -> Unit) {
        val updates = mapOf(
            "status" to status,
            "complaintId" to complaintId,
            "repairStatus" to "Reported"
        )
        database.child(poleId).updateChildren(updates)
            .addOnCompleteListener { onComplete(it.isSuccessful) }
    }

    fun getPolesReference(): DatabaseReference {
        return database
    }
}
