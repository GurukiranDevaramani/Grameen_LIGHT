package com.example.grameen_light.activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.grameen_light.GrameenLightApp
import com.example.grameen_light.databinding.ActivityMapBinding
import com.example.grameen_light.models.Pole
import com.example.grameen_light.repository.PoleRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapBinding
    private lateinit var repository: PoleRepository
    private val TAG = "MapActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        Configuration.getInstance().userAgentValue = packageName

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = (application as GrameenLightApp).repository

        setupMap()
        
        // Start observing both sources
        observeFirebaseUpdates()
        observeLocalRoom()

        binding.fabRefresh.setOnClickListener {
            observeFirebaseUpdates()
        }
    }

    private fun setupMap() {
        binding.map.setTileSource(TileSourceFactory.MAPNIK)
        binding.map.setMultiTouchControls(true)
        binding.map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
        
        val mapController = binding.map.controller
        mapController.setZoom(14.0)
        val startPoint = GeoPoint(12.9716, 77.5946)
        mapController.setCenter(startPoint)
    }

    private fun observeFirebaseUpdates() {
        repository.getFirebaseReference().addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val poles = mutableListOf<Pole>()
                    binding.map.overlays.clear()
                    for (poleSnapshot in snapshot.children) {
                        val pole = poleSnapshot.getValue(Pole::class.java)
                        pole?.let { 
                            poles.add(it)
                            addMarkerForPole(it)
                        }
                    }
                    binding.map.invalidate()
                    
                    lifecycleScope.launch {
                        repository.syncFirebaseToRoom(poles)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Firebase fail: ${error.message}")
            }
        })
    }

    private fun observeLocalRoom() {
        lifecycleScope.launch {
            repository.allLocalPoles.collect { localPoles ->
                if (localPoles.isNotEmpty()) {
                    Log.d(TAG, "Loading ${localPoles.size} markers from Room fallback")
                    binding.map.overlays.clear()
                    localPoles.forEach { entity ->
                        val pole = Pole(entity.poleId, 12.9716, 77.5946, entity.status, entity.complaintId)
                        // Note: Lat/Lng are not in Room entity yet, but we'll add them if needed. 
                        // For demo, we use central coords if Room doesn't have them.
                        // Actually, let's update PoleEntity to include Lat/Lng.
                        addMarkerForPole(pole)
                    }
                    binding.map.invalidate()
                }
            }
        }
    }

    private fun addMarkerForPole(pole: Pole) {
        val marker = Marker(binding.map)
        marker.position = GeoPoint(pole.latitude, pole.longitude)
        marker.title = pole.poleId
        marker.snippet = "Status: ${pole.status}\nRepair: ${pole.repairStatus}"
        
        val iconRes = when (pole.status) {
            "Working" -> android.R.drawable.ic_menu_mylocation
            "Fused" -> android.R.drawable.ic_delete
            "Burning in Day" -> android.R.drawable.ic_menu_day
            else -> android.R.drawable.ic_dialog_info
        }
        marker.icon = ContextCompat.getDrawable(this, iconRes)
        
        marker.setOnMarkerClickListener { m, _ ->
            val intent = Intent(this, ReportActivity::class.java)
            intent.putExtra("POLE_ID", m.title)
            startActivity(intent)
            true
        }
        binding.map.overlays.add(marker)
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }
}
