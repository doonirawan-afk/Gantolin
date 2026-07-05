package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "oil_change_logs")
data class OilChangeLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val vehicleId: Int,
    val date: Long,
    val odometer: Int,
    val oilBrand: String,
    val oilType: String,
    val cost: Double,
    val notes: String = ""
)
