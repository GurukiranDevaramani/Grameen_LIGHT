package com.example.grameen_light.viewmodel

import androidx.lifecycle.*
import com.example.grameen_light.database.PoleEntity
import com.example.grameen_light.repository.PoleRepository
import kotlinx.coroutines.launch

/**
 * STEP 11: ViewModel Layer
 */
class PoleViewModel(private val repository: PoleRepository) : ViewModel() {

    // Using Room Flow converted to LiveData for UI
    val allPoles: LiveData<List<PoleEntity>> = repository.allLocalPoles.asLiveData()

    /**
     * Submit report through repository
     */
    fun submitReport(
        poleId: String,
        status: String,
        complaintId: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        repository.submitPoleReport(poleId, status, complaintId, onResult)
    }

    fun insert(poles: List<PoleEntity>) = viewModelScope.launch {
        repository.insertLocal(poles.first()) // Assuming one for simplicity in this call
    }

    class Factory(private val repository: PoleRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PoleViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PoleViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
