package com.masit.hub.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masit.hub.data.AppState
import com.masit.hub.data.StatusAduan
import com.masit.hub.ui.theme.*

@Composable
fun HomeTeknisiScreen(
    onKelolaAduan: (String) -> Unit,
    onRiwayat: () -> Unit,
    onProfil: () -> Unit
) {
    // derivedStateOf — reaktif, hanya recompute saat aduanList berubah
    val aduanList by remember {
        derivedStateOf { AppState.getAduanForTeknisi() }
    }

    var sortMode by remember { mutableStateOf("Terbaru") }
    val sortedList by remember {
        derivedStateOf {
            when (sortMode) {
                "Terlama" -> aduanList.sortedBy { it.urutan }
                else      -> aduanList.sortedByDescending { it.urutan }
            }
        }
    }

    Scaffold(
        containerColor = BackgroundLight,
        bottomBar = {
            BottomNavTeknisi(
                selectedTab = 0,
                onAduan = {},
                onRiwayat = onRiwayat
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // ── Top Bar ───────────────────────────────────────────────────────
            item {
                HomeTeknisiTopBar(onProfil = onProfil)
            }

            // ── Sort Dropdown ─────────────────────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(14.dp))
                SortDropdown(
                    selected = sortMode,
                    onSelected = { sortMode = it },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── List aduan atau empty state ───────────────────────────────────
            if (sortedList.isEmpty()) {
                item { EmptyAduan() }
            } else {
                items(items = sortedList, key = { it.id }) { aduan ->
                    AduanTeknisiCard(
                        aduan = aduan,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 10.dp),
                        onTangani = { AppState.tanganiAduan(aduan.id) }, // langsung ubah status, tidak navigasi
                        onDetail = { onKelolaAduan(aduan.id) }
                    )
                }
            }
        }
    }
}

// ─── Top Bar Teknisi ──────────────────────────────────────────────────────────
@Composable
fun HomeTeknisiTopBar(onProfil: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp, vertical = 14.dp),  // sama persis HomeUserScreen
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            MasItLogo(modifier = Modifier.size(28.dp), color = PrimaryBlue)
            Spacer(modifier = Modifier.width(8.dp))
            Text("MAS IT", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        }
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { onProfil() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Person, contentDescription = "Profil", tint = PrimaryBlue, modifier = Modifier.size(22.dp))
        }
    }
    HorizontalDivider(color = InputBorder, thickness = 0.8.dp)
}

// ─── Card Aduan Teknisi ───────────────────────────────────────────────────────
// Sesuai mockup halaman 6: ada tombol "Tangani Aduan" atau "Detail & Update"
@Composable
fun AduanTeknisiCard(
    aduan: com.masit.hub.data.Aduan,
    modifier: Modifier = Modifier,
    onTangani: () -> Unit,
    onDetail: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, InputBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Ticket + status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(aduan.ticketNumber, fontSize = 11.sp, color = TextHint, fontWeight = FontWeight.Medium)
                StatusBadgeText(status = aduan.status)
            }

            // Judul
            Text(aduan.judul, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

            // Pelapor + Lokasi
            Text(
                "${aduan.pelapor} · ${aduan.lokasi}",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Tombol aksi — sesuai status
            if (aduan.status == StatusAduan.MENUNGGU) {
                // Langsung ubah status tanpa navigasi
                Button(
                    onClick = onTangani,
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentYellow,
                        contentColor = PrimaryBlue
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Icon(Icons.Filled.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tangani Aduan", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                // Masuk ke KelolaAduan untuk update detail
                OutlinedButton(
                    onClick = onDetail,
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = AccentYellow.copy(alpha = 0.15f),
                        contentColor = PrimaryBlue
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentYellow)
                ) {
                    Icon(Icons.Filled.Assignment, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Detail & Update", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ─── Empty state ──────────────────────────────────────────────────────────────
@Composable
fun EmptyAduan() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Inbox,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = TextHint
            )
            Text(
                text = "Tidak ada aduan",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )
            Text(
                text = "Semua aduan sudah tertangani",
                fontSize = 13.sp,
                color = TextHint,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─── Bottom Nav Teknisi ───────────────────────────────────────────────────────
@Composable
fun BottomNavTeknisi(
    selectedTab: Int,       // 0 = Aduan Masuk, 1 = Riwayat
    onAduan: () -> Unit,
    onRiwayat: () -> Unit
) {
    NavigationBar(
        containerColor = PrimaryBlue,
        tonalElevation = 0.dp,
        modifier = Modifier.height(80.dp)
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = onAduan,
            icon = {
                Icon(
                    imageVector = Icons.Filled.Inbox,
                    contentDescription = "Aduan Masuk",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Aduan Masuk", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentYellow,
                selectedTextColor = AccentYellow,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = onRiwayat,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.History,
                    contentDescription = "Riwayat",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Riwayat", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentYellow,
                selectedTextColor = AccentYellow,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                indicatorColor = Color.Transparent
            )
        )
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────
@Preview(name = "Home Teknisi Screen", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun HomeTeknisiScreenPreview() {
    com.masit.hub.ui.theme.MasITTheme {
        HomeTeknisiScreen(
            onKelolaAduan = {},
            onRiwayat = {},
            onProfil = {}
        )
    }
}