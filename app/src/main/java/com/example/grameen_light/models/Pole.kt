package com.example.grameen_light.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Pole(
    var poleId: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var status: String = "Working", // Working, Fused, Burning in Day
    var complaintId: String = "",
    var repairStatus: String = "Normal" // Normal, Assigned, Pending, Fixed
)
