package com.masit.hub.ui.screen

// Stub screens — akan diganti satu per satu setiap halaman selesai dibuat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun RiwayatTeknisiScreen(
    onHome: () -> Unit,
    onProfil: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("RiwayatTeknisi - Coming Soon")
    }
}

@Composable
fun GantiPasswordScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("GantiPassword - Coming Soon")
    }
}

@Composable
fun LupaPasswordScreen(
    onBack: () -> Unit,
    onNavigateToBuatPassword: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("LupaPassword - Coming Soon")
    }
}

@Composable
fun BuatPasswordBaruScreen(
    onBack: () -> Unit,
    onPasswordSaved: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("BuatPasswordBaru - Coming Soon")
    }
}
