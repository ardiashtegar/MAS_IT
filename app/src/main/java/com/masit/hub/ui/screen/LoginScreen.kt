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
import com.masit.hub.ui.component.FormField
import com.masit.hub.ui.component.PrimaryButton
import com.masit.hub.ui.component.outlinedFieldColors
import com.masit.hub.ui.theme.*

@Composable
fun LoginScreen(onNavigate: (String) -> Unit) {
    var username       by remember { mutableStateOf("") }
    var password       by remember { mutableStateOf("") }
    var showPassword   by remember { mutableStateOf(false) }
    var errorMessage   by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.fillMaxSize().background(PrimaryBlue)
    ) {
        // Header biru
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 56.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(18.dp)).background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_masit_logo),
                    contentDescription = "Logo MAS IT",
                    modifier = Modifier.size(56.dp).padding(2.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("MAS IT", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Manajemen Aduan & Solusi IT", fontSize = 13.sp, color = Color.White.copy(alpha = 0.75f))
        }

        // Form card
        Box(
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)).background(BackgroundLight)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                Text("Masuk ke Akun", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Gunakan akun perusahaan untuk login", fontSize = 13.sp, color = TextSecondary)

                Spacer(modifier = Modifier.height(28.dp))

                FormField(label = "USERNAME (ID KARYAWAN)") {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it; errorMessage = "" },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Contoh: 20260001", color = TextHint, fontSize = 14.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        shape = RoundedCornerShape(10.dp),
                        colors = outlinedFieldColors(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                FormField(label = "PASSWORD") {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = "" },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Password", color = TextHint, fontSize = 14.sp) },
                        singleLine = true,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = null,
                                    tint = TextHint
                                )
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = outlinedFieldColors(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Lupa Password?",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    modifier = Modifier.clickable { onNavigate(Routes.LUPA_PASSWORD) }.padding(vertical = 4.dp)
                )

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(28.dp))

                PrimaryButton(
                    text = "Masuk",
                    onClick = {
                        focusManager.clearFocus()
                        if (username.isBlank() || password.isBlank()) {
                            errorMessage = "Username dan password tidak boleh kosong"
                            return@PrimaryButton
                        }
                        val user = AppState.login(username.trim(), password)
                        if (user != null) {
                            onNavigate(if (user.role == UserRole.TEKNISI) Routes.HOME_TEKNISI else Routes.HOME_USER)
                        } else {
                            errorMessage = "Username atau password salah"
                        }
                    }
                )
            }
        }
    }
}

@Preview(name = "Login Screen", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun LoginScreenPreview() {
    MasITTheme { LoginScreen(onNavigate = {}) }
}
