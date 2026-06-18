package com.masit.hub.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masit.hub.data.AppState
import com.masit.hub.data.UserRole
import com.masit.hub.ui.component.KonfirmasiDialog
import com.masit.hub.ui.component.PrimaryButton
import com.masit.hub.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilScreen(
    onBack: () -> Unit,
    onGantiPassword: () -> Unit,
    onLogout: () -> Unit
) {
    val user = AppState.currentUser
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        KonfirmasiDialog(
            title = "Keluar?",
            message = "Anda akan keluar dari akun ini. Yakin ingin logout?",
            confirmLabel = "Ya, Logout",
            confirmColor = DangerRed,
            confirmTextColor = Color.White,
            onConfirm = { showLogoutDialog = false; onLogout() },
            onDismiss = { showLogoutDialog = false }
        )
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Profil Saya", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = PrimaryBlue) } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
                )
                HorizontalDivider(color = InputBorder, thickness = 0.8.dp)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState())
        ) {
            // Header biru dengan avatar
            Box(
                modifier = Modifier.fillMaxWidth().background(PrimaryBlue).padding(vertical = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier.size(72.dp).clip(RoundedCornerShape(16.dp)).background(AccentYellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Person, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(40.dp))
                    }
                    Text(user?.namaLengkap ?: "-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    val roleLabel = if (user?.role == UserRole.TEKNISI) "Teknisi" else "User"
                    Box(
                        modifier = Modifier.background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(50)).padding(horizontal = 16.dp, vertical = 5.dp)
                    ) {
                        Text("$roleLabel · ${user?.department ?: "-"}", fontSize = 12.sp, color = AccentYellow, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info card
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = androidx.compose.foundation.BorderStroke(0.8.dp, InputBorder)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ProfilInfoRow(icon = Icons.Filled.Badge,    label = "Nama Lengkap",  value = user?.namaLengkap ?: "-", showDivider = true)
                    ProfilInfoRow(icon = Icons.Filled.Person,   label = "ID Karyawan",   value = user?.idKaryawan  ?: "-", showDivider = true)
                    ProfilInfoRow(icon = Icons.Filled.Email,    label = "Email",          value = user?.email       ?: "-", showDivider = true)
                    ProfilInfoRow(icon = Icons.Filled.Security, label = "Role",           value = if (user?.role == UserRole.TEKNISI) "Teknisi" else "User", showDivider = false)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                text = "Ganti Password",
                icon = Icons.Filled.Lock,
                onClick = onGantiPassword,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            PrimaryButton(
                text = "Logout",
                icon = Icons.AutoMirrored.Filled.Logout,
                onClick = { showLogoutDialog = true },
                containerColor = DangerRed,
                contentColor = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "MAS IT v1.0.0 · © 2026",
                fontSize = 11.sp,
                color = TextHint,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ProfilInfoRow(icon: ImageVector, label: String, value: String, showDivider: Boolean) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryBlue.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(label, fontSize = 12.sp, color = TextSecondary)
                Text(value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }
        }
        if (showDivider) HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = InputBorder, thickness = 0.5.dp)
    }
}

@Preview(name = "Profil Screen", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun ProfilScreenPreview() {
    MasITTheme { ProfilScreen(onBack = {}, onGantiPassword = {}, onLogout = {}) }
}
