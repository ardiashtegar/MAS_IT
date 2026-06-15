package com.masit.hub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.Coil
import coil.ImageLoader
import coil.memory.MemoryCache
import com.masit.hub.data.AppState
import com.masit.hub.navigation.Routes
import com.masit.hub.ui.screen.*
import com.masit.hub.ui.theme.BackgroundLight
import com.masit.hub.ui.theme.MasITTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Konfigurasi Coil sekali di awal — memory cache 20% RAM, disk cache aktif
        Coil.setImageLoader(
            ImageLoader.Builder(this)
                .memoryCache {
                    MemoryCache.Builder(this)
                        .maxSizePercent(0.20)   // pakai maks 20% RAM untuk cache gambar
                        .build()
                }
                .crossfade(false)               // matikan crossfade global — lebih ringan
                .build()
        )

        enableEdgeToEdge()
        setContent {
            MasITTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundLight
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Routes.LOGIN
                    ) {
                        // ── Auth Screens ───────────────────────────────────────
                        composable(Routes.LOGIN) {
                            LoginScreen(
                                onNavigate = { route ->
                                    when (route) {
                                        Routes.HOME_USER -> navController.navigate(Routes.HOME_USER) {
                                            popUpTo(Routes.LOGIN) { inclusive = true }
                                        }
                                        Routes.HOME_TEKNISI -> navController.navigate(Routes.HOME_TEKNISI) {
                                            popUpTo(Routes.LOGIN) { inclusive = true }
                                        }
                                        else -> navController.navigate(route)
                                    }
                                }
                            )
                        }

                        composable(Routes.LUPA_PASSWORD) {
                            LupaPasswordScreen(
                                onBack = { navController.popBackStack() },
                                onNavigateToBuatPassword = {
                                    navController.navigate(Routes.BUAT_PASSWORD_BARU)
                                }
                            )
                        }

                        composable(Routes.BUAT_PASSWORD_BARU) {
                            BuatPasswordBaruScreen(
                                onBack = { navController.popBackStack() },
                                onPasswordSaved = {
                                    navController.navigate(Routes.LOGIN) {
                                        popUpTo(Routes.LOGIN) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // ── User Screens ───────────────────────────────────────
                        composable(Routes.HOME_USER) {
                            HomeUserScreen(
                                onBuatAduan = { navController.navigate(Routes.BUAT_ADUAN) },
                                onDetailAduan = { id ->
                                    navController.navigate(Routes.detailAduan(id))
                                },
                                onRiwayat = { navController.navigate(Routes.RIWAYAT_USER) },
                                onProfil = { navController.navigate(Routes.PROFIL) }
                            )
                        }

                        composable(Routes.BUAT_ADUAN) {
                            BuatAduanScreen(
                                onBack = { navController.popBackStack() },
                                onSubmit = { navController.popBackStack() }
                            )
                        }

                        composable(
                            route = Routes.DETAIL_ADUAN,
                            arguments = listOf(navArgument("aduanId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val aduanId = backStackEntry.arguments?.getString("aduanId") ?: ""
                            DetailAduanScreen(
                                aduanId = aduanId,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(Routes.RIWAYAT_USER) {
                            RiwayatUserScreen(
                                onHome = { navController.navigate(Routes.HOME_USER) { popUpTo(Routes.HOME_USER) { inclusive = true } } },
                                onDetailAduan = { id -> navController.navigate(Routes.detailAduan(id)) },
                                onProfil = { navController.navigate(Routes.PROFIL) }
                            )
                        }

                        // ── Teknisi Screens ────────────────────────────────────
                        composable(Routes.HOME_TEKNISI) {
                            HomeTeknisiScreen(
                                onKelolaAduan = { id ->
                                    navController.navigate(Routes.kelolaAduan(id))
                                },
                                onRiwayat = { navController.navigate(Routes.RIWAYAT_TEKNISI) },
                                onProfil = { navController.navigate(Routes.PROFIL) }
                            )
                        }

                        composable(
                            route = Routes.KELOLA_ADUAN,
                            arguments = listOf(navArgument("aduanId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val aduanId = backStackEntry.arguments?.getString("aduanId") ?: ""
                            KelolaAduanScreen(
                                aduanId = aduanId,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(Routes.RIWAYAT_TEKNISI) {
                            RiwayatTeknisiScreen(
                                onHome = { navController.navigate(Routes.HOME_TEKNISI) { popUpTo(Routes.HOME_TEKNISI) { inclusive = true } } },
                                onProfil = { navController.navigate(Routes.PROFIL) }
                            )
                        }

                        // ── Shared Screens ─────────────────────────────────────
                        composable(Routes.PROFIL) {
                            ProfilScreen(
                                onBack = { navController.popBackStack() },
                                onGantiPassword = { navController.navigate(Routes.GANTI_PASSWORD) },
                                onLogout = {
                                    AppState.logout()
                                    navController.navigate(Routes.LOGIN) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(Routes.GANTI_PASSWORD) {
                            GantiPasswordScreen(
                                onBack = { navController.popBackStack() },
                                onSaved = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
