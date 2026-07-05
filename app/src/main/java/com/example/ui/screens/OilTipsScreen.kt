package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OilTipsScreen(
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("Motor Matik") }

    // Educational Accordion open/close states
    var openQuestion1 by remember { mutableStateOf(false) }
    var openQuestion2 by remember { mutableStateOf(false) }
    var openQuestion3 by remember { mutableStateOf(false) }
    var openQuestion4 by remember { mutableStateOf(false) }

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
                TipsHeaderSection()
            }

            // Interactive Selector Section
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Rekomendasi Spesifikasi Oli",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Pilih tipe kendaraan untuk mengetahui SAE (kekentalan) & sertifikasi oli yang disarankan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))

                        // Category Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val categories = listOf("Motor Matik", "Motor Manual", "Mobil Bensin")
                            categories.forEach { category ->
                                FilterChip(
                                    selected = selectedCategory == category,
                                    onClick = { selectedCategory = category },
                                    label = { Text(category, fontSize = 12.sp) },
                                    modifier = Modifier.testTag("tips_chip_${category.lowercase().replace(" ", "_")}")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Recommendation Result Box
                        AnimatedContent(
                            targetState = selectedCategory,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            },
                            label = "RecomBox"
                        ) { category ->
                            RecommendationResult(category = category)
                        }
                    }
                }
            }

            // FAQ Section
            item {
                Text(
                    text = "Tanya Jawab & Edukasi",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                FaqItem(
                    question = "Kapan waktu terbaik mengganti oli?",
                    answer = "Idealnya untuk sepeda motor dilakukan setiap 2.000 - 3.000 KM atau maksimal 3 bulan sekali. Untuk mobil, ganti oli disarankan setiap 10.000 KM atau maksimal 6 bulan sekali. Meskipun kendaraan jarang dipakai, oli mesin harus tetap diganti berkala karena kualitas oli akan menurun seiring waktu (oksidasi).",
                    isOpen = openQuestion1,
                    onToggle = { openQuestion1 = !openQuestion1 },
                    tag = "faq_1"
                )
            }

            item {
                FaqItem(
                    question = "Apa arti kode SAE 10W-40 atau 0W-20?",
                    answer = "SAE (Society of Automotive Engineers) menunjukkan tingkat kekentalan oli. Huruf 'W' berarti Winter (Musim Dingin). Angka sebelum W menunjukkan tingkat kemudahan oli mengalir di suhu rendah (semakin kecil, semakin tahan dingin). Angka setelah W menunjukkan kekentalan oli pada suhu operasional mesin tinggi (semakin tinggi angkanya, semakin tebal lapisan olinya).",
                    isOpen = openQuestion2,
                    onToggle = { openQuestion2 = !openQuestion2 },
                    tag = "faq_2"
                )
            }

            item {
                FaqItem(
                    question = "Apa akibatnya jika sering terlambat ganti oli?",
                    answer = "Keterlambatan ganti oli menyebabkan gesekan antar komponen mesin sangat tinggi karena oli kehilangan kemampuan pelumasan. Ini memicu mesin cepat panas (overheat), suara berisik kasar, terbentuknya lumpur oli (oil sludge) yang menyumbat jalur oli, serta berpotensi merusak piston/piston ring yang membutuhkan turun mesin (overhaul) dengan biaya sangat mahal.",
                    isOpen = openQuestion3,
                    onToggle = { openQuestion3 = !openQuestion3 },
                    tag = "faq_3"
                )
            }

            item {
                FaqItem(
                    question = "Bagaimana cara mendeteksi oli sudah harus diganti?",
                    answer = "Buka penutup atau cabut dipstick oli di mesin. Lap dengan tisu bersih, lalu celupkan kembali. Perhatikan warna oli:\n\n1. Kuning kecokelatan transparan: Oli masih bagus.\n2. Hitam pekat namun cair: Oli sudah kotor, segera ganti.\n3. Hitam pekat dan kental berpasir: Oli sudah sangat buruk, wajib ganti secepatnya.\n4. Cokelat keputihan (seperti susu): Oli tercampur air radiator, harus segera dibawa ke bengkel.",
                    isOpen = openQuestion4,
                    onToggle = { openQuestion4 = !openQuestion4 },
                    tag = "faq_4"
                )
            }

            // Extra spacer for navigation bars padding
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun TipsHeaderSection() {
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
                                MaterialTheme.colorScheme.tertiary,
                                MaterialTheme.colorScheme.primary
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Panduan & Tips",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Text(
                    text = "Edukasi jenis pelumas dan cara merawat mesin kendaraan Anda",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                )
            }
        }
    }
}

@Composable
fun RecommendationResult(category: String) {
    val resultTitle: String
    val resultSae: String
    val resultCert: String
    val resultDesc: String

    when (category) {
        "Motor Matik" -> {
            resultTitle = "Skutik & Motor Matik (Scooter)"
            resultSae = "SAE 10W-30 atau 10W-40"
            resultCert = "API SL/SN + JASO MB"
            resultDesc = "JASO MB didesain khusus untuk mesin matik kopling kering. Oli JASO MB licin sempurna sehingga tidak boleh digunakan pada motor kopling basah (bebek/sport) karena bisa memicu kopling selip."
        }
        "Motor Manual" -> {
            resultTitle = "Motor Kopling Basah (Sport & Bebek)"
            resultSae = "SAE 10W-40 atau 20W-50"
            resultCert = "API SL/SN/SP + JASO MA/MA2"
            resultDesc = "Sertifikasi JASO MA/MA2 dirancang memiliki tingkat friksi/gesekan tinggi untuk mencegah slip kopling pada motor manual yang koplingnya terendam oli mesin."
        }
        else -> {
            resultTitle = "Mobil Penumpang Bensin Modern"
            resultSae = "SAE 0W-20 atau 5W-30"
            resultCert = "API SP + ILSAC GF-6"
            resultDesc = "Mobil bensin keluaran terbaru menggunakan konstruksi mesin presisi tinggi yang membutuhkan oli sangat encer (0W-20) untuk menghemat bahan bakar, mempercepat pelumasan saat dingin, serta melindungi turbocharger."
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = resultTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Rekomendasi Kekentalan (SAE)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = resultSae,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Sertifikasi Standard",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = resultCert,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = resultDesc,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun FaqItem(
    question: String,
    answer: String,
    isOpen: Boolean,
    onToggle: () -> Unit,
    tag: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .testTag(tag)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isOpen) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isOpen) "Tutup" else "Buka",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(
                visible = isOpen,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = answer,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}
