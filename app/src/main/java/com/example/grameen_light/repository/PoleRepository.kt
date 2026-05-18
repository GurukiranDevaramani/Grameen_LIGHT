package com.example.grameen_light.repository

import com.example.grameen_light.database.PoleDao
import com.example.grameen_light.database.PoleEntity
import com.example.grameen_light.firebase.FirebaseRepository
import com.example.grameen_light.models.Pole
import kotlinx.coroutines.flow.Flow

/**
 * Main Repository that orchestrates data between Firebase and Local Room DB
 */
class PoleRepository(
    private val poleDao: PoleDao,
    private val firebaseRepo: FirebaseRepository
) {

    // Room Data (Offline Cache)
    val allLocalPoles: Flow<List<PoleEntity>> = poleDao.getAllPoles()

    // Firebase Data Reference
    fun getFirebaseReference() = firebaseRepo.getReference()

    /**
     * Submit report to Firebase and also update local Room cache
     */
    fun submitPoleReport(
        poleId: String,
        status: String,
        complaintId: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        firebaseRepo.submitReport(poleId, status, complaintId) { success, error ->
            onResult(success, error)
        }
    }

    /**
     * Sync data from Firebase to Room
     */
    suspend fun syncFirebaseToRoom(poles: List<Pole>) {
        val entities = poles.map { 
            PoleEntity(it.poleId, it.status, it.complaintId) 
        }
        poleDao.insertPoles(entities)
    }

    suspend fun insertLocal(pole: PoleEntity) {
        poleDao.insertPoles(listOf(pole))
    }
}
