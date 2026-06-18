package com.masit.hub.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masit.hub.data.AppState
import com.masit.hub.data.Aduan
import com.masit.hub.data.StatusAduan
import com.masit.hub.data.formatWaktuRelatif
import com.masit.hub.ui.component.BottomNavTeknisi
import com.masit.hub.ui.component.EmptyState
import com.masit.hub.ui.component.HomeTopBar
import com.masit.hub.ui.component.SortDropdown
import com.masit.hub.ui.component.StatusBadgeText
import com.masit.hub.ui.theme.*

@Composable
fun HomeTeknisiScreen(
    onKelolaAduan: (String) -> Unit,
    onRiwayat: () -> Unit,
    onProfil: () -> Unit
) {
    val aduanList by remember { derivedStateOf { AppState.getAduanForTeknisi() } }

    var sortMode by remember { mutableStateOf("Terbaru") }
    val sortedList by remember {
        derivedStateOf {
            when (sortMode) {
                "Terlama" -> aduanList.sortedBy { it.urutan }
                else      -> aduanList.sortedByDescending { it.urutan }
            }
        }
    }

    // Auto-refresh waktu relatif setiap 60 detik
    var tick by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(60_000L)
            tick = System.currentTimeMillis()
        }
    }

    Scaffold(
        containerColor = BackgroundLight,
        bottomBar = { BottomNavTeknisi(selectedTab = 0, onAduan = {}, onRiwayat = onRiwayat) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item { HomeTopBar(onProfil = onProfil) }

            item {
                Spacer(modifier = Modifier.height(14.dp))
                SortDropdown(
                    selected = sortMode,
                    onSelected = { sortMode = it },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (sortedList.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Filled.Inbox,
                        title = "Tidak ada aduan",
                        subtitle = "Semua aduan sudah tertangani"
                    )
                }
            } else {
                items(items = sortedList, key = { it.id }) { aduan ->
                    AduanTeknisiCard(
                        aduan = aduan,
                        tick = tick,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 10.dp),
                        onTangani = { AppState.tanganiAduan(aduan.id) },
                        onDetail = { onKelolaAduan(aduan.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun AduanTeknisiCard(
    aduan: Aduan,
    tick: Long,
    modifier: Modifier = Modifier,
    onTangani: () -> Unit,
    onDetail: () -> Unit
) {
    val waktuRelatif = remember(tick) {
        formatWaktuRelatif(aduan.createdAt)
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(0.8.dp, InputBorder)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(aduan.ticketNumber, fontSize = 11.sp, color = TextHint, fontWeight = FontWeight.Medium)
                StatusBadgeText(status = aduan.status)
            }
            Text(aduan.judul, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(
                text = "${aduan.pelapor} · ${aduan.lokasi} · $waktuRelatif",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            if (aduan.status == StatusAduan.MENUNGGU) {
                Button(
                    onClick = onTangani,
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentYellow, contentColor = PrimaryBlue),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Icon(Icons.Filled.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tangani Aduan", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                OutlinedButton(
                    onClick = onDetail,
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = AccentYellow.copy(alpha = 0.15f),
                        contentColor = PrimaryBlue
                    ),
                    border = BorderStroke(1.dp, AccentYellow)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Detail & Update", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Preview(name = "Home Teknisi Screen", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun HomeTeknisiScreenPreview() {
    MasITTheme { HomeTeknisiScreen(onKelolaAduan = {}, onRiwayat = {}, onProfil = {}) }
}
