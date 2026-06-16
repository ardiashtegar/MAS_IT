package com.masit.hub.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masit.hub.R
import com.masit.hub.data.*
import com.masit.hub.ui.theme.*

@Composable
fun HomeUserScreen(
    onBuatAduan: () -> Unit,
    onDetailAduan: (String) -> Unit,
    onRiwayat: () -> Unit,
    onProfil: () -> Unit
) {
    // derivedStateOf — hanya recompute saat aduanList benar-benar berubah,
    // bukan setiap recompose. Ini mencegah recompose berlebihan.
    val aduanAktif by remember {
        derivedStateOf { AppState.getAduanForUser() }
    }
    val selesaiCount by remember {
        derivedStateOf { AppState.getSelesaiForUser().size }
    }
    val menungguCount by remember {
        derivedStateOf { aduanAktif.count { it.status == StatusAduan.MENUNGGU } }
    }
    val ditanganiCount by remember {
        derivedStateOf { aduanAktif.count { it.status == StatusAduan.DITANGANI } }
    }

    var sortMode by remember { mutableStateOf("Terbaru") }
    val sortedList by remember {
        derivedStateOf {
            when (sortMode) {
                "Terlama" -> aduanAktif.sortedBy { it.urutan }
                else      -> aduanAktif.sortedByDescending { it.urutan }
            }
        }
    }

    Scaffold(
        containerColor = BackgroundLight,
        bottomBar = {
            BottomNavUser(
                selectedTab = 0,
                onHome = {},
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
                HomeUserTopBar(onProfil = onProfil)
            }

            // ── Stat Cards ────────────────────────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(modifier = Modifier.weight(1f), count = menungguCount,  label = "Menunggu")
                    StatCard(modifier = Modifier.weight(1f), count = ditanganiCount, label = "Ditangani")
                    StatCard(modifier = Modifier.weight(1f), count = selesaiCount,   label = "Selesai")
                }
            }

            // ── Buat Aduan Button ─────────────────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onBuatAduan,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentYellow,
                        contentColor = PrimaryBlue
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Buat Aduan Baru", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            // ── Sort Dropdown — kirim state ke bawah ─────────────────────────
            item {
                Spacer(modifier = Modifier.height(14.dp))
                SortDropdown(
                    selected = sortMode,
                    onSelected = { sortMode = it },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── Aduan List atau Empty State ───────────────────────────────────
            if (sortedList.isEmpty()) {
                item { EmptyAduanUser() }
            } else {
                items(sortedList) { aduan ->
                    AduanCard(
                        aduan = aduan,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 10.dp),
                        onClick = { onDetailAduan(aduan.id) }
                    )
                }
            }
        }
    }
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────
@Composable
fun HomeUserTopBar(onProfil: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_masit_logo),
                contentDescription = "Logo MAS IT",
                modifier = Modifier.size(28.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "MAS IT", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        }
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { onProfil() },
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Filled.Person, contentDescription = "Profil", tint = PrimaryBlue, modifier = Modifier.size(22.dp))
        }
    }
    HorizontalDivider(color = InputBorder, thickness = 0.8.dp)
}

// ─── Stat Card ────────────────────────────────────────────────────────────────
@Composable
fun StatCard(modifier: Modifier = Modifier, count: Int, label: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, InputBorder)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count.toString(), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = label, fontSize = 12.sp, color = TextSecondary)
        }
    }
}

// ─── Sort Dropdown — terima selected & onSelected dari luar ───────────────────
@Composable
fun SortDropdown(
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Terbaru", "Terlama")

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = !expanded },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = TextPrimary
            ),
            border = androidx.compose.foundation.BorderStroke(0.8.dp, InputBorder),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(text = selected, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(18.dp), tint = TextPrimary)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            fontSize = 13.sp,
                            fontWeight = if (option == selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (option == selected) PrimaryBlue else TextPrimary
                        )
                    },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ─── Aduan Card ───────────────────────────────────────────────────────────────
@Composable
fun AduanCard(aduan: Aduan, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, InputBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = aduan.ticketNumber, fontSize = 11.sp, color = TextHint, fontWeight = FontWeight.Medium)
                StatusBadgeText(status = aduan.status)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = aduan.judul, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${aduan.kategori.label} • ${aduan.waktu}", fontSize = 12.sp, color = TextSecondary)
        }
    }
}

// ─── Status Badge Text ────────────────────────────────────────────────────────
@Composable
fun StatusBadgeText(status: StatusAduan) {
    val (text, color) = when (status) {
        StatusAduan.MENUNGGU  -> "Menunggu"  to StatusMenunggu
        StatusAduan.DITANGANI -> "Ditangani" to StatusDitangani
        StatusAduan.SELESAI   -> "Selesai"   to StatusSelesai
        StatusAduan.BATAL     -> "Batal"     to StatusBatal
    }
    Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = color)
}

// ─── Bottom Nav User ──────────────────────────────────────────────────────────
@Composable
fun BottomNavUser(
    selectedTab: Int,
    onHome: () -> Unit,
    onRiwayat: () -> Unit
) {
    NavigationBar(
        containerColor = PrimaryBlue,
        tonalElevation = 0.dp,
        modifier = Modifier.height(80.dp)
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = onHome,
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text(text = "Home", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
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
            label = { Text(text = "Riwayat", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
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

// ─── Empty State User ─────────────────────────────────────────────────────────
@Composable
fun EmptyAduanUser() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
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
                text = "Belum ada aduan aktif",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )
            Text(
                text = "Ketuk tombol di atas untuk\nmembuat aduan baru",
                fontSize = 13.sp,
                color = TextHint,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────
@Preview(name = "Home User Screen", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun HomeUserScreenPreview() {
    MasITTheme {
        HomeUserScreen(
            onBuatAduan = {},
            onDetailAduan = {},
            onRiwayat = {},
            onProfil = {}
        )
    }
}