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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.masit.hub.data.AppState
import com.masit.hub.data.KategoriAduan
import com.masit.hub.ui.theme.*

private const val MAX_FOTO = 3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuatAduanScreen(
    onBack: () -> Unit,
    onSubmit: () -> Unit
) {
    // ── Form state ─────────────────────────────────────────────────────────────
    var judul by remember { mutableStateOf("") }
    var selectedKategori by remember { mutableStateOf(KategoriAduan.JARINGAN) }
    var lokasi by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    val fotoUris = remember { mutableStateListOf<Uri>() }

    // ── Validation state ───────────────────────────────────────────────────────
    var judulError by remember { mutableStateOf(false) }
    var lokasiError by remember { mutableStateOf(false) }
    var deskripsiError by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // ── Photo picker — pakai Android Photo Picker (tidak perlu permission) ──────
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = MAX_FOTO)
    ) { uris: List<Uri> ->
        val sisaSlot = MAX_FOTO - fotoUris.size
        uris.take(sisaSlot).forEach { fotoUris.add(it) }
    }

    fun pickPhoto() {
        if (fotoUris.size >= MAX_FOTO) return
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    // ── Success dialog ─────────────────────────────────────────────────────────
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text("Aduan Terkirim!", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
            },
            text = {
                Text(
                    "Aduan Anda telah berhasil dikirim dan sedang menunggu penanganan dari tim IT.",
                    fontSize = 14.sp, color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = { showSuccessDialog = false; onSubmit() },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentYellow, contentColor = PrimaryBlue),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("OK", fontWeight = FontWeight.Bold) }
            }
        )
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text("Buat Aduan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
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
            // ── Kirim Aduan button + space bawah ─────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundLight)
                    .padding(horizontal = 16.dp)
                    .padding(top = 10.dp, bottom = 20.dp)   // space bawah 20dp
            ) {
                Button(
                    onClick = {
                        judulError = judul.isBlank()
                        lokasiError = lokasi.isBlank()
                        deskripsiError = deskripsi.isBlank()
                        if (!judulError && !lokasiError && !deskripsiError) {
                            AppState.tambahAduan(
                                judul = judul.trim(),
                                kategori = selectedKategori,
                                lokasi = lokasi.trim(),
                                deskripsi = deskripsi.trim(),
                                fotoUris = fotoUris.map { it.toString() }
                            )
                            showSuccessDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentYellow,
                        contentColor = PrimaryBlue
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    // Icon pesawat kertas (Send) — dari Material Icons, serasi
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kirim Aduan", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ── JUDUL GANGGUAN ────────────────────────────────────────────────
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

            // ── KATEGORI ──────────────────────────────────────────────────────
            FormField(label = "KATEGORI") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Router → ikon jaringan Material, serasi dgn ikon lain
                        KategoriButton(
                            label = "Jaringan",
                            icon = Icons.Filled.Router,
                            selected = selectedKategori == KategoriAduan.JARINGAN,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedKategori = KategoriAduan.JARINGAN }
                        )
                        // PhoneAndroid → mewakili perangkat keras
                        KategoriButton(
                            label = "Hardware",
                            icon = Icons.Filled.PhoneAndroid,
                            selected = selectedKategori == KategoriAduan.HARDWARE,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedKategori = KategoriAduan.HARDWARE }
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Terminal → ikon software / kode
                        KategoriButton(
                            label = "Software",
                            icon = Icons.Filled.Terminal,
                            selected = selectedKategori == KategoriAduan.SOFTWARE,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedKategori = KategoriAduan.SOFTWARE }
                        )
                        KategoriButton(
                            label = "Lainnya",
                            icon = Icons.Filled.MoreHoriz,
                            selected = selectedKategori == KategoriAduan.LAINNYA,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedKategori = KategoriAduan.LAINNYA }
                        )
                    }
                }
            }

            // ── LOKASI ────────────────────────────────────────────────────────
            FormField(label = "LOKASI") {
                OutlinedTextField(
                    value = lokasi,
                    onValueChange = { lokasi = it; lokasiError = false },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Gedung / Lantai / Ruangan", color = TextHint, fontSize = 13.sp) },
                    singleLine = true,
                    isError = lokasiError,
                    supportingText = if (lokasiError) {{ Text("Lokasi tidak boleh kosong", color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }} else null,
                    shape = RoundedCornerShape(10.dp),
                    colors = outlinedFieldColors()
                )
            }

            // ── DESKRIPSI ─────────────────────────────────────────────────────
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

            // ── UPLOAD FOTO (maks 3) ──────────────────────────────────────────
            FormField(label = "UPLOAD FOTO (OPSIONAL · MAKS. $MAX_FOTO)") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                    // Row preview foto yang sudah dipilih
                    if (fotoUris.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            itemsIndexed(fotoUris) { index, uri ->
                                FotoPreviewItem(
                                    uri = uri,
                                    onRemove = { fotoUris.removeAt(index) }
                                )
                            }
                            // Tombol tambah foto kecil jika slot masih ada
                            if (fotoUris.size < MAX_FOTO) {
                                item { TambahFotoKecil(onClick = { pickPhoto() }) }
                            }
                        }
                    }

                    // Area upload besar — hanya tampil jika belum ada foto
                    if (fotoUris.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFEEEEF4))
                                .border(BorderStroke(1.dp, Color(0xFFCCCCDD)), RoundedCornerShape(10.dp))
                                .clickable { pickPhoto() },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AddPhotoAlternate,
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp),
                                    tint = TextHint
                                )
                                Text("Ketuk untuk pilih foto", fontSize = 13.sp, color = TextSecondary)
                                Text("JPG, PNG · maks. 5 MB per foto", fontSize = 11.sp, color = TextHint)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

// ─── Preview item satu foto ───────────────────────────────────────────────────
@Composable
fun FotoPreviewItem(uri: Uri, onRemove: () -> Unit) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .size(110.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, InputBorder, RoundedCornerShape(10.dp))
    ) {
        AsyncImage(
            // Batasi decode ke 220x220px (2x ukuran tampil untuk ketajaman),
            // mencegah OOM dan membuat scroll jauh lebih ringan
            model = ImageRequest.Builder(context)
                .data(uri)
                .size(220, 220)
                .crossfade(false)   // matikan animasi fade — lebih ringan
                .build(),
            contentDescription = "Foto aduan",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Tombol ✕ — bulatan putih + icon hitam, 8dp dari pojok agar tidak mepet
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)              // jarak dari pojok gambar
                .size(22.dp)
                .background(Color.White, CircleShape)
                .clickable { onRemove() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Hapus foto",
                tint = Color.Black,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

// ─── Tombol tambah foto kecil di row preview ──────────────────────────────────
@Composable
fun TambahFotoKecil(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(110.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFEEEEF4))
            .border(BorderStroke(1.dp, Color(0xFFCCCCDD)), RoundedCornerShape(10.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.AddPhotoAlternate,
                contentDescription = "Tambah foto",
                tint = TextHint,
                modifier = Modifier.size(26.dp)
            )
            Text("Tambah", fontSize = 11.sp, color = TextSecondary)
        }
    }
}

// ─── Kategori Button — semua pakai Material ImageVector ──────────────────────
@Composable
fun KategoriButton(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) PrimaryBlue else Color.White,
            contentColor = if (selected) Color.White else TextPrimary
        ),
        border = BorderStroke(
            width = if (selected) 0.dp else 1.dp,
            color = if (selected) Color.Transparent else CategoryBorder
        ),
        contentPadding = PaddingValues(horizontal = 10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = if (selected) Color.White else PrimaryBlue
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// ─── Form Field wrapper ───────────────────────────────────────────────────────
@Composable
fun FormField(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp,
            color = TextSecondary
        )
        content()
    }
}

// ─── Outlined field colors helper ────────────────────────────────────────────
@Composable
fun outlinedFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    focusedBorderColor = PrimaryBlue,
    unfocusedBorderColor = InputBorder,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    cursorColor = PrimaryBlue,
    errorContainerColor = Color.White
)

// ─── Preview ─────────────────────────────────────────────────────────────────
@Preview(name = "Buat Aduan Screen", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun BuatAduanScreenPreview() {
    MasITTheme {
        BuatAduanScreen(onBack = {}, onSubmit = {})
    }
}
