package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Vehicle
import com.example.data.model.OilChangeLog
import com.example.viewmodel.OilReminderViewModel
import java.text.DateFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailScreen(
    vehicleId: Int,
    viewModel: OilReminderViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vehicles by viewModel.vehicles.collectAsState()
    val logs by viewModel.logs.collectAsState()

    val vehicle = vehicles.find { it.id == vehicleId }
    val vehicleLogs = logs.filter { it.vehicleId == vehicleId }

    if (vehicle == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Kendaraan tidak ditemukan")
        }
        return
    }

    var showOdoDialog by remember { mutableStateOf(false) }
    var showLogDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Odometer state
    var odoInput by remember { mutableStateOf(vehicle.currentOdometer.toString()) }

    // Log Form states
    var logOdometer by remember { mutableStateOf(vehicle.currentOdometer.toString()) }
    var oilBrand by remember { mutableStateOf("") }
    var oilType by remember { mutableStateOf("") }
    var costInput by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val lifePercent = vehicle.getOilLifePercentage()
    val isDue = vehicle.isDueForChange()

    val statusColor = if (isDue) {
        MaterialTheme.colorScheme.error
    } else if (lifePercent < 0.25f) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.primary
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(vehicle.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("detail_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.testTag("detail_delete_button")) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus Kendaraan", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Vehicle Info Card
                item {
                    VehicleSpecCard(
                        vehicle = vehicle,
                        statusColor = statusColor,
                        onUpdateOdoClick = {
                            odoInput = vehicle.currentOdometer.toString()
                            showOdoDialog = true
                        }
                    )
                }

                // Health Progress Bars
                item {
                    HealthStatusWidget(
                        vehicle = vehicle,
                        lifePercent = lifePercent,
                        timePercent = vehicle.getOilTimeProgress(),
                        statusColor = statusColor
                    )
                }

                // Actions Card (Quick Actions)
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    logOdometer = vehicle.currentOdometer.toString()
                                    showLogDialog = true
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp)
                                    .testTag("btn_log_oil_change")
                            ) {
                                Icon(Icons.Default.LocalGasStation, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Catat Ganti Oli", fontSize = 12.sp)
                            }

                            FilledTonalButton(
                                onClick = {
                                    odoInput = vehicle.currentOdometer.toString()
                                    showOdoDialog = true
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp)
                                    .testTag("btn_update_odo")
                            ) {
                                Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Update KM", fontSize = 12.sp)
                            }
                        }
                    }
                }

                // History Title
                item {
                    Text(
                        text = "Riwayat Ganti Oli",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (vehicleLogs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Belum ada riwayat ganti oli",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(vehicleLogs, key = { it.id }) { log ->
                        HistoryLogItem(
                            log = log,
                            onDelete = { viewModel.deleteLog(log.id) }
                        )
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }

            // Odometer Update Dialog
            if (showOdoDialog) {
                AlertDialog(
                    onDismissRequest = { showOdoDialog = false },
                    title = { Text("Update Odometer", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "Masukkan angka odometer baru untuk ${vehicle.name} (KM saat ini: ${formatKm(vehicle.currentOdometer)} KM).",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            OutlinedTextField(
                                value = odoInput,
                                onValueChange = { odoInput = it },
                                label = { Text("Odometer Baru (KM)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("dialog_odo_input"),
                                singleLine = true
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val km = odoInput.toIntOrNull() ?: 0
                                if (km >= vehicle.currentOdometer) {
                                    viewModel.updateOdometer(vehicle.id, km)
                                    showOdoDialog = false
                                }
                            },
                            enabled = (odoInput.toIntOrNull() ?: 0) >= vehicle.currentOdometer,
                            modifier = Modifier.testTag("dialog_odo_confirm")
                        ) {
                            Text("Simpan")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showOdoDialog = false }) {
                            Text("Batal")
                        }
                    }
                )
            }

            // Oil Change Log Dialog
            if (showLogDialog) {
                AlertDialog(
                    onDismissRequest = { showLogDialog = false },
                    title = { Text("Catat Ganti Oli Baru", fontWeight = FontWeight.Bold) },
                    text = {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                Text(
                                    "Masukkan data servis ganti oli kendaraan Anda. Ini akan mereset jadwal ganti oli berikutnya.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = logOdometer,
                                    onValueChange = { logOdometer = it },
                                    label = { Text("Odometer saat ganti oli (KM)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("log_odo_input"),
                                    singleLine = true
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = oilBrand,
                                    onValueChange = { oilBrand = it },
                                    label = { Text("Merek Oli (Contoh: Shell, Castrol)") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("log_brand_input"),
                                    singleLine = true
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = oilType,
                                    onValueChange = { oilType = it },
                                    label = { Text("Spesifikasi Oli (Contoh: 10W-40)") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("log_spec_input"),
                                    singleLine = true
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = costInput,
                                    onValueChange = { costInput = it },
                                    label = { Text("Total Biaya (Rp)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("log_cost_input"),
                                    singleLine = true
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = notes,
                                    onValueChange = { notes = it },
                                    label = { Text("Catatan tambahan (Contoh: Filter oli diganti)") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("log_notes_input"),
                                    singleLine = true
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val odo = logOdometer.toIntOrNull() ?: vehicle.currentOdometer
                                val cost = costInput.toDoubleOrNull() ?: 0.0

                                viewModel.addOilChangeLog(
                                    vehicleId = vehicle.id,
                                    date = System.currentTimeMillis(),
                                    odometer = odo,
                                    oilBrand = oilBrand,
                                    oilType = oilType,
                                    cost = cost,
                                    notes = notes
                                )

                                // Clear states and dismiss
                                oilBrand = ""
                                oilType = ""
                                costInput = ""
                                notes = ""
                                showLogDialog = false
                            },
                            enabled = logOdometer.isNotBlank(),
                            modifier = Modifier.testTag("log_confirm_button")
                        ) {
                            Text("Simpan")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogDialog = false }) {
                            Text("Batal")
                        }
                    }
                )
            }

            // Delete Confirmation Dialog
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Hapus Kendaraan?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error) },
                    text = { Text("Apakah Anda yakin ingin menghapus ${vehicle.name}? Semua data spesifikasi dan riwayat ganti oli akan hilang permanen.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.deleteVehicle(vehicle)
                                showDeleteDialog = false
                                onBack()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.testTag("delete_confirm_button")
                        ) {
                            Text("Hapus")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Batal")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun VehicleSpecCard(
    vehicle: Vehicle,
    statusColor: Color,
    onUpdateOdoClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (vehicle.type == "Motor") Icons.Default.TwoWheeler else Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Spesifikasi Kendaraan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // License Plate Tag
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = vehicle.licensePlate.ifEmpty { "Tanpa Plat" },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Details Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Odometer Saat Ini", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${formatKm(vehicle.currentOdometer)} KM", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Odometer",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { onUpdateOdoClick() }
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text("Rekomendasi Jenis Oli", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        vehicle.oilTypeRecommendation.ifEmpty { "Semua Spesifikasi Standard" },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Interval Odometer", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Setiap ${formatKm(vehicle.oilIntervalKm)} KM", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                }

                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text("Interval Durasi", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Setiap ${vehicle.oilIntervalMonths} Bulan", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun HealthStatusWidget(
    vehicle: Vehicle,
    lifePercent: Float,
    timePercent: Float,
    statusColor: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Parameter Pengingat Ganti Oli",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Mileage Limit Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Berdasarkan Jarak (KM)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    Text(
                        "Tersisa ${formatKm(vehicle.getRemainingKm())} KM dari ${formatKm(vehicle.oilIntervalKm)} KM",
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { lifePercent },
                    color = statusColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time Limit Bar
            val daysPassed = ((System.currentTimeMillis() - vehicle.lastOilChangeDate) / (24L * 60L * 60L * 1000L)).toInt()
            val totalDays = vehicle.oilIntervalMonths * 30
            val remainingDays = (totalDays - daysPassed).coerceAtLeast(0)

            val timeStatusColor = if (daysPassed >= totalDays) {
                MaterialTheme.colorScheme.error
            } else if (remainingDays < 15) {
                MaterialTheme.colorScheme.tertiary
            } else {
                MaterialTheme.colorScheme.primary
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Berdasarkan Waktu", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    Text(
                        "Tersisa $remainingDays hari dari $totalDays hari",
                        style = MaterialTheme.typography.bodySmall,
                        color = timeStatusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { timePercent },
                    color = timeStatusColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            val dateStr = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale("id", "ID")).format(Date(vehicle.lastOilChangeDate))
            Text(
                "Ganti oli terakhir dilakukan pada tanggal $dateStr di KM ${formatKm(vehicle.lastOilChangeOdometer)}.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun HistoryLogItem(
    log: OilChangeLog,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dateStr = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale("id", "ID")).format(Date(log.date))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = dateStr,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Odo: ${formatKm(log.odometer)} KM",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Rp " + formatMoney(log.cost),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Hapus Log",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (log.oilBrand.isNotEmpty() || log.oilType.isNotEmpty() || log.notes.isNotEmpty()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (log.oilBrand.isNotEmpty() || log.oilType.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalGasStation,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = listOfNotNull(
                                    log.oilBrand.takeIf { it.isNotEmpty() },
                                    log.oilType.takeIf { it.isNotEmpty() }
                                ).joinToString(" "),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (log.notes.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp).padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = log.notes,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

fun formatMoney(number: Double): String {
    return try {
        NumberFormat.getNumberInstance(Locale("id", "ID")).format(number)
    } catch (e: Exception) {
        number.toString()
    }
}
