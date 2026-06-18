package com.masit.hub.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.masit.hub.data.StatusAduan
import com.masit.hub.ui.component.KonfirmasiDialog
import com.masit.hub.ui.theme.*
import androidx.core.net.toUri
import com.masit.hub.ui.component.DetailInfoItem
import com.masit.hub.ui.component.FotoLampiranItem
import com.masit.hub.ui.component.PrimaryButton
import com.masit.hub.ui.component.RiwayatItem
import com.masit.hub.ui.component.StatusBadgeText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailAduanScreen(
    aduanId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val aduan by remember { derivedStateOf { AppState.getAduanById(aduanId) } }

    var showBatalDialog by remember { mutableStateOf(false) }
    var showHapusDialog by remember { mutableStateOf(false) }
    var previewFotoUri  by remember { mutableStateOf<String?>(null) }

    if (showBatalDialog) {
        KonfirmasiDialog(
            title = "Batalkan Aduan?",
            message = "Aduan yang dibatalkan tidak dapat dikembalikan. Yakin ingin membatalkan?",
            confirmLabel = "Ya, Batalkan",
            confirmColor = DangerRed,
            confirmTextColor = Color.White,
            onConfirm = { AppState.batalkanAduan(aduanId); showBatalDialog = false; onBack() },
            onDismiss = { showBatalDialog = false }
        )
    }

    if (showHapusDialog) {
        KonfirmasiDialog(
            title = "Hapus Aduan?",
            message = "Aduan yang dihapus tidak bisa dikembalikan. Yakin ingin menghapus aduan ini?",
            confirmLabel = "Ya, Hapus",
            confirmColor = DangerRed,
            confirmTextColor = Color.White,
            onConfirm = { AppState.hapusAduan(aduanId); showHapusDialog = false; onBack() },
            onDismiss = { showHapusDialog = false }
        )
    }

    if (previewFotoUri != null) {
        Dialog(onDismissRequest = { previewFotoUri = null }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black).clickable { previewFotoUri = null },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(previewFotoUri?.toUri()).crossfade(false).build(),
                    contentDescription = "Preview foto",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
                Box(
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                        .size(36.dp).background(Color.Black.copy(alpha = 0.6f), androidx.compose.foundation.shape.CircleShape)
                        .clickable { previewFotoUri = null },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Tutup", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }

    if (aduan == null) {
        Scaffold(
            containerColor = BackgroundLight,
            topBar = {
                TopAppBar(
                    title = { Text("Detail Aduan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = PrimaryBlue) } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
                )
            }
        ) { inner ->
            Box(Modifier.fillMaxSize().padding(inner), Alignment.Center) { Text("Aduan tidak ditemukan", color = TextSecondary) }
        }
        return
    }

    val data = aduan!!

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Detail Aduan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = PrimaryBlue) } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
                )
                HorizontalDivider(color = InputBorder, thickness = 0.8.dp)
            }
        },
        bottomBar = {
            when (data.status) {
                StatusAduan.MENUNGGU -> {
                    Box(modifier = Modifier.fillMaxWidth().background(BackgroundLight).padding(horizontal = 16.dp).padding(top = 10.dp, bottom = 20.dp)) {
                        PrimaryButton(
                            text = "Batalkan Aduan",
                            onClick = { showBatalDialog = true },
                            containerColor = DangerRed,
                            contentColor = Color.White
                        )
                    }
                }
                StatusAduan.BATAL -> {
                    Box(modifier = Modifier.fillMaxWidth().background(BackgroundLight).padding(horizontal = 16.dp).padding(top = 10.dp, bottom = 20.dp)) {
                        PrimaryButton(
                            text = "Hapus Aduan",
                            onClick = { showHapusDialog = true },
                            containerColor = DangerRed,
                            contentColor = Color.White
                        )
                    }
                }
                else -> {}
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = androidx.compose.foundation.BorderStroke(0.8.dp, InputBorder)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(data.ticketNumber, fontSize = 11.sp, color = TextHint, fontWeight = FontWeight.Medium)
                        StatusBadgeText(status = data.status)
                    }
                    Text(data.judul, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    HorizontalDivider(color = InputBorder, thickness = 0.5.dp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        DetailInfoItem(modifier = Modifier.weight(1f), label = "KATEGORI", value = data.kategori.label)
                        DetailInfoItem(modifier = Modifier.weight(1f), label = "LOKASI",   value = data.lokasi)
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("DESKRIPSI", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.8.sp, color = TextSecondary)
                        Text(data.deskripsi, fontSize = 13.sp, color = TextPrimary, lineHeight = 20.sp)
                    }
                    if (data.fotoUris.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("LAMPIRAN", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.8.sp, color = TextSecondary)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                itemsIndexed(data.fotoUris) { _, uriStr ->
                                    FotoLampiranItem(uriStr = uriStr, onClick = { previewFotoUri = uriStr })
                                }
                            }
                        }
                    }
                }
            }

            Column {
                Text("RIWAYAT UPDATE", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.8.sp, color = TextSecondary, modifier = Modifier.padding(bottom = 12.dp))
                data.riwayatUpdate.forEachIndexed { index, riwayat ->
                    RiwayatItem(
                        riwayat = riwayat,
                        isFirst = index == 0,
                        isLast  = index == data.riwayatUpdate.lastIndex
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Preview(name = "Detail Aduan Screen", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun DetailAduanScreenPreview() {
    MasITTheme { DetailAduanScreen(aduanId = "a001", onBack = {}) }
}
