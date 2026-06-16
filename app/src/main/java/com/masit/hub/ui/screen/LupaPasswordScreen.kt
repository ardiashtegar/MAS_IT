package com.masit.hub.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masit.hub.data.AppState
import com.masit.hub.data.OtpService
import com.masit.hub.data.OtpState
import com.masit.hub.data.VerifyResult
import com.masit.hub.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LupaPasswordScreen(
    onBack: () -> Unit,
    onNavigateToBuatPassword: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var otpTerkirim by remember { mutableStateOf(false) }
    var isSending by remember { mutableStateOf(false) }

    val otpDigits = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = remember { List(6) { FocusRequester() } }
    var otpError by remember { mutableStateOf("") }
    var isVerifying by remember { mutableStateOf(false) }

    // Countdown 10 menit
    var sisaDetik by remember { mutableIntStateOf(0) }
    LaunchedEffect(otpTerkirim) {
        if (otpTerkirim) {
            sisaDetik = 10 * 60
            while (sisaDetik > 0) {
                delay(1000L)
                sisaDetik--
            }
        }
    }
    val timerText = "%02d:%02d".format(sisaDetik / 60, sisaDetik % 60)

    fun kirimOtp() {
        val emailTrimmed = email.trim()
        if (emailTrimmed.isBlank()) {
            emailError = "Email tidak boleh kosong"
            return
        }
        val terdaftar = AppState.userList.any {
            it.email.equals(emailTrimmed, ignoreCase = true)
        }
        if (!terdaftar) {
            emailError = "Email tidak terdaftar di sistem"
            return
        }
        emailError = ""
        isSending = true
        scope.launch {
            val berhasil = OtpService.kirimOtp(emailTrimmed)
            isSending = false
            if (berhasil) {
                otpTerkirim = true
                repeat(6) { otpDigits[it] = "" }
                otpError = ""
            } else {
                emailError = "Gagal mengirim OTP. Periksa koneksi internet."
            }
        }
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text("Lupa Password", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = PrimaryBlue)
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
            Text(
                text = "Masukkan email company Anda. Kode OTP 6 digit akan dikirim ke email tersebut.",
                fontSize = 13.sp, color = TextSecondary, lineHeight = 20.sp
            )

            // ── Email ─────────────────────────────────────────────────────────
            FormField(label = "EMAIL COMPANY") {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; emailError = "" },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("nama@company.id", color = TextHint, fontSize = 14.sp) },
                    singleLine = true,
                    enabled = !otpTerkirim,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = emailError.isNotEmpty(),
                    supportingText = if (emailError.isNotEmpty()) {
                        { Text(emailError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }
                    } else null,
                    shape = RoundedCornerShape(10.dp),
                    colors = outlinedFieldColors()
                )
            }

            // ── Tombol Kirim OTP ──────────────────────────────────────────────
            Button(
                onClick = { kirimOtp() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !isSending && !otpTerkirim,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentYellow,
                    contentColor = PrimaryBlue,
                    disabledContainerColor = AccentYellow.copy(alpha = 0.5f),
                    disabledContentColor = PrimaryBlue.copy(alpha = 0.5f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                if (isSending) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = PrimaryBlue, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mengirim...", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                } else {
                    Text(
                        text = if (otpTerkirim) "OTP Terkirim ✓" else "Kirim Kode OTP",
                        fontSize = 15.sp, fontWeight = FontWeight.Bold
                    )
                }
            }

            // ── OTP Section ───────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (otpTerkirim) Color.White else Color(0xFFF0F0F5)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = androidx.compose.foundation.BorderStroke(
                    0.8.dp,
                    if (otpTerkirim) InputBorder else Color(0xFFDDDDE8)
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Hint
                    Text(
                        text = if (otpTerkirim)
                            "Masukkan kode OTP yang dikirim ke\n${email.trim()}"
                        else
                            "Kirim email terlebih dahulu untuk mengisi kode OTP",
                        fontSize = 12.sp,
                        color = if (otpTerkirim) TextSecondary else TextHint,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    // 6 Kotak OTP — weight(1f) agar otomatis membagi rata
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        otpDigits.forEachIndexed { index, digit ->
                            OtpDigitBox(
                                digit = digit,
                                enabled = otpTerkirim,
                                isError = otpError.isNotEmpty(),
                                focusRequester = focusRequesters[index],
                                modifier = Modifier.weight(1f),
                                onValueChange = { newVal ->
                                    val filtered = newVal.filter { it.isDigit() }.take(1)
                                    otpDigits[index] = filtered
                                    otpError = ""
                                    if (filtered.isNotEmpty() && index < 5) {
                                        focusRequesters[index + 1].requestFocus()
                                    } else if (filtered.isEmpty() && index > 0) {
                                        focusRequesters[index - 1].requestFocus()
                                    }
                                }
                            )
                        }
                    }

                    // Timer
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (otpTerkirim) TextSecondary else TextHint
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Kode berlaku: $timerText",
                            fontSize = 12.sp,
                            color = if (otpTerkirim) TextSecondary else TextHint
                        )
                    }

                    // Kirim ulang
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tidak menerima kode? ", fontSize = 12.sp, color = TextSecondary)
                        Text(
                            text = "Kirim ulang",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (otpTerkirim && !isSending) PrimaryBlue else TextHint,
                            modifier = if (otpTerkirim && !isSending) Modifier.clickable {
                                otpTerkirim = false
                                repeat(6) { otpDigits[it] = "" }
                                otpError = ""
                                kirimOtp()
                            } else Modifier
                        )
                    }

                    // Error OTP
                    if (otpError.isNotEmpty()) {
                        Text(
                            otpError,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Tombol Verifikasi OTP
                    Button(
                        onClick = {
                            val inputOtp = otpDigits.joinToString("")
                            if (inputOtp.length < 6) {
                                otpError = "Masukkan 6 digit kode OTP"
                                return@Button
                            }
                            isVerifying = true
                            scope.launch {
                                delay(500L)
                                val result = OtpState.verify(inputOtp)
                                isVerifying = false
                                when (result) {
                                    VerifyResult.VALID -> {
                                        // Jangan clear dulu — emailTarget masih dibutuhkan
                                        // di BuatPasswordBaruScreen untuk reset password
                                        onNavigateToBuatPassword()
                                    }
                                    VerifyResult.EXPIRED ->
                                        otpError = "Kode OTP sudah kedaluwarsa. Kirim ulang kode."
                                    VerifyResult.INVALID ->
                                        otpError = "Kode OTP tidak sesuai. Periksa kembali."
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        enabled = otpTerkirim && !isVerifying,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFFCCCCDD),
                            disabledContentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        if (isVerifying) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Verifikasi OTP", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ─── Satu kotak digit OTP ─────────────────────────────────────────────────────
@Composable
fun OtpDigitBox(
    digit: String,
    enabled: Boolean,
    isError: Boolean,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = digit,
        onValueChange = onValueChange,
        modifier = modifier
            .height(56.dp)
            .focusRequester(focusRequester),
        enabled = enabled,
        singleLine = true,
        textStyle = androidx.compose.ui.text.TextStyle(
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            lineHeight = 24.sp
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = if (enabled) Color.White else Color(0xFFF0F0F5),
            disabledContainerColor = Color(0xFFF0F0F5),
            focusedBorderColor = if (isError) MaterialTheme.colorScheme.error else PrimaryBlue,
            unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error else InputBorder,
            disabledBorderColor = Color(0xFFDDDDE8),
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            disabledTextColor = TextHint,
            cursorColor = PrimaryBlue
        )
    )
}

// ─── Preview ─────────────────────────────────────────────────────────────────
@Preview(name = "Lupa Password Screen", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun LupaPasswordScreenPreview() {
    MasITTheme {
        LupaPasswordScreen(onBack = {}, onNavigateToBuatPassword = {})
    }
}
