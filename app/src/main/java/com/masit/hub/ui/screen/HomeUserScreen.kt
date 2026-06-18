package com.masit.hub.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.masit.hub.data.AppState
import com.masit.hub.data.StatusAduan
import com.masit.hub.ui.component.AduanCard
import com.masit.hub.ui.component.BottomNavUser
import com.masit.hub.ui.component.EmptyState
import com.masit.hub.ui.component.HomeTopBar
import com.masit.hub.ui.component.SortDropdown
import com.masit.hub.ui.component.StatCard
import com.masit.hub.ui.theme.*

@Composable
fun HomeUserScreen(
    onBuatAduan: () -> Unit,
    onDetailAduan: (String) -> Unit,
    onRiwayat: () -> Unit,
    onProfil: () -> Unit
) {
    val aduanAktif   by remember { derivedStateOf { AppState.getAduanForUser() } }
    val selesaiCount by remember { derivedStateOf { AppState.getSelesaiForUser().size } }
    val menungguCount  by remember { derivedStateOf { aduanAktif.count { it.status == StatusAduan.MENUNGGU } } }
    val ditanganiCount by remember { derivedStateOf { aduanAktif.count { it.status == StatusAduan.DITANGANI } } }

    var sortMode by remember { mutableStateOf("Terbaru") }
    val sortedList by remember {
        derivedStateOf {
            when (sortMode) {
                "Terlama" -> aduanAktif.sortedBy { it.urutan }
                else      -> aduanAktif.sortedByDescending { it.urutan }
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
        bottomBar = { BottomNavUser(selectedTab = 0, onHome = {}, onRiwayat = onRiwayat) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item { HomeTopBar(onProfil = onProfil) }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(modifier = Modifier.weight(1f), count = menungguCount,  label = "Menunggu")
                    StatCard(modifier = Modifier.weight(1f), count = ditanganiCount, label = "Ditangani")
                    StatCard(modifier = Modifier.weight(1f), count = selesaiCount,   label = "Selesai")
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onBuatAduan,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentYellow, contentColor = PrimaryBlue),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Buat Aduan Baru", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

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
                        title = "Belum ada aduan aktif",
                        subtitle = "Ketuk tombol di atas untuk\nmembuat aduan baru"
                    )
                }
            } else {
                items(items = sortedList, key = { it.id }) { aduan ->
                    AduanCard(
                        aduan = aduan,
                        tick = tick,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 10.dp),
                        onClick = { onDetailAduan(aduan.id) }
                    )
                }
            }
        }
    }
}

@Preview(name = "Home User Screen", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun HomeUserScreenPreview() {
    MasITTheme {
        HomeUserScreen(onBuatAduan = {}, onDetailAduan = {}, onRiwayat = {}, onProfil = {})
    }
}
