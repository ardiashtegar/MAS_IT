package com.masit.hub.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.masit.hub.data.AppState
import com.masit.hub.data.RiwayatUpdate
import com.masit.hub.data.StatusAduan
import com.masit.hub.ui.theme.*
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailAduanScreen(
    aduanId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Observe dari mutableStateListOf agar auto-refresh saat status berubah
    val aduan by remember {
        derivedStateOf { AppState.getAduanById(aduanId) }
    }

    // State konfirmasi batal
    var showKonfirmasiBatal by remember { mutableStateOf(false) }

    // State preview foto fullscreen
    var previewFotoUri by remember { mutableStateOf<String?>(null) }

    // Dialog konfirmasi batal
    if (showKonfirmasiBatal) {
        AlertDialog(
            onDismissRequest = { showKonfirmasiBatal = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text("Batalkan Aduan?", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextPrimary)
            },
            text = {
                Text(
                    "Aduan yang dibatalkan tidak dapat dikembalikan. Yakin ingin membatalkan?",
                    fontSize = 14.sp, color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = { AppState.batalkanAduan(aduanId); showKonfirmasiBatal = false; onBack() },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Ya, Batalkan", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showKonfirmasiBatal = false },
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, InputBorder)
                ) { Text("Tidak", color = TextPrimary) }
            }
        )
    }

    // Fullscreen foto preview dialog
    if (previewFotoUri != null) {
        Dialog(
            onDismissRequest = { previewFotoUri = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { previewFotoUri = null }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(previewFotoUri?.toUri())
                        .crossfade(false)
                        .build(),
                    contentDescription = "Preview foto",
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    contentScale = ContentScale.Fit
                )
                // Tombol tutup
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        .clickable { previewFotoUri = null },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Tutup",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // Aduan tidak ditemukan
    if (aduan == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detail Aduan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = PrimaryBlue)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
                )
            },
            containerColor = BackgroundLight
        ) { inner ->
            Box(modifier = Modifier.fillMaxSize().padding(inner), contentAlignment = Alignment.Center) {
                Text("Aduan tidak ditemukan", color = TextSecondary)
            }
        }
        return
    }

    val data = aduan!!

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text("Detail Aduan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = PrimaryBlue)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
                )
                HorizontalDivider(color = InputBorder, thickness = 0.8.dp)
            }
        },
        bottomBar = {
            // Tombol Batalkan — hanya muncul saat status MENUNGGU
            if (data.status == StatusAduan.MENUNGGU) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundLight)
                        .padding(horizontal = 16.dp)
                        .padding(top = 10.dp, bottom = 20.dp)
                ) {
                    Button(
                        onClick = { showKonfirmasiBatal = true },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DangerRed,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text("Batalkan Aduan", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Card Info Aduan ───────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = androidx.compose.foundation.BorderStroke(0.8.dp, InputBorder)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Ticket + Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(data.ticketNumber, fontSize = 11.sp, color = TextHint, fontWeight = FontWeight.Medium)
                        StatusBadgeText(status = data.status)
                    }

                    // Judul
                    Text(data.judul, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                    HorizontalDivider(color = InputBorder, thickness = 0.5.dp)

                    // Kategori + Lokasi
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DetailInfoItem(modifier = Modifier.weight(1f), label = "KATEGORI", value = data.kategori.label)
                        DetailInfoItem(modifier = Modifier.weight(1f), label = "LOKASI", value = data.lokasi)
                    }

                    // Deskripsi
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("DESKRIPSI", fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.8.sp, color = TextSecondary)
                        Text(data.deskripsi, fontSize = 13.sp, color = TextPrimary, lineHeight = 20.sp)
                    }

                    // ── Lampiran Foto ─────────────────────────────────────────
                    if (data.fotoUris.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("LAMPIRAN", fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.8.sp, color = TextSecondary)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                itemsIndexed(data.fotoUris) { _, uriStr ->
                                    FotoLampiranItem(uriStr = uriStr, onClick = { previewFotoUri = uriStr })
                                }
                            }
                        }
                    }
                }
            }

            // ── Riwayat Update ────────────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                Text(
                    text = "RIWAYAT UPDATE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                data.riwayatUpdate.forEachIndexed { index, riwayat ->
                    RiwayatItem(
                        riwayat = riwayat,
                        isFirst = index == 0,
                        isLast = index == data.riwayatUpdate.lastIndex
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

// ─── Detail Info Item (label + value) ────────────────────────────────────────
@Composable
fun DetailInfoItem(modifier: Modifier = Modifier, label: String, value: String) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp, color = TextSecondary)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}

// ─── Foto Lampiran Item — kotak kecil, klik untuk preview fullscreen ──────────
@Composable
fun FotoLampiranItem(uriStr: String, onClick: () -> Unit) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, InputBorder, RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(uriStr.toUri())
                .size(160, 160)   // 2x ukuran tampil
                .crossfade(false)
                .build(),
            contentDescription = "Lampiran foto",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

// ─── Riwayat Item (timeline style) ───────────────────────────────────────────
@Composable
fun RiwayatItem(riwayat: RiwayatUpdate, isFirst: Boolean, isLast: Boolean) {
    val dotColor = if (isFirst) PrimaryBlue else Color(0xFFCCCCCC)

    Row(modifier = Modifier.fillMaxWidth()) {
        // Timeline kolom kiri: garis + dot
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp)
        ) {
            // Garis atas (tidak tampil untuk item pertama)
            if (!isFirst) {
                Box(modifier = Modifier.width(2.dp).height(8.dp).background(Color(0xFFE0E0E0)))
            } else {
                Spacer(modifier = Modifier.height(4.dp))
            }
            // Dot
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(dotColor, CircleShape)
            )
            // Garis bawah (tidak tampil untuk item terakhir)
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(Color(0xFFE0E0E0))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Konten kanan
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 4.dp)
        ) {
            Text(
                text = "Status diubah: ${riwayat.status.label}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            if (riwayat.catatan.isNotBlank() && riwayat.catatan != "-") {
                Text("Catatan: ${riwayat.catatan}", fontSize = 12.sp, color = TextSecondary)
            } else {
                Text("Catatan: -", fontSize = 12.sp, color = TextSecondary)
            }
            Text(riwayat.waktu, fontSize = 11.sp, color = TextHint)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────
@Preview(name = "Detail Aduan Screen", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun DetailAduanScreenPreview() {
    MasITTheme {
        DetailAduanScreen(aduanId = "a001", onBack = {})
    }
}
