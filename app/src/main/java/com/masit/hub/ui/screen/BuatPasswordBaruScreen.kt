package com.masit.hub.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masit.hub.data.AppState
import com.masit.hub.data.OtpState
import com.masit.hub.ui.component.FormField
import com.masit.hub.ui.component.KonfirmasiDialog
import com.masit.hub.ui.component.PasswordField
import com.masit.hub.ui.component.PrimaryButton
import com.masit.hub.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuatPasswordBaruScreen(
    onBack: () -> Unit,
    onPasswordSaved: () -> Unit
) {
    val emailTarget        = OtpState.emailTarget
    var passwordBaru       by remember { mutableStateOf("") }
    var konfirmasiPassword by remember { mutableStateOf("") }
    var showBaru           by remember { mutableStateOf(false) }
    var showKonfirmasi     by remember { mutableStateOf(false) }
    var errorBaru          by remember { mutableStateOf("") }
    var errorKonfirmasi    by remember { mutableStateOf("") }
    var showSuccess        by remember { mutableStateOf(false) }

    if (showSuccess) {
        KonfirmasiDialog(
            title = "Password Berhasil Dibuat!",
            message = "Password baru Anda telah berhasil disimpan. Silakan login menggunakan password baru.",
            confirmLabel = "Login Sekarang",
            onConfirm = { showSuccess = false; onPasswordSaved() }
        )
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Buat Password Baru", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = PrimaryBlue) } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
                )
                HorizontalDivider(color = InputBorder, thickness = 0.8.dp)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                "Demi keamanan Anda, pastikan password baru sulit ditebak namun mudah diingat oleh Anda sendiri.",
                fontSize = 13.sp, color = TextSecondary, lineHeight = 20.sp
            )

            FormField(label = "PASSWORD BARU") {
                PasswordField(
                    value = passwordBaru,
                    onValueChange = {
                        passwordBaru = it; errorBaru = ""
                        if (errorKonfirmasi == "Password tidak cocok") errorKonfirmasi = ""
                    },
                    placeholder = "Min. 8 karakter",
                    showPassword = showBaru,
                    onToggleVisibility = { showBaru = !showBaru },
                    isError = errorBaru.isNotEmpty(),
                    supportingText = if (errorBaru.isNotEmpty()) {{ Text(errorBaru, color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }} else null
                )
            }

            FormField(label = "KONFIRMASI PASSWORD") {
                PasswordField(
                    value = konfirmasiPassword,
                    onValueChange = { konfirmasiPassword = it; errorKonfirmasi = "" },
                    placeholder = "Ulangi password baru",
                    showPassword = showKonfirmasi,
                    onToggleVisibility = { showKonfirmasi = !showKonfirmasi },
                    isError = errorKonfirmasi.isNotEmpty(),
                    supportingText = if (errorKonfirmasi.isNotEmpty()) {{ Text(errorKonfirmasi, color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }} else null
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            PrimaryButton(
                text = "Simpan Password",
                onClick = {
                    var valid = true
                    if (passwordBaru.length < 8)             { errorBaru = "Password minimal 8 karakter"; valid = false }
                    if (konfirmasiPassword.isBlank())        { errorKonfirmasi = "Konfirmasi password tidak boleh kosong"; valid = false }
                    else if (konfirmasiPassword != passwordBaru) { errorKonfirmasi = "Password tidak cocok"; valid = false }
                    if (!valid) return@PrimaryButton

                    if (AppState.resetPassword(email = emailTarget, passwordBaru = passwordBaru)) {
                        OtpState.clear()
                        showSuccess = true
                    } else {
                        errorBaru = "Terjadi kesalahan. Silakan ulangi proses lupa password."
                    }
                }
            )
        }
    }
}

@Preview(name = "Buat Password Baru", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun BuatPasswordBaruScreenPreview() {
    MasITTheme { BuatPasswordBaruScreen(onBack = {}, onPasswordSaved = {}) }
}
