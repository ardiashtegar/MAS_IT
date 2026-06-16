package com.masit.hub.data

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateListOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ─── Models ───────────────────────────────────────────────────────────────────

data class User(
    val id: String,             // internal key, cth: "u001"
    val idKaryawan: String,     // display + username login, cth: "260001"
    val namaLengkap: String,
    val email: String,
    val role: UserRole,
    val department: String,
    val password: String        // default = idKaryawan
)

enum class UserRole { USER, TEKNISI }

data class Aduan(
    val id: String,             // internal key, cth: "a001"
    val ticketNumber: String,   // display, cth: "#20260601-001"
    val judul: String,
    val kategori: KategoriAduan,
    val lokasi: String,
    val deskripsi: String,
    val status: StatusAduan,
    val pelapor: String,        // nama singkat untuk display
    val pelaporId: String,      // user.id pelapor
    val teknisiId: String? = null, // user.id teknisi yang menangani
    val waktu: String,
    val urutan: Int,
    val fotoUris: List<String> = emptyList(),
    val riwayatUpdate: List<RiwayatUpdate> = emptyList()
)

enum class KategoriAduan(val label: String) {
    JARINGAN("Jaringan"),
    HARDWARE("Hardware"),
    SOFTWARE("Software"),
    LAINNYA("Lainnya")
}

enum class StatusAduan(val label: String) {
    MENUNGGU("Menunggu"),
    DITANGANI("Ditangani"),
    SELESAI("Selesai"),
    BATAL("Batal")
}

data class RiwayatUpdate(
    val status: StatusAduan,
    val catatan: String,
    val waktu: String
)

// ─── App State ────────────────────────────────────────────────────────────────

object AppState {
    var currentUser: User? = null

    // Format username = idKaryawan: [2 digit tahun masuk][4 digit nomor urut]
    // cth: tahun masuk 2026, urutan ke-1 → "260001"
    val userList = mutableStateListOf(
        User(
            id          = "u001",
            idKaryawan  = "260001",
            namaLengkap = "Andi Prasetyo",
            email       = "ardiashtegar125@gmail.com",
            role        = UserRole.USER,
            department  = "Finance Department",
            password    = "260001"
        ),
        User(
            id          = "u002",
            idKaryawan  = "260002",
            namaLengkap = "Dewi Lestari",
            email       = "dewi.l@company.id",
            role        = UserRole.USER,
            department  = "HR Department",
            password    = "260002"
        ),
        User(
            id          = "u003",
            idKaryawan  = "260003",
            namaLengkap = "Reza Firmansyah",
            email       = "reza.f@company.id",
            role        = UserRole.TEKNISI,
            department  = "IT Department",
            password    = "260003"
        ),
        User(
            id          = "u004",
            idKaryawan  = "260004",
            namaLengkap = "Siti Rahayu",
            email       = "siti.r@company.id",
            role        = UserRole.TEKNISI,
            department  = "IT Department",
            password    = "260004"
        )
    )

    // Aduan kosong — diisi saat runtime
    val aduanList = mutableStateListOf<Aduan>()

    // ── Auth ──────────────────────────────────────────────────────────────────

    fun login(username: String, password: String): User? {
        val user = userList.find {
            it.idKaryawan == username.trim() && it.password == password
        }
        currentUser = user
        return user
    }

    fun logout() {
        currentUser = null
    }

    // ── Password ──────────────────────────────────────────────────────────────

    fun gantiPassword(passwordLama: String, passwordBaru: String): Boolean {
        val user = currentUser ?: return false
        val index = userList.indexOfFirst { it.id == user.id }
        if (index == -1 || userList[index].password != passwordLama) return false
        val updated = userList[index].copy(password = passwordBaru)
        userList[index] = updated
        currentUser = updated
        return true
    }

    fun resetPassword(email: String, passwordBaru: String): Boolean {
        val index = userList.indexOfFirst {
            it.email.equals(email, ignoreCase = true)
        }
        if (index == -1) return false
        val updated = userList[index].copy(password = passwordBaru)
        userList[index] = updated
        if (currentUser?.email?.equals(email, ignoreCase = true) == true) {
            currentUser = updated
        }
        return true
    }

    // ── Aduan ─────────────────────────────────────────────────────────────────

    @SuppressLint("DefaultLocale")
    fun tambahAduan(
        judul: String,
        kategori: KategoriAduan,
        lokasi: String,
        deskripsi: String,
        fotoUris: List<String> = emptyList()
    ) {
        val user = currentUser ?: return
        val now = System.currentTimeMillis()
        val dateStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(now))
        val todayCount = aduanList.count { it.ticketNumber.contains(dateStr) }
        val ticketNumber = "#$dateStr-${String.format("%03d", todayCount + 1)}"
        val waktuTampil = "Baru saja"
        val nextUrutan = (aduanList.maxOfOrNull { it.urutan } ?: 0) + 1

        val namaParts = user.namaLengkap.trim().split(" ")
        val namaDisplay = if (namaParts.size >= 2)
            "${namaParts.first()} ${namaParts[1].first()}."
        else namaParts.first()

        aduanList.add(0, Aduan(
            id          = "a$now",
            ticketNumber= ticketNumber,
            judul       = judul,
            kategori    = kategori,
            lokasi      = lokasi,
            deskripsi   = deskripsi,
            status      = StatusAduan.MENUNGGU,
            pelapor     = namaDisplay,
            pelaporId   = user.id,
            waktu       = waktuTampil,
            urutan      = nextUrutan,
            fotoUris    = fotoUris,
            riwayatUpdate = listOf(
                RiwayatUpdate(StatusAduan.MENUNGGU, "Aduan dibuat", waktuTampil)
            )
        ))
    }

    fun tanganiAduan(id: String) {
        val teknisi = currentUser ?: return
        val index = aduanList.indexOfFirst { it.id == id }
        if (index == -1 || aduanList[index].status != StatusAduan.MENUNGGU) return
        val waktu = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        aduanList[index] = aduanList[index].copy(
            status     = StatusAduan.DITANGANI,
            teknisiId  = teknisi.id,
            riwayatUpdate = listOf(
                RiwayatUpdate(StatusAduan.DITANGANI, "Ditangani oleh ${teknisi.namaLengkap}", "Hari ini, $waktu")
            ) + aduanList[index].riwayatUpdate
        )
    }

    fun updateAduan(id: String, statusBaru: StatusAduan, catatan: String) {
        val index = aduanList.indexOfFirst { it.id == id }
        if (index == -1) return
        val waktu = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        aduanList[index] = aduanList[index].copy(
            status = statusBaru,
            riwayatUpdate = listOf(
                RiwayatUpdate(statusBaru, catatan.ifBlank { "-" }, "Hari ini, $waktu")
            ) + aduanList[index].riwayatUpdate
        )
    }

    fun batalkanAduan(id: String) {
        val index = aduanList.indexOfFirst { it.id == id }
        if (index == -1 || aduanList[index].status != StatusAduan.MENUNGGU) return
        val waktu = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        aduanList[index] = aduanList[index].copy(
            status = StatusAduan.BATAL,
            riwayatUpdate = listOf(
                RiwayatUpdate(StatusAduan.BATAL, "Dibatalkan oleh pelapor", "Hari ini, $waktu")
            ) + aduanList[index].riwayatUpdate
        )
    }

    // ── Filter ────────────────────────────────────────────────────────────────

    fun getAduanForUser(): List<Aduan> {
        val id = currentUser?.id ?: return emptyList()
        return aduanList.filter {
            it.pelaporId == id &&
                    it.status in listOf(StatusAduan.MENUNGGU, StatusAduan.DITANGANI, StatusAduan.BATAL)
        }
    }

    fun getSelesaiForUser(): List<Aduan> {
        val id = currentUser?.id ?: return emptyList()
        return aduanList.filter {
            it.pelaporId == id && it.status == StatusAduan.SELESAI
        }
    }

    fun getAduanForTeknisi(): List<Aduan> {
        val id = currentUser?.id ?: return emptyList()
        return aduanList.filter {
            it.status == StatusAduan.MENUNGGU ||
                    (it.status == StatusAduan.DITANGANI && it.teknisiId == id)
        }
    }

    fun getSelesaiForTeknisi(): List<Aduan> {
        val id = currentUser?.id ?: return emptyList()
        return aduanList.filter {
            it.status == StatusAduan.SELESAI && it.teknisiId == id
        }
    }

    fun getAduanById(id: String): Aduan? = aduanList.find { it.id == id }
}
