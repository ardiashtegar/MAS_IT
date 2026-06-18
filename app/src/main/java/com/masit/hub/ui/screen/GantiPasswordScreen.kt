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
import com.masit.hub.ui.component.FormField
import com.masit.hub.ui.component.KonfirmasiDialog
import com.masit.hub.ui.component.PasswordField
import com.masit.hub.ui.component.PrimaryButton
import com.masit.hub.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GantiPasswordScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    var passwordLama       by remember { mutableStateOf("") }
    var passwordBaru       by remember { mutableStateOf("") }
    var konfirmasiPassword by remember { mutableStateOf("") }
    var showLama           by remember { mutableStateOf(false) }
    var showBaru           by remember { mutableStateOf(false) }
    var showKonfirmasi     by remember { mutableStateOf(false) }
    var errorLama          by remember { mutableStateOf("") }
    var errorBaru          by remember { mutableStateOf("") }
    var errorKonfirmasi    by remember { mutableStateOf("") }
    var showSuccess        by remember { mutableStateOf(false) }

    if (showSuccess) {
        KonfirmasiDialog(
            title = "Password Berhasil Diubah!",
            message = "Password Anda telah berhasil diperbarui. Gunakan password baru untuk login berikutnya.",
            confirmLabel = "OK",
            onConfirm = { showSuccess = false; onSaved() }
        )
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Ganti Password", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue) },
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
                "Masukkan password lama Anda, lalu buat password baru yang kuat.",
                fontSize = 13.sp, color = TextSecondary, lineHeight = 20.sp
            )

            FormField(label = "PASSWORD LAMA") {
                PasswordField(
                    value = passwordLama,
                    onValueChange = { passwordLama = it; errorLama = "" },
                    placeholder = "Password lama",
                    showPassword = showLama,
                    onToggleVisibility = { showLama = !showLama },
                    isError = errorLama.isNotEmpty(),
                    supportingText = if (errorLama.isNotEmpty()) {{ Text(errorLama, color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }} else null
                )
            }

            FormField(label = "PASSWORD BARU") {
                PasswordField(
                    value = passwordBaru,
                    onValueChange = { passwordBaru = it; errorBaru = ""; if (errorKonfirmasi == "Password tidak cocok") errorKonfirmasi = "" },
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
                    if (passwordLama.isBlank())                              { errorLama = "Password lama tidak boleh kosong"; valid = false }
                    if (passwordBaru.length < 8)                             { errorBaru = "Password baru minimal 8 karakter"; valid = false }
                    if (passwordBaru == passwordLama && passwordBaru.isNotBlank()) { errorBaru = "Password baru tidak boleh sama dengan password lama"; valid = false }
                    if (konfirmasiPassword.isBlank())                        { errorKonfirmasi = "Konfirmasi password tidak boleh kosong"; valid = false }
                    else if (konfirmasiPassword != passwordBaru)             { errorKonfirmasi = "Password tidak cocok"; valid = false }
                    if (!valid) return@PrimaryButton

                    if (AppState.gantiPassword(passwordLama, passwordBaru)) {
                        showSuccess = true
                    } else {
                        errorLama = "Password lama tidak sesuai"
                    }
                }
            )
        }
    }
}

@Preview(name = "Ganti Password Screen", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun GantiPasswordScreenPreview() {
    MasITTheme { GantiPasswordScreen(onBack = {}, onSaved = {}) }
}
