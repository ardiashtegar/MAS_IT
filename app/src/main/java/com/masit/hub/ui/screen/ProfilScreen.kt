package com.masit.hub.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masit.hub.data.AppState
import com.masit.hub.data.UserRole
import com.masit.hub.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilScreen(
    onBack: () -> Unit,
    onGantiPassword: () -> Unit,
    onLogout: () -> Unit
) {
    // Ambil data user yang sedang login
    val user = AppState.currentUser

    var showLogoutDialog by remember { mutableStateOf(false) }

    // Dialog konfirmasi logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text("Keluar?", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextPrimary)
            },
            text = {
                Text(
                    "Anda akan keluar dari akun ini. Yakin ingin logout?",
                    fontSize = 14.sp, color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DangerRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Ya, Logout", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutDialog = false },
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, InputBorder)
                ) {
                    Text("Batal", color = TextPrimary)
                }
            }
        )
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text("Profil Saya", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Header biru dengan avatar ─────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBlue)
                    .padding(vertical = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Avatar kuning
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(AccentYellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    // Nama
                    Text(
                        text = user?.namaLengkap ?: "-",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    // Role + Department badge
                    val roleLabel = if (user?.role == UserRole.TEKNISI) "Teknisi" else "User"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.Transparent)
                            .then(
                                Modifier.background(
                                    Color.White.copy(alpha = 0.15f),
                                    RoundedCornerShape(50)
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "$roleLabel · ${user?.department ?: "-"}",
                            fontSize = 12.sp,
                            color = AccentYellow,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Info Card ─────────────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = androidx.compose.foundation.BorderStroke(0.8.dp, InputBorder)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ProfilInfoRow(
                        icon = Icons.Filled.Badge,
                        label = "Nama Lengkap",
                        value = user?.namaLengkap ?: "-",
                        showDivider = true
                    )
                    ProfilInfoRow(
                        icon = Icons.Filled.Person,
                        label = "ID Karyawan",
                        value = user?.idKaryawan ?: "-",
                        showDivider = true
                    )
                    ProfilInfoRow(
                        icon = Icons.Filled.Email,
                        label = "Email",
                        value = user?.email ?: "-",
                        showDivider = true
                    )
                    ProfilInfoRow(
                        icon = Icons.Filled.Security,
                        label = "Role",
                        value = if (user?.role == UserRole.TEKNISI) "Teknisi" else "User",
                        showDivider = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Tombol Ganti Password ─────────────────────────────────────────
            Button(
                onClick = onGantiPassword,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentYellow,
                    contentColor = PrimaryBlue
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ganti Password", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── Tombol Logout ─────────────────────────────────────────────────
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DangerRed,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Versi app ─────────────────────────────────────────────────────
            Text(
                text = "MAS IT v1.0.0 · © 2026",
                fontSize = 11.sp,
                color = TextHint,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// ─── Info Row ─────────────────────────────────────────────────────────────────
@Composable
fun ProfilInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    showDivider: Boolean
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryBlue.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(label, fontSize = 12.sp, color = TextSecondary)
                Text(value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = InputBorder,
                thickness = 0.5.dp
            )
        }
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────
@Preview(name = "Profil Screen", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun ProfilScreenPreview() {
    MasITTheme {
        ProfilScreen(onBack = {}, onGantiPassword = {}, onLogout = {})
    }
}
