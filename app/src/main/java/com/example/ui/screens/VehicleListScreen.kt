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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Vehicle
import com.example.viewmodel.OilReminderViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleListScreen(
    viewModel: OilReminderViewModel,
    onVehicleClick: (Vehicle) -> Unit,
    modifier: Modifier = Modifier
) {
    val vehicles by viewModel.vehicles.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    // Form states
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Motor") } // "Motor", "Mobil"
    var licensePlate by remember { mutableStateOf("") }
    var currentOdometer by remember { mutableStateOf("") }
    var lastOilChangeOdometer by remember { mutableStateOf("") }
    var oilIntervalKm by remember { mutableStateOf("2000") }
    var oilIntervalMonths by remember { mutableStateOf("3") }
    var oilTypeRecommendation by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            item {
                HeaderSection()
            }

            // Summary Card
            item {
                SummaryCard(vehicles = vehicles)
            }

            // Section Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kendaraan Anda",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    
                    TextButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("add_vehicle_text_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tambah")
                    }
                }
            }

            if (vehicles.isEmpty()) {
                item {
                    EmptyVehiclesState(onAddClick = { showAddDialog = true })
                }
            } else {
                items(vehicles, key = { it.id }) { vehicle ->
                    VehicleItemCard(
                        vehicle = vehicle,
                        onClick = { onVehicleClick(vehicle) }
                    )
                }
            }
            
            // Extra spacer for navigation bars padding
            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        // FAB to add vehicle
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
                .testTag("add_vehicle_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Tambah Kendaraan")
        }

        // Add Vehicle Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Tambah Kendaraan Baru", fontWeight = FontWeight.Bold) },
                text = {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Nama Kendaraan (Contoh: Honda Vario)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_vehicle_name"),
                                singleLine = true
                            )
                        }

                        item {
                            Text("Tipe Kendaraan", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = type == "Motor",
                                    onClick = {
                                        type = "Motor"
                                        oilIntervalKm = "2000"
                                        oilIntervalMonths = "3"
                                    },
                                    label = { Text("Motor") },
                                    leadingIcon = if (type == "Motor") {
                                        { Icon(Icons.Default.TwoWheeler, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                    } else null,
                                    modifier = Modifier.weight(1f).testTag("chip_motor")
                                )
                                FilterChip(
                                    selected = type == "Mobil",
                                    onClick = {
                                        type = "Mobil"
                                        oilIntervalKm = "10000"
                                        oilIntervalMonths = "6"
                                    },
                                    label = { Text("Mobil") },
                                    leadingIcon = if (type == "Mobil") {
                                        { Icon(Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                    } else null,
                                    modifier = Modifier.weight(1f).testTag("chip_mobil")
                                )
                            }
                        }

                        item {
                            OutlinedTextField(
                                value = licensePlate,
                                onValueChange = { licensePlate = it.uppercase() },
                                label = { Text("Nomor Plat Kendaraan (Contoh: B 1234 CD)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_license_plate"),
                                singleLine = true
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = currentOdometer,
                                onValueChange = { currentOdometer = it },
                                label = { Text("Odometer Saat Ini (KM)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_current_odo"),
                                singleLine = true
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = lastOilChangeOdometer,
                                onValueChange = { lastOilChangeOdometer = it },
                                label = { Text("Odometer Ganti Oli Terakhir (KM)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_last_change_odo"),
                                singleLine = true
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = oilIntervalKm,
                                onValueChange = { oilIntervalKm = it },
                                label = { Text("Interval Ganti Oli (KM)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_interval_km"),
                                singleLine = true
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = oilIntervalMonths,
                                onValueChange = { oilIntervalMonths = it },
                                label = { Text("Interval Ganti Oli (Bulan)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_interval_months"),
                                singleLine = true
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = oilTypeRecommendation,
                                onValueChange = { oilTypeRecommendation = it },
                                label = { Text("Rekomendasi Jenis Oli (Contoh: 10W-40)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_recommendation"),
                                singleLine = true
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                val curOdo = currentOdometer.toIntOrNull() ?: 0
                                val lastOdo = lastOilChangeOdometer.toIntOrNull() ?: 0
                                val intervalKmVal = oilIntervalKm.toIntOrNull() ?: 2000
                                val intervalMoVal = oilIntervalMonths.toIntOrNull() ?: 3

                                viewModel.addVehicle(
                                    name = name,
                                    type = type,
                                    licensePlate = licensePlate,
                                    currentOdometer = curOdo,
                                    lastOilChangeOdometer = if (lastOdo > curOdo) curOdo else lastOdo,
                                    lastOilChangeDate = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000), // default to 1 month ago
                                    oilIntervalKm = intervalKmVal,
                                    oilIntervalMonths = intervalMoVal,
                                    oilTypeRecommendation = oilTypeRecommendation
                                )

                                // Reset form and dismiss
                                name = ""
                                type = "Motor"
                                licensePlate = ""
                                currentOdometer = ""
                                lastOilChangeOdometer = ""
                                oilIntervalKm = "2000"
                                oilIntervalMonths = "3"
                                oilTypeRecommendation = ""
                                showAddDialog = false
                            }
                        },
                        enabled = name.isNotBlank(),
                        modifier = Modifier.testTag("dialog_save_button")
                    ) {
                        Text("Simpan")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

@Composable
fun HeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Gantolin",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Text(
                    text = "Aplikasi Pengingat Ganti Oli Online",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                )
            }
        }
    }
}

@Composable
fun SummaryCard(vehicles: List<Vehicle>) {
    val total = vehicles.size
    val needsAttention = vehicles.count { it.isDueForChange() }
    val safeCount = total - needsAttention

    val cardColor = if (needsAttention > 0) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }

    val onCardColor = if (needsAttention > 0) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("summary_card"),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ringkasan Kesehatan Kendaraan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = onCardColor
                )
                Icon(
                    imageVector = if (needsAttention > 0) Icons.Default.Warning else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (needsAttention > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SummaryStat(
                    value = total.toString(),
                    label = "Total Kendaraan",
                    textColor = onCardColor
                )
                SummaryStat(
                    value = needsAttention.toString(),
                    label = "Wajib Ganti Oli",
                    textColor = if (needsAttention > 0) MaterialTheme.colorScheme.error else onCardColor,
                    isImportant = needsAttention > 0
                )
                SummaryStat(
                    value = safeCount.toString(),
                    label = "Oli Aman",
                    textColor = onCardColor
                )
            }
            
            if (needsAttention > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Ada $needsAttention kendaraan yang memerlukan penggantian oli segera!",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryStat(
    value: String,
    label: String,
    textColor: Color,
    isImportant: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = textColor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isImportant) FontWeight.Bold else FontWeight.Normal,
            color = textColor.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun VehicleItemCard(
    vehicle: Vehicle,
    onClick: () -> Unit
) {
    val lifePercent = vehicle.getOilLifePercentage()
    val isDue = vehicle.isDueForChange()
    val remainingKm = vehicle.getRemainingKm()

    val statusText: String
    val statusColor: Color
    val progressColor: Color

    if (isDue) {
        statusText = "Wajib Ganti"
        statusColor = MaterialTheme.colorScheme.error
        progressColor = MaterialTheme.colorScheme.error
    } else if (lifePercent < 0.25f) {
        statusText = "Segera Ganti"
        statusColor = MaterialTheme.colorScheme.tertiary
        progressColor = MaterialTheme.colorScheme.tertiary
    } else {
        statusText = "Aman"
        statusColor = MaterialTheme.colorScheme.primary
        progressColor = MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("vehicle_card_${vehicle.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (vehicle.type == "Motor") Icons.Default.TwoWheeler else Icons.Default.DirectionsCar,
                            contentDescription = vehicle.type,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = vehicle.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = vehicle.licensePlate.ifEmpty { "Tanpa Plat" },
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Box(
                                modifier = Modifier
                                    .size(3.dp)
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant, shape = CircleShape)
                            )
                            Text(
                                text = vehicle.type,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Status Badge
                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mileage / Odometer Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Odometer",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${formatKm(vehicle.currentOdometer)} KM",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Ganti Berikutnya",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${formatKm(vehicle.lastOilChangeOdometer + vehicle.oilIntervalKm)} KM",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isDue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kesehatan Oli",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (isDue) {
                            "Wajib Ganti!"
                        } else {
                            "Tersisa ${formatKm(remainingKm)} KM (${(lifePercent * 100).toInt()}%)"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isDue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                LinearProgressIndicator(
                    progress = { lifePercent },
                    color = progressColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

@Composable
fun EmptyVehiclesState(onAddClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .testTag("empty_state_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                modifier = Modifier.size(72.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Belum Ada Kendaraan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tambahkan kendaraan pertama Anda untuk mulai memantau dan mendapatkan pengingat jadwal ganti oli.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
                lineHeight = 20.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("add_vehicle_empty_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tambah Kendaraan")
            }
        }
    }
}

fun formatKm(number: Int): String {
    return try {
        NumberFormat.getNumberInstance(Locale("id", "ID")).format(number)
    } catch (e: Exception) {
        number.toString()
    }
}
