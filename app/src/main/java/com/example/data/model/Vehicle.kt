package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String, // "Motor" or "Mobil" or "Lainnya"
    val licensePlate: String,
    val currentOdometer: Int,
    val lastOilChangeOdometer: Int,
    val lastOilChangeDate: Long,
    val oilIntervalKm: Int,
    val oilIntervalMonths: Int,
    val oilTypeRecommendation: String = ""
) {
    // Calculates percentage of oil life left (0.0 to 1.0)
    fun getOilLifePercentage(): Float {
        val kmTraveled = currentOdometer - lastOilChangeOdometer
        if (kmTraveled <= 0) return 1.0f
        if (oilIntervalKm <= 0) return 1.0f
        val fraction = 1.0f - (kmTraveled.toFloat() / oilIntervalKm.toFloat())
        return fraction.coerceIn(0.0f, 1.0f)
    }

    // Calculates days left based on average or static time interval
    fun getOilTimeProgress(): Float {
        val elapsedMillis = System.currentTimeMillis() - lastOilChangeDate
        val intervalMillis = oilIntervalMonths * 30L * 24L * 60L * 60L * 1000L
        if (elapsedMillis <= 0) return 1.0f
        if (intervalMillis <= 0) return 1.0f
        val fraction = 1.0f - (elapsedMillis.toFloat() / intervalMillis.toFloat())
        return fraction.coerceIn(0.0f, 1.0f)
    }

    fun isDueForChange(): Boolean {
        val kmTraveled = currentOdometer - lastOilChangeOdometer
        val elapsedMillis = System.currentTimeMillis() - lastOilChangeDate
        val intervalMillis = oilIntervalMonths * 30L * 24L * 60L * 60L * 1000L
        
        return kmTraveled >= oilIntervalKm || elapsedMillis >= intervalMillis
    }

    fun getRemainingKm(): Int {
        val remaining = oilIntervalKm - (currentOdometer - lastOilChangeOdometer)
        return if (remaining < 0) 0 else remaining
    }
}
