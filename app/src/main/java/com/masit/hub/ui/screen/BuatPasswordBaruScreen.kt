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
import com.masit.hub.data.OtpState
import com.masit.hub.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuatPasswordBaruScreen(
    onBack: () -> Unit,
    onPasswordSaved: () -> Unit
) {
    // Email dari OtpState — sudah diverifikasi di LupaPasswordScreen
    val emailTarget = OtpState.emailTarget

    var passwordBaru by remember { mutableStateOf("") }
    var konfirmasiPassword by remember { mutableStateOf("") }
    var showBaru by remember { mutableStateOf(false) }
    var showKonfirmasi by remember { mutableStateOf(false) }
    var errorBaru by remember { mutableStateOf("") }
    var errorKonfirmasi by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    "Password Berhasil Dibuat!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    "Password baru Anda telah berhasil disimpan. Silakan login menggunakan password baru.",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = { showSuccessDialog = false; onPasswordSaved() },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentYellow, contentColor = PrimaryBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Login Sekarang", fontWeight = FontWeight.Bold)
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
                        Text("Buat Password Baru", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
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
                text = "Demi keamanan Anda, pastikan password baru sulit ditebak namun mudah diingat oleh Anda sendiri.",
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 20.sp
            )

            // ── PASSWORD BARU ─────────────────────────────────────────────────
            FormField(label = "PASSWORD BARU") {
                OutlinedTextField(
                    value = passwordBaru,
                    onValueChange = {
                        passwordBaru = it
                        errorBaru = ""
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
                    var valid = true

                    if (passwordBaru.length < 8) {
                        errorBaru = "Password minimal 8 karakter"
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

                    // Reset password menggunakan email yang sudah diverifikasi OTP
                    val berhasil = AppState.resetPassword(
                        email = emailTarget,
                        passwordBaru = passwordBaru
                    )

                    if (berhasil) {
                        OtpState.clear()   // baru clear setelah password berhasil disimpan
                        showSuccessDialog = true
                    } else {
                        errorBaru = "Terjadi kesalahan. Silakan ulangi proses lupa password."
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
@Preview(name = "Buat Password Baru", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun BuatPasswordBaruScreenPreview() {
    MasITTheme {
        BuatPasswordBaruScreen(onBack = {}, onPasswordSaved = {})
    }
}
