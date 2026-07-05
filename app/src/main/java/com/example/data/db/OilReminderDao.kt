package com.example.data.db

import androidx.room.*
import com.example.data.model.Vehicle
import com.example.data.model.OilChangeLog
import kotlinx.coroutines.flow.Flow

@Dao
interface OilReminderDao {
    
    // --- Vehicles ---
    @Query("SELECT * FROM vehicles ORDER BY name ASC")
    fun getAllVehicles(): Flow<List<Vehicle>>

    @Query("SELECT * FROM vehicles WHERE id = :id")
    fun getVehicleById(id: Int): Flow<Vehicle?>

    @Query("SELECT * FROM vehicles WHERE id = :id")
    suspend fun getVehicleByIdSync(id: Int): Vehicle?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: Vehicle): Long

    @Update
    suspend fun updateVehicle(vehicle: Vehicle)

    @Delete
    suspend fun deleteVehicle(vehicle: Vehicle)

    // --- Oil Change Logs ---
    @Query("SELECT * FROM oil_change_logs ORDER BY date DESC")
    fun getAllLogs(): Flow<List<OilChangeLog>>

    @Query("SELECT * FROM oil_change_logs WHERE vehicleId = :vehicleId ORDER BY date DESC")
    fun getLogsForVehicle(vehicleId: Int): Flow<List<OilChangeLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: OilChangeLog): Long

    @Query("DELETE FROM oil_change_logs WHERE id = :id")
    suspend fun deleteLogById(id: Int)

    // Room Transaction to add a log and update vehicle's last change status
    @Transaction
    suspend fun addOilChangeLogAndUpdateVehicle(log: OilChangeLog) {
        insertLog(log)
        val vehicle = getVehicleByIdSync(log.vehicleId)
        if (vehicle != null) {
            // Update the vehicle's last change info
            val updatedVehicle = vehicle.copy(
                lastOilChangeOdometer = log.odometer,
                lastOilChangeDate = log.date,
                // Ensure current odometer is at least as high as this log's odometer
                currentOdometer = if (log.odometer > vehicle.currentOdometer) log.odometer else vehicle.currentOdometer
            )
            updateVehicle(updatedVehicle)
        }
    }
}
