package com.masit.hub.navigation

object Routes {
    const val LOGIN = "login"
    const val HOME_USER = "home_user"
    const val HOME_TEKNISI = "home_teknisi"
    const val BUAT_ADUAN = "buat_aduan"
    const val DETAIL_ADUAN = "detail_aduan/{aduanId}"
    const val KELOLA_ADUAN = "kelola_aduan/{aduanId}"
    const val RIWAYAT_USER = "riwayat_user"
    const val RIWAYAT_TEKNISI = "riwayat_teknisi"
    const val PROFIL = "profil"
    const val GANTI_PASSWORD = "ganti_password"
    const val LUPA_PASSWORD = "lupa_password"
    const val BUAT_PASSWORD_BARU = "buat_password_baru"

    fun detailAduan(aduanId: String) = "detail_aduan/$aduanId"
    fun kelolaAduan(aduanId: String) = "kelola_aduan/$aduanId"
}