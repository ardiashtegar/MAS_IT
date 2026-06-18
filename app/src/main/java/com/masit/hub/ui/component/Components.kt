package com.masit.hub.ui.component

// ─── Components.kt ────────────────────────────────────────────────────────────
// Komponen reusable yang dipakai di lebih dari satu screen.

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masit.hub.R
import com.masit.hub.data.*
import com.masit.hub.ui.theme.*
import androidx.core.net.toUri

// ─── Logo & Header ────────────────────────────────────────────────────────────

@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_masit_logo),
        contentDescription = "Logo MAS IT",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

@Composable
fun ProfilIconButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color.White)
            .clickable { onClick() },
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

// ─── Top Bar ──────────────────────────────────────────────────────────────────

@Composable
fun HomeTopBar(onProfil: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundLight)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppLogo(modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "MAS IT",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }
            ProfilIconButton(onClick = onProfil)
        }
        HorizontalDivider(color = InputBorder, thickness = 0.8.dp)
    }
}

@Composable
fun RiwayatTopBar(onProfil: () -> Unit) {
    Column {
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
            ProfilIconButton(onClick = onProfil)
        }
        HorizontalDivider(color = InputBorder, thickness = 0.8.dp)
    }
}

// ─── Bottom Navigation ────────────────────────────────────────────────────────

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
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home", modifier = Modifier.size(24.dp)) },
            label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = navBarItemColors()
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = onRiwayat,
            icon = { Icon(Icons.Outlined.History, contentDescription = "Riwayat", modifier = Modifier.size(24.dp)) },
            label = { Text("Riwayat", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = navBarItemColors()
        )
    }
}

@Composable
fun BottomNavTeknisi(
    selectedTab: Int,
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
            icon = { Icon(Icons.Filled.Inbox, contentDescription = "Aduan Masuk", modifier = Modifier.size(24.dp)) },
            label = { Text("Aduan Masuk", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = navBarItemColors()
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = onRiwayat,
            icon = { Icon(Icons.Outlined.History, contentDescription = "Riwayat", modifier = Modifier.size(24.dp)) },
            label = { Text("Riwayat", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = navBarItemColors()
        )
    }
}

@Composable
private fun navBarItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = AccentYellow,
    selectedTextColor = AccentYellow,
    unselectedIconColor = Color.White.copy(alpha = 0.6f),
    unselectedTextColor = Color.White.copy(alpha = 0.6f),
    indicatorColor = Color.Transparent
)

// ─── Aduan Cards ──────────────────────────────────────────────────────────────

// Card untuk HomeUser & HomeTeknisi — waktu relatif
@Composable
fun AduanCard(
    aduan: Aduan,
    tick: Long,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val waktuRelatif = remember(tick) {
        formatWaktuRelatif(aduan.createdAt)
    }

    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(0.8.dp, InputBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(aduan.ticketNumber, fontSize = 11.sp, color = TextHint, fontWeight = FontWeight.Medium)
                StatusBadgeText(status = aduan.status)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(aduan.judul, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${aduan.kategori.label} · $waktuRelatif",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

// Card untuk Riwayat — waktu tanggal
@Composable
fun AduanCardRiwayat(
    aduan: Aduan,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(0.8.dp, InputBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(aduan.ticketNumber, fontSize = 11.sp, color = TextHint, fontWeight = FontWeight.Medium)
                StatusBadgeText(status = aduan.status)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(aduan.judul, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${aduan.kategori.label} · ${formatTanggal(aduan.createdAt)}",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

// ─── Status Badge ─────────────────────────────────────────────────────────────

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

// ─── Sort Dropdown ────────────────────────────────────────────────────────────

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
            border = BorderStroke(0.8.dp, InputBorder),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(selected, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(18.dp), tint = TextPrimary)
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
                    onClick = { onSelected(option); expanded = false }
                )
            }
        }
    }
}

// ─── Form Field ───────────────────────────────────────────────────────────────

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

// ─── Password Field ───────────────────────────────────────────────────────────

@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    showPassword: Boolean,
    onToggleVisibility: () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = TextHint, fontSize = 14.sp) },
        singleLine = true,
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = null,
                    tint = TextHint
                )
            }
        },
        isError = isError,
        supportingText = supportingText,
        shape = RoundedCornerShape(10.dp),
        colors = outlinedFieldColors()
    )
}

// ─── OutlinedTextField Colors ─────────────────────────────────────────────────

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

// ─── Detail Info Item ─────────────────────────────────────────────────────────

@Composable
fun DetailInfoItem(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.8.sp, color = TextSecondary)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}

// ─── Foto Lampiran ────────────────────────────────────────────────────────────

@Composable
fun FotoLampiranItem(uriStr: String, onClick: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        coil.compose.AsyncImage(
            model = coil.request.ImageRequest.Builder(context)
                .data(uriStr.toUri())
                .size(160, 160)
                .crossfade(false)
                .build(),
            contentDescription = "Lampiran foto",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

// ─── Riwayat Timeline Item ────────────────────────────────────────────────────

@Composable
fun RiwayatItem(
    riwayat: RiwayatUpdate,
    isFirst: Boolean,
    isLast: Boolean
) {
    val dotColor = if (isFirst) PrimaryBlue else Color(0xFFCCCCCC)

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp)
        ) {
            if (!isFirst) {
                Box(modifier = Modifier.width(2.dp).height(8.dp).background(Color(0xFFE0E0E0)))
            } else {
                Spacer(modifier = Modifier.height(4.dp))
            }
            Box(modifier = Modifier.size(12.dp).background(dotColor, CircleShape))
            if (!isLast) {
                Box(modifier = Modifier.width(2.dp).height(40.dp).background(Color(0xFFE0E0E0)))
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f).padding(bottom = if (isLast) 0.dp else 4.dp)
        ) {
            Text(
                text = "Status diubah: ${riwayat.status.label}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = "Catatan: ${riwayat.catatan.ifBlank { "-" }}",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Text(riwayat.waktu, fontSize = 11.sp, color = TextHint)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

// ─── Empty States ─────────────────────────────────────────────────────────────

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 80.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(48.dp), tint = TextHint)
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
            Text(subtitle, fontSize = 13.sp, color = TextHint, textAlign = TextAlign.Center, lineHeight = 20.sp)
        }
    }
}

// ─── Stat Card ────────────────────────────────────────────────────────────────

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    count: Int,
    label: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(0.8.dp, InputBorder)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(count.toString(), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(label, fontSize = 12.sp, color = TextSecondary)
        }
    }
}

// ─── Konfirmasi Dialog ────────────────────────────────────────────────────────

@Composable
fun KonfirmasiDialog(
    title: String,
    message: String,
    confirmLabel: String,
    confirmColor: Color = AccentYellow,
    confirmTextColor: Color = PrimaryBlue,
    onConfirm: () -> Unit,
    onDismiss: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = { onDismiss?.invoke() },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        title = { Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextPrimary) },
        text = { Text(message, fontSize = 14.sp, color = TextSecondary) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = confirmColor, contentColor = confirmTextColor),
                shape = RoundedCornerShape(10.dp)
            ) { Text(confirmLabel, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            if (onDismiss != null) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, InputBorder)
                ) { Text("Batal", color = TextPrimary) }
            }
        }
    )
}

// ─── Primary Button ───────────────────────────────────────────────────────────

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    containerColor: Color = AccentYellow,
    contentColor: Color = PrimaryBlue
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}
