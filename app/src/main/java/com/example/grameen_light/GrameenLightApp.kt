package com.example.grameen_light

import android.app.Application
import com.example.grameen_light.database.PoleDatabase
import com.example.grameen_light.firebase.FirebaseRepository
import com.example.grameen_light.repository.PoleRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class GrameenLightApp : Application() {

    // Database and Repository setup for Dependency Injection (Manual)
    val database by lazy { PoleDatabase.getDatabase(this) }
    val firebaseRepo by lazy { FirebaseRepository() }
    val repository by lazy { PoleRepository(database.poleDao(), firebaseRepo) }

    override fun onCreate() {
        super.onCreate()
        
        // STEP 2: Initialize Firebase
        FirebaseApp.initializeApp(this)

        // STEP 9: Enable Realtime Database Offline Persistence globally
        // This must be done before any database reference is created
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        } catch (e: Exception) {
            // Persistence might already be enabled or reference already created
        }
    }
}
