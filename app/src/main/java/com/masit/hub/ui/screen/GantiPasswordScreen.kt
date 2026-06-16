package com.masit.hub.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masit.hub.data.AppState
import com.masit.hub.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GantiPasswordScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    // ── Form state ─────────────────────────────────────────────────────────────
    var passwordLama by remember { mutableStateOf("") }
    var passwordBaru by remember { mutableStateOf("") }
    var konfirmasiPassword by remember { mutableStateOf("") }

    // ── Visibility toggle ──────────────────────────────────────────────────────
    var showLama by remember { mutableStateOf(false) }
    var showBaru by remember { mutableStateOf(false) }
    var showKonfirmasi by remember { mutableStateOf(false) }

    // ── Error state ────────────────────────────────────────────────────────────
    var errorLama by remember { mutableStateOf("") }
    var errorBaru by remember { mutableStateOf("") }
    var errorKonfirmasi by remember { mutableStateOf("") }

    // ── Success dialog ─────────────────────────────────────────────────────────
    var showSuccessDialog by remember { mutableStateOf(false) }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text("Password Berhasil Diubah!", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextPrimary)
            },
            text = {
                Text(
                    "Password Anda telah berhasil diperbarui. Gunakan password baru untuk login berikutnya.",
                    fontSize = 14.sp, color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = { showSuccessDialog = false; onSaved() },
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
                        Text("Ganti Password", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
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
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Subtitle
            Text(
                text = "Masukkan password lama Anda, lalu buat password baru yang kuat.",
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 20.sp
            )

            // ── PASSWORD LAMA ─────────────────────────────────────────────────
            FormField(label = "PASSWORD LAMA") {
                OutlinedTextField(
                    value = passwordLama,
                    onValueChange = {
                        passwordLama = it
                        errorLama = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Password lama", color = TextHint, fontSize = 14.sp) },
                    singleLine = true,
                    visualTransformation = if (showLama) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showLama = !showLama }) {
                            Icon(
                                imageVector = if (showLama) Icons.Filled.Visibility
                                else Icons.Filled.VisibilityOff,
                                contentDescription = null,
                                tint = TextHint
                            )
                        }
                    },
                    isError = errorLama.isNotEmpty(),
                    supportingText = if (errorLama.isNotEmpty()) {
                        { Text(errorLama, color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }
                    } else null,
                    shape = RoundedCornerShape(10.dp),
                    colors = outlinedFieldColors()
                )
            }

            // ── PASSWORD BARU ─────────────────────────────────────────────────
            FormField(label = "PASSWORD BARU") {
                OutlinedTextField(
                    value = passwordBaru,
                    onValueChange = {
                        passwordBaru = it
                        errorBaru = ""
                        // Reset error konfirmasi jika user mengetik ulang
                        if (errorKonfirmasi == "Password tidak cocok") errorKonfirmasi = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Min. 8 karakter", color = TextHint, fontSize = 14.sp) },
                    singleLine = true,
                    visualTransformation = if (showBaru) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showBaru = !showBaru }) {
                            Icon(
                                imageVector = if (showBaru) Icons.Filled.Visibility
                                else Icons.Filled.VisibilityOff,
                                contentDescription = null,
                                tint = TextHint
                            )
                        }
                    },
                    isError = errorBaru.isNotEmpty(),
                    supportingText = if (errorBaru.isNotEmpty()) {
                        { Text(errorBaru, color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }
                    } else null,
                    shape = RoundedCornerShape(10.dp),
                    colors = outlinedFieldColors()
                )
            }

            // ── KONFIRMASI PASSWORD ───────────────────────────────────────────
            FormField(label = "KONFIRMASI PASSWORD") {
                OutlinedTextField(
                    value = konfirmasiPassword,
                    onValueChange = {
                        konfirmasiPassword = it
                        errorKonfirmasi = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ulangi password baru", color = TextHint, fontSize = 14.sp) },
                    singleLine = true,
                    visualTransformation = if (showKonfirmasi) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showKonfirmasi = !showKonfirmasi }) {
                            Icon(
                                imageVector = if (showKonfirmasi) Icons.Filled.Visibility
                                else Icons.Filled.VisibilityOff,
                                contentDescription = null,
                                tint = TextHint
                            )
                        }
                    },
                    isError = errorKonfirmasi.isNotEmpty(),
                    supportingText = if (errorKonfirmasi.isNotEmpty()) {
                        { Text(errorKonfirmasi, color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }
                    } else null,
                    shape = RoundedCornerShape(10.dp),
                    colors = outlinedFieldColors()
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ── Tombol Simpan ─────────────────────────────────────────────────
            Button(
                onClick = {
                    // Validasi semua field
                    var valid = true

                    if (passwordLama.isBlank()) {
                        errorLama = "Password lama tidak boleh kosong"
                        valid = false
                    }
                    if (passwordBaru.length < 8) {
                        errorBaru = "Password baru minimal 8 karakter"
                        valid = false
                    }
                    if (passwordBaru == passwordLama && passwordBaru.isNotBlank()) {
                        errorBaru = "Password baru tidak boleh sama dengan password lama"
                        valid = false
                    }
                    if (konfirmasiPassword.isBlank()) {
                        errorKonfirmasi = "Konfirmasi password tidak boleh kosong"
                        valid = false
                    } else if (konfirmasiPassword != passwordBaru) {
                        errorKonfirmasi = "Password tidak cocok"
                        valid = false
                    }

                    if (!valid) return@Button

                    // Coba ganti password — false jika password lama salah
                    val berhasil = AppState.gantiPassword(
                        passwordLama = passwordLama,
                        passwordBaru = passwordBaru
                    )

                    if (berhasil) {
                        showSuccessDialog = true
                    } else {
                        errorLama = "Password lama tidak sesuai"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentYellow,
                    contentColor = PrimaryBlue
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text("Simpan Password", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────
@Preview(name = "Ganti Password Screen", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun GantiPasswordScreenPreview() {
    MasITTheme {
        GantiPasswordScreen(onBack = {}, onSaved = {})
    }
}
