package com.masit.hub.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.masit.hub.data.AppState
import com.masit.hub.ui.component.AduanCardRiwayat
import com.masit.hub.ui.component.BottomNavTeknisi
import com.masit.hub.ui.component.EmptyState
import com.masit.hub.ui.component.RiwayatTopBar
import com.masit.hub.ui.component.SortDropdown
import com.masit.hub.ui.theme.*

@Composable
fun RiwayatTeknisiScreen(
    onHome: () -> Unit,
    onDetailAduan: (String) -> Unit,
    onProfil: () -> Unit
) {
    val selesaiList by remember { derivedStateOf { AppState.getSelesaiForTeknisi() } }

    var sortMode by remember { mutableStateOf("Terbaru") }
    val sortedList by remember {
        derivedStateOf {
            when (sortMode) {
                "Terlama" -> selesaiList.sortedBy { it.urutan }
                else      -> selesaiList.sortedByDescending { it.urutan }
            }
        }
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = { RiwayatTopBar(onProfil = onProfil) },
        bottomBar = { BottomNavTeknisi(selectedTab = 1, onAduan = onHome, onRiwayat = {}) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                SortDropdown(selected = sortMode, onSelected = { sortMode = it })
            }

            if (sortedList.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Outlined.History,
                        title = "Belum ada riwayat",
                        subtitle = "Aduan yang sudah Anda selesaikan\nakan tampil di sini"
                    )
                }
            } else {
                items(items = sortedList, key = { it.id }) { aduan ->
                    AduanCardRiwayat(
                        aduan = aduan,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onDetailAduan(aduan.id) }
                    )
                }
            }
        }
    }
}

@Preview(name = "Riwayat Teknisi Screen", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun RiwayatTeknisiScreenPreview() {
    MasITTheme { RiwayatTeknisiScreen(onHome = {}, onDetailAduan = {}, onProfil = {}) }
}
