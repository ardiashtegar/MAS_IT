package com.masit.hub.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import com.masit.hub.ui.theme.*

@Composable
fun RiwayatUserScreen(
    onHome: () -> Unit,
    onDetailAduan: (String) -> Unit,
    onProfil: () -> Unit
) {
    // Hanya aduan SELESAI milik user yang sedang login
    val selesaiList by remember {
        derivedStateOf { AppState.getSelesaiForUser() }
    }

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
        topBar = {
            Column {
                // ── Top Bar ───────────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundLight)
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Riwayat Selesai",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    // Profil icon button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable { onProfil() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profil",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                HorizontalDivider(color = InputBorder, thickness = 0.8.dp)
            }
        },
        bottomBar = {
            // Tab Riwayat aktif (selectedTab = 1)
            BottomNavUser(
                selectedTab = 1,
                onHome = onHome,
                onRiwayat = {}
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = 14.dp, bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ── Sort Dropdown ─────────────────────────────────────────────────
            item {
                SortDropdown(
                    selected = sortMode,
                    onSelected = { sortMode = it }
                )
            }

            // ── List atau Empty State ─────────────────────────────────────────
            if (sortedList.isEmpty()) {
                item { EmptyRiwayat() }
            } else {
                items(items = sortedList, key = { it.id }) { aduan ->
                    AduanCard(
                        aduan = aduan,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onDetailAduan(aduan.id) }
                    )
                }
            }
        }
    }
}

// ─── Empty state ──────────────────────────────────────────────────────────────
@Composable
fun EmptyRiwayat() {
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
                imageVector = Icons.Outlined.History,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = TextHint
            )
            Text(
                text = "Belum ada riwayat",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )
            Text(
                text = "Aduan yang sudah selesai\nakan tampil di sini",
                fontSize = 13.sp,
                color = TextHint,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────
@Preview(name = "Riwayat User Screen", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun RiwayatUserScreenPreview() {
    MasITTheme {
        RiwayatUserScreen(
            onHome = {},
            onDetailAduan = {},
            onProfil = {}
        )
    }
}
