package com.masit.hub.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masit.hub.R
import com.masit.hub.data.AppState
import com.masit.hub.data.UserRole
import com.masit.hub.navigation.Routes
import com.masit.hub.ui.theme.*
import com.masit.hub.ui.theme.MasITTheme

@Composable
fun LoginScreen(
    onNavigate: (String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBlue)
    ) {
        // ── Header / Hero section ──────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 56.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App icon box — logo asli dari drawable
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_masit_logo),
                    contentDescription = "Logo MAS IT",
                    modifier = Modifier
                        .size(56.dp)
                        .padding(2.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "MAS IT",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Manajemen Aduan & Solusi IT",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.75f)
            )
        }

        // ── White card content ─────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(BackgroundLight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                // Title
                Text(
                    text = "Masuk ke Akun",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Gunakan akun perusahaan untuk login",
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(28.dp))

                // USERNAME field
                Text(
                    text = "USERNAME",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        errorMessage = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Username",
                            color = TextHint,
                            fontSize = 14.sp
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = InputBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = PrimaryBlue
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // PASSWORD field
                Text(
                    text = "PASSWORD",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Password",
                            color = TextHint,
                            fontSize = 14.sp
                        )
                    },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility
                                else Icons.Filled.VisibilityOff,
                                contentDescription = if (passwordVisible) "Sembunyikan password"
                                else "Tampilkan password",
                                tint = TextHint
                            )
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = InputBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = PrimaryBlue
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Lupa Password
                Text(
                    text = "Lupa Password?",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    modifier = Modifier
                        .clickable { onNavigate(Routes.LUPA_PASSWORD) }
                        .padding(vertical = 4.dp)
                )

                // Error message
                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // MASUK button
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        if (username.isBlank() || password.isBlank()) {
                            errorMessage = "Username dan password tidak boleh kosong"
                            return@Button
                        }
                        val user = AppState.login(username.trim(), password)
                        if (user != null) {
                            if (user.role == UserRole.TEKNISI) {
                                onNavigate(Routes.HOME_TEKNISI)
                            } else {
                                onNavigate(Routes.HOME_USER)
                            }
                        } else {
                            errorMessage = "Username atau password salah"
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
                    Text(
                        text = "Masuk",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────
@Preview(name = "Login Screen", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun LoginScreenPreview() {
    MasITTheme {
        LoginScreen(onNavigate = {})
    }
}
