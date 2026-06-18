package com.masit.hub.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.masit.hub.data.AppState
import com.masit.hub.data.KategoriAduan
import com.masit.hub.ui.component.FormField
import com.masit.hub.ui.component.KonfirmasiDialog
import com.masit.hub.ui.component.PrimaryButton
import com.masit.hub.ui.component.outlinedFieldColors
import com.masit.hub.ui.theme.*

private const val MAX_FOTO = 3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuatAduanScreen(
    onBack: () -> Unit,
    onSubmit: () -> Unit
) {
    var judul            by remember { mutableStateOf("") }
    var selectedKategori by remember { mutableStateOf(KategoriAduan.JARINGAN) }
    var lokasi           by remember { mutableStateOf("") }
    var deskripsi        by remember { mutableStateOf("") }
    val fotoUris = remember { mutableStateListOf<Uri>() }

    var judulError     by remember { mutableStateOf(false) }
    var lokasiError    by remember { mutableStateOf(false) }
    var deskripsiError by remember { mutableStateOf(false) }
    var showSuccess    by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = MAX_FOTO)
    ) { uris ->
        val sisaSlot = MAX_FOTO - fotoUris.size
        uris.take(sisaSlot).forEach { fotoUris.add(it) }
    }

    fun pickPhoto() {
        if (fotoUris.size < MAX_FOTO)
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    if (showSuccess) {
        KonfirmasiDialog(
            title = "Aduan Terkirim!",
            message = "Aduan Anda telah berhasil dikirim dan sedang menunggu penanganan dari tim IT.",
            confirmLabel = "OK",
            onConfirm = { showSuccess = false; onSubmit() }
        )
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Buat Aduan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue) },
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
            Column(
                modifier = Modifier.fillMaxWidth().background(BackgroundLight)
                    .padding(horizontal = 16.dp).padding(top = 10.dp, bottom = 20.dp)
            ) {
                PrimaryButton(
                    text = "Kirim Aduan",
                    icon = Icons.AutoMirrored.Filled.Send,
                    onClick = {
                        judulError     = judul.isBlank()
                        lokasiError    = lokasi.isBlank()
                        deskripsiError = deskripsi.isBlank()
                        if (!judulError && !lokasiError && !deskripsiError) {
                            AppState.tambahAduan(
                                judul    = judul.trim(),
                                kategori = selectedKategori,
                                lokasi   = lokasi.trim(),
                                deskripsi = deskripsi.trim(),
                                fotoUris = fotoUris.map { it.toString() }
                            )
                            showSuccess = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
                .verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            FormField(label = "JUDUL GANGGUAN") {
                OutlinedTextField(
                    value = judul,
                    onValueChange = { judul = it; judulError = false },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Deskripsikan gangguan secara singkat...", color = TextHint, fontSize = 13.sp) },
                    singleLine = true,
                    isError = judulError,
                    supportingText = if (judulError) {{ Text("Judul tidak boleh kosong", color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }} else null,
                    shape = RoundedCornerShape(10.dp),
                    colors = outlinedFieldColors()
                )
            }

            FormField(label = "KATEGORI") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        KategoriButton(KategoriAduan.JARINGAN, Icons.Filled.Router,      selectedKategori, Modifier.weight(1f)) { selectedKategori = it }
                        KategoriButton(KategoriAduan.HARDWARE, Icons.Filled.PhoneAndroid, selectedKategori, Modifier.weight(1f)) { selectedKategori = it }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        KategoriButton(KategoriAduan.SOFTWARE, Icons.Filled.Terminal,  selectedKategori, Modifier.weight(1f)) { selectedKategori = it }
                        KategoriButton(KategoriAduan.LAINNYA,  Icons.Filled.MoreHoriz, selectedKategori, Modifier.weight(1f)) { selectedKategori = it }
                    }
                }
            }

            FormField(label = "LOKASI") {
                OutlinedTextField(
                    value = lokasi,
                    onValueChange = { lokasi = it; lokasiError = false },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Lantai / Ruangan", color = TextHint, fontSize = 13.sp) },
                    singleLine = true,
                    isError = lokasiError,
                    supportingText = if (lokasiError) {{ Text("Lokasi tidak boleh kosong", color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }} else null,
                    shape = RoundedCornerShape(10.dp),
                    colors = outlinedFieldColors()
                )
            }

            FormField(label = "DESKRIPSI") {
                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it; deskripsiError = false },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                    placeholder = { Text("Jelaskan gangguan secara detail...", color = TextHint, fontSize = 13.sp) },
                    singleLine = false,
                    maxLines = 5,
                    isError = deskripsiError,
                    supportingText = if (deskripsiError) {{ Text("Deskripsi tidak boleh kosong", color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }} else null,
                    shape = RoundedCornerShape(10.dp),
                    colors = outlinedFieldColors()
                )
            }

            FormField(label = "UPLOAD FOTO (OPSIONAL · MAKS. $MAX_FOTO)") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (fotoUris.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            itemsIndexed(fotoUris) { index, uri ->
                                FotoPreviewItem(uri = uri, onRemove = { fotoUris.removeAt(index) })
                            }
                            if (fotoUris.size < MAX_FOTO) {
                                item { TambahFotoKecil(onClick = { pickPhoto() }) }
                            }
                        }
                    } else {
                        FotoUploadArea(onClick = { pickPhoto() })
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun KategoriButton(
    kategori: KategoriAduan,
    icon: ImageVector,
    selected: KategoriAduan,
    modifier: Modifier = Modifier,
    onClick: (KategoriAduan) -> Unit
) {
    val isSelected = kategori == selected
    OutlinedButton(
        onClick = { onClick(kategori) },
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) PrimaryBlue else Color.White,
            contentColor   = if (isSelected) Color.White else TextPrimary
        ),
        border = BorderStroke(if (isSelected) 0.dp else 1.dp, if (isSelected) Color.Transparent else CategoryBorder),
        contentPadding = PaddingValues(horizontal = 10.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = if (isSelected) Color.White else PrimaryBlue)
        Spacer(modifier = Modifier.width(6.dp))
        Text(kategori.label, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
    }
}

@Composable
private fun FotoUploadArea(onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().height(130.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFEEEEF4))
            .border(BorderStroke(1.dp, Color(0xFFCCCCDD)), RoundedCornerShape(10.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(32.dp), tint = TextHint)
            Text("Ketuk untuk pilih foto", fontSize = 13.sp, color = TextSecondary)
            Text("JPG, PNG · maks. 5 MB per foto", fontSize = 11.sp, color = TextHint)
        }
    }
}

@Composable
private fun FotoPreviewItem(uri: Uri, onRemove: () -> Unit) {
    val context = LocalContext.current
    Box(
        modifier = Modifier.size(110.dp).clip(RoundedCornerShape(10.dp))
            .border(1.dp, InputBorder, RoundedCornerShape(10.dp))
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context).data(uri).size(220, 220).crossfade(false).build(),
            contentDescription = "Foto aduan",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(22.dp)
                .background(Color.White, CircleShape).clickable { onRemove() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Close, contentDescription = "Hapus foto", tint = Color.Black, modifier = Modifier.size(13.dp))
        }
    }
}

@Composable
private fun TambahFotoKecil(onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(110.dp).clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFEEEEF4))
            .border(BorderStroke(1.dp, Color(0xFFCCCCDD)), RoundedCornerShape(10.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(Icons.Filled.AddPhotoAlternate, contentDescription = "Tambah foto", tint = TextHint, modifier = Modifier.size(26.dp))
            Text("Tambah", fontSize = 11.sp, color = TextSecondary)
        }
    }
}

@Preview(name = "Buat Aduan Screen", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun BuatAduanScreenPreview() {
    MasITTheme { BuatAduanScreen(onBack = {}, onSubmit = {}) }
}
