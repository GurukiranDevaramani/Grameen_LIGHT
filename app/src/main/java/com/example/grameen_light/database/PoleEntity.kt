package com.example.grameen_light.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "poles")
data class PoleEntity(
    @PrimaryKey val poleId: String,
    val status: String,
    val complaintId: String
)
