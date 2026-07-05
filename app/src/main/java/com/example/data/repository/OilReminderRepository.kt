package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.model.Vehicle
import com.example.data.model.OilChangeLog
import kotlinx.coroutines.flow.Flow

class OilReminderRepository(private val database: AppDatabase) {
    private val dao = database.oilReminderDao()

    val allVehicles: Flow<List<Vehicle>> = dao.getAllVehicles()
    val allLogs: Flow<List<OilChangeLog>> = dao.getAllLogs()

    fun getVehicleById(id: Int): Flow<Vehicle?> = dao.getVehicleById(id)

    fun getLogsForVehicle(vehicleId: Int): Flow<List<OilChangeLog>> = dao.getLogsForVehicle(vehicleId)

    suspend fun insertVehicle(vehicle: Vehicle): Long = dao.insertVehicle(vehicle)

    suspend fun updateVehicle(vehicle: Vehicle) = dao.updateVehicle(vehicle)

    suspend fun deleteVehicle(vehicle: Vehicle) = dao.deleteVehicle(vehicle)

    suspend fun addOilChangeLog(log: OilChangeLog) = dao.addOilChangeLogAndUpdateVehicle(log)

    suspend fun deleteLogById(id: Int) = dao.deleteLogById(id)
}
