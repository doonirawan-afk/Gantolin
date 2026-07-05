package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Vehicle
import com.example.data.model.OilChangeLog
import com.example.data.repository.OilReminderRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OilReminderViewModel(private val repository: OilReminderRepository) : ViewModel() {

    // Vehicles Flow
    val vehicles: StateFlow<List<Vehicle>> = repository.allVehicles
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Logs Flow
    val logs: StateFlow<List<OilChangeLog>> = repository.allLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Seed initial data if the database is empty
        viewModelScope.launch {
            vehicles.collectLatest { list ->
                if (list.isEmpty()) {
                    seedInitialData()
                }
            }
        }
    }

    private suspend fun seedInitialData() {
        val now = System.currentTimeMillis()
        
        // 1. Honda Vario 150 (Motor)
        // Last change was 1,300 KM ago, oil change interval 2000 KM.
        val motorId = repository.insertVehicle(
            Vehicle(
                name = "Honda Vario 150",
                type = "Motor",
                licensePlate = "B 4721 SHC",
                currentOdometer = 12500,
                lastOilChangeOdometer = 11200,
                lastOilChangeDate = now - (45L * 24 * 60 * 60 * 1000), // 45 days ago
                oilIntervalKm = 2000,
                oilIntervalMonths = 3,
                oilTypeRecommendation = "10W-30 (SPX2 / MPX2)"
            )
        )

        // Add history for Vario
        repository.addOilChangeLog(
            OilChangeLog(
                vehicleId = motorId.toInt(),
                date = now - (120L * 24 * 60 * 60 * 1000), // 120 days ago
                odometer = 9500,
                oilBrand = "AHM Oil SPX2",
                oilType = "10W-30",
                cost = 65000.0,
                notes = "Ganti oli rutin pertama kali di bengkel resmi."
            )
        )
        repository.addOilChangeLog(
            OilChangeLog(
                vehicleId = motorId.toInt(),
                date = now - (45L * 24 * 60 * 60 * 1000), // 45 days ago
                odometer = 11200,
                oilBrand = "AHM Oil SPX2",
                oilType = "10W-30",
                cost = 68000.0,
                notes = "Oli mesin saja, kondisi motor aman."
            )
        )

        // 2. Toyota Avanza (Mobil)
        // Last change was 8,500 KM ago, oil change interval 10000 KM. Near due!
        val mobilId = repository.insertVehicle(
            Vehicle(
                name = "Toyota Avanza",
                type = "Mobil",
                licensePlate = "B 2981 FFS",
                currentOdometer = 53500,
                lastOilChangeOdometer = 45000,
                lastOilChangeDate = now - (160L * 24 * 60 * 60 * 1000), // 160 days ago (~5 months)
                oilIntervalKm = 10000,
                oilIntervalMonths = 6,
                oilTypeRecommendation = "0W-20 / 5W-30 Synthetic"
            )
        )

        // Add history for Avanza
        repository.addOilChangeLog(
            OilChangeLog(
                vehicleId = mobilId.toInt(),
                date = now - (320L * 24 * 60 * 60 * 1000), // 320 days ago
                odometer = 35000,
                oilBrand = "TMO Toyota Motor Oil",
                oilType = "0W-20",
                cost = 450000.0,
                notes = "Servis rutin + filter oli."
            )
        )
        repository.addOilChangeLog(
            OilChangeLog(
                vehicleId = mobilId.toInt(),
                date = now - (160L * 24 * 60 * 60 * 1000), // 160 days ago
                odometer = 45000,
                oilBrand = "Shell Helix HX8",
                oilType = "5W-30",
                cost = 480000.0,
                notes = "Pindah ke Shell HX8, suara mesin lebih halus."
            )
        )
    }

    // --- Actions ---

    fun addVehicle(
        name: String,
        type: String,
        licensePlate: String,
        currentOdometer: Int,
        lastOilChangeOdometer: Int,
        lastOilChangeDate: Long,
        oilIntervalKm: Int,
        oilIntervalMonths: Int,
        oilTypeRecommendation: String
    ) {
        viewModelScope.launch {
            repository.insertVehicle(
                Vehicle(
                    name = name,
                    type = type,
                    licensePlate = licensePlate,
                    currentOdometer = currentOdometer,
                    lastOilChangeOdometer = lastOilChangeOdometer,
                    lastOilChangeDate = lastOilChangeDate,
                    oilIntervalKm = oilIntervalKm,
                    oilIntervalMonths = oilIntervalMonths,
                    oilTypeRecommendation = oilTypeRecommendation
                )
            )
        }
    }

    fun updateVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            repository.updateVehicle(vehicle)
        }
    }

    fun deleteVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            repository.deleteVehicle(vehicle)
        }
    }

    fun updateOdometer(vehicleId: Int, newOdometer: Int) {
        viewModelScope.launch {
            vehicles.value.find { it.id == vehicleId }?.let { vehicle ->
                // Ensure the odometer doesn't go backwards
                if (newOdometer >= vehicle.currentOdometer) {
                    repository.updateVehicle(vehicle.copy(currentOdometer = newOdometer))
                }
            }
        }
    }

    fun addOilChangeLog(
        vehicleId: Int,
        date: Long,
        odometer: Int,
        oilBrand: String,
        oilType: String,
        cost: Double,
        notes: String
    ) {
        viewModelScope.launch {
            repository.addOilChangeLog(
                OilChangeLog(
                    vehicleId = vehicleId,
                    date = date,
                    odometer = odometer,
                    oilBrand = oilBrand,
                    oilType = oilType,
                    cost = cost,
                    notes = notes
                )
            )
        }
    }

    fun deleteLog(logId: Int) {
        viewModelScope.launch {
            repository.deleteLogById(logId)
        }
    }

    companion object {
        fun provideFactory(repository: OilReminderRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return OilReminderViewModel(repository) as T
                }
            }
    }
}
