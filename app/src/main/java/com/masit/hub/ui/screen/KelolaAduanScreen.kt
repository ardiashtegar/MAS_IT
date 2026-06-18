package com.masit.hub.ui.screen

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Save
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
import com.masit.hub.ui.component.DetailInfoItem
import com.masit.hub.ui.component.FotoLampiranItem
import com.masit.hub.ui.component.KonfirmasiDialog
import com.masit.hub.ui.component.PrimaryButton
import com.masit.hub.ui.component.outlinedFieldColors
import com.masit.hub.ui.theme.*
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KelolaAduanScreen(
    aduanId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val aduan by remember { derivedStateOf { AppState.getAduanById(aduanId) } }

    var selectedStatus  by remember { mutableStateOf(aduan?.status ?: StatusAduan.DITANGANI) }
    var catatan         by remember { mutableStateOf("") }
    var catatanError    by remember { mutableStateOf(false) }
    var showSuccess     by remember { mutableStateOf(false) }
    var previewFotoUri  by remember { mutableStateOf<String?>(null) }

    if (showSuccess) {
        KonfirmasiDialog(
            title = "Perubahan Disimpan!",
            message = "Status aduan berhasil diperbarui. Pelapor akan melihat perubahan ini secara langsung.",
            confirmLabel = "OK",
            onConfirm = { showSuccess = false; onBack() }
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
                    title = { Text("Kelola Aduan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue) },
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
                    title = { Text("Kelola Aduan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = PrimaryBlue) } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
                )
                HorizontalDivider(color = InputBorder, thickness = 0.8.dp)
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth().background(BackgroundLight)
                    .padding(horizontal = 16.dp).padding(top = 10.dp, bottom = 20.dp)
            ) {
                PrimaryButton(
                    text = "Simpan Perubahan",
                    icon = Icons.Filled.Save,
                    onClick = {
                        if (selectedStatus == StatusAduan.BATAL && catatan.isBlank()) {
                            catatanError = true; return@PrimaryButton
                        }
                        catatanError = false
                        AppState.updateAduan(id = aduanId, statusBaru = selectedStatus, catatan = catatan)
                        catatan = ""
                        showSuccess = true
                    }
                )
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
                border = BorderStroke(0.8.dp, InputBorder)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(data.ticketNumber, fontSize = 11.sp, color = TextHint, fontWeight = FontWeight.Medium)
                    Text(data.judul, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Pelapor: ${data.pelapor}", fontSize = 13.sp, color = TextSecondary)
                    HorizontalDivider(color = InputBorder, thickness = 0.5.dp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        DetailInfoItem(Modifier.weight(1f), "KATEGORI", data.kategori.label)
                        DetailInfoItem(Modifier.weight(1f), "LOKASI",   data.lokasi)
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

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("UBAH STATUS", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.8.sp, color = TextSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(StatusAduan.DITANGANI, StatusAduan.SELESAI, StatusAduan.BATAL).forEach { status ->
                        StatusPilihButton(
                            label = status.label,
                            selected = selectedStatus == status,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedStatus = status; if (status != StatusAduan.BATAL) catatanError = false }
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("CATATAN TEKNISI", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.8.sp, color = TextSecondary)
                    if (selectedStatus == StatusAduan.BATAL) {
                        Text("  *wajib diisi jika Batal", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                    }
                }
                OutlinedTextField(
                    value = catatan,
                    onValueChange = { catatan = it; if (catatanError) catatanError = false },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                    placeholder = {
                        Text(
                            if (selectedStatus == StatusAduan.BATAL) "Tuliskan alasan pembatalan..." else "Tambahkan catatan penanganan...",
                            color = TextHint, fontSize = 13.sp
                        )
                    },
                    isError = catatanError,
                    supportingText = if (catatanError) {{ Text("Catatan wajib diisi untuk status Batal", color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }} else null,
                    singleLine = false,
                    maxLines = 5,
                    shape = RoundedCornerShape(10.dp),
                    colors = outlinedFieldColors()
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun StatusPilihButton(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(42.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) PrimaryBlue else Color.White,
            contentColor   = if (selected) Color.White else TextPrimary
        ),
        border = BorderStroke(if (selected) 0.dp else 1.dp, if (selected) Color.Transparent else CategoryBorder)
    ) {
        Text(label, fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Preview(name = "Kelola Aduan Screen", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun KelolaAduanScreenPreview() {
    MasITTheme { KelolaAduanScreen(aduanId = "a001", onBack = {}) }
}
