package com.masit.hub.data

import androidx.compose.runtime.mutableStateListOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ─── Models ───────────────────────────────────────────────────────────────────

data class User(
    val id: String,
    val namaLengkap: String,
    val idKaryawan: String,
    val email: String,
    val role: UserRole,
    val department: String,
    val username: String,
    val password: String
)

enum class UserRole { USER, TEKNISI }

data class Aduan(
    val id: String,
    val ticketNumber: String,
    val judul: String,
    val kategori: KategoriAduan,
    val lokasi: String,
    val deskripsi: String,
    val status: StatusAduan,
    val pelapor: String,
    val pelaporId: String,
    val waktu: String,
    val urutan: Int,
    val fotoUris: List<String> = emptyList(),
    val teknisiId: String? = null,             // id teknisi yang menangani, null = belum ditangani
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

    // mutableStateListOf agar perubahan password langsung reaktif
    val userList = mutableStateListOf(
        User(
            id = "u001",
            namaLengkap = "Budi Santoso",
            idKaryawan = "HR-12345",
            email = "ardiashtegar125@gmail.com",
            role = UserRole.USER,
            department = "HR Department",
            username = "budi.s",
            password = "password123"
        ),
        User(
            id = "u002",
            namaLengkap = "Ahmad Teknisi",
            idKaryawan = "IT-00123",
            email = "ahmad.t@company.id",
            role = UserRole.TEKNISI,
            department = "IT Department",
            username = "ahmad.t",
            password = "password123"
        ),
        User(
            id = "u003",
            namaLengkap = "Sari Wulandari",
            idKaryawan = "DS-00456",
            email = "sari.w@company.id",
            role = UserRole.USER,
            department = "Design Department",
            username = "sari.w",
            password = "password123"
        )
    )

    // mutableStateListOf agar Compose otomatis recompose saat list berubah
    val aduanList = mutableStateListOf(
        Aduan(
            id = "a001",
            ticketNumber = "#20260601-003",
            judul = "WiFi Kantor Lantai 3 Mati",
            kategori = KategoriAduan.JARINGAN,
            lokasi = "Lantai 3, R.05",
            deskripsi = "Seluruh perangkat di ruang 05 tidak dapat terhubung ke jaringan WiFi sejak pagi hari.",
            status = StatusAduan.DITANGANI,
            pelapor = "Budi S.",
            pelaporId = "u001",
            waktu = "2 jam lalu",
            urutan = 3,
            riwayatUpdate = listOf(
                RiwayatUpdate(StatusAduan.DITANGANI, "-", "Hari ini, 10:45"),
                RiwayatUpdate(StatusAduan.MENUNGGU, "Aduan dibuat", "Hari ini, 10:30")
            )
        ),
        Aduan(
            id = "a002",
            ticketNumber = "#20260601-002",
            judul = "Printer Error Tidak Bisa Print",
            kategori = KategoriAduan.HARDWARE,
            lokasi = "Lantai 3, R.07",
            deskripsi = "Printer di ruang 07 menampilkan error code E2 dan tidak bisa mencetak dokumen apapun.",
            status = StatusAduan.MENUNGGU,
            pelapor = "Budi S.",
            pelaporId = "u001",
            waktu = "3 jam lalu",
            urutan = 2,
            riwayatUpdate = listOf(
                RiwayatUpdate(StatusAduan.MENUNGGU, "Aduan dibuat", "Hari ini, 09:00")
            )
        ),
        Aduan(
            id = "a003",
            ticketNumber = "#20260601-001",
            judul = "Komputer Tidak Bisa Menyala",
            kategori = KategoriAduan.HARDWARE,
            lokasi = "Lantai 2, R.12",
            deskripsi = "Komputer di meja kerja tidak menyala sama sekali setelah weekend.",
            status = StatusAduan.SELESAI,
            pelapor = "Budi S.",
            pelaporId = "u001",
            waktu = "1 Jun 2026",
            urutan = 1,
            riwayatUpdate = listOf(
                RiwayatUpdate(StatusAduan.SELESAI, "Power supply diganti, komputer normal kembali.", "1 Jun 2026, 15:00"),
                RiwayatUpdate(StatusAduan.DITANGANI, "Sedang dicek power supply", "1 Jun 2026, 13:00"),
                RiwayatUpdate(StatusAduan.MENUNGGU, "Aduan dibuat", "1 Jun 2026, 08:00")
            )
        ),
        Aduan(
            id = "a004",
            ticketNumber = "#20260528-012",
            judul = "Update Software Desain",
            kategori = KategoriAduan.SOFTWARE,
            lokasi = "Lantai 1, R.03",
            deskripsi = "Software desain perlu diupdate ke versi terbaru untuk mendukung format file baru.",
            status = StatusAduan.SELESAI,
            pelapor = "Sari W.",
            pelaporId = "u003",
            waktu = "28 Mei 2026",
            urutan = 0,
            riwayatUpdate = listOf(
                RiwayatUpdate(StatusAduan.SELESAI, "Software berhasil diupdate ke versi 2.5.1", "28 Mei 2026, 14:00"),
                RiwayatUpdate(StatusAduan.MENUNGGU, "Aduan dibuat", "28 Mei 2026, 09:30")
            )
        )
    )

    fun login(username: String, password: String): User? {
        val user = userList.find { it.username == username && it.password == password }
        currentUser = user
        return user
    }

    fun logout() {
        currentUser = null
    }

    // ── Reset password via OTP (lupa password) ────────────────────────────────
    // Cari user berdasarkan email yang sudah diverifikasi OTP
    fun resetPassword(email: String, passwordBaru: String): Boolean {
        val index = userList.indexOfFirst {
            it.email.equals(email, ignoreCase = true)
        }
        if (index == -1) return false
        val updatedUser = userList[index].copy(password = passwordBaru)
        userList[index] = updatedUser
        // Jika user ini sedang login, update currentUser juga
        if (currentUser?.email?.equals(email, ignoreCase = true) == true) {
            currentUser = updatedUser
        }
        return true
    }
    // Return: true = berhasil, false = password lama salah
    fun gantiPassword(passwordLama: String, passwordBaru: String): Boolean {
        val user = currentUser ?: return false
        val index = userList.indexOfFirst { it.id == user.id }
        if (index == -1) return false
        if (userList[index].password != passwordLama) return false
        val updatedUser = userList[index].copy(password = passwordBaru)
        userList[index] = updatedUser
        currentUser = updatedUser   // update referensi currentUser juga
        return true
    }

    // ── Tambah aduan baru dari form BuatAduan ─────────────────────────────────
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
        val todayTickets = aduanList.count { it.ticketNumber.contains(dateStr) }
        val ticketNumber = "#$dateStr-${String.format("%03d", todayTickets + 1)}"
        val waktuTampil = "Baru saja"
        val nextUrutan = (aduanList.maxOfOrNull { it.urutan } ?: 0) + 1

        val aduan = Aduan(
            id = "a${now}",
            ticketNumber = ticketNumber,
            judul = judul,
            kategori = kategori,
            lokasi = lokasi,
            deskripsi = deskripsi,
            status = StatusAduan.MENUNGGU,
            pelapor = "${user.namaLengkap.split(" ").first()} ${user.namaLengkap.split(" ").getOrNull(1)?.first() ?: ""}.",
            pelaporId = user.id,
            waktu = waktuTampil,
            urutan = nextUrutan,
            fotoUris = fotoUris,
            riwayatUpdate = listOf(
                RiwayatUpdate(StatusAduan.MENUNGGU, "Aduan dibuat", waktuTampil)
            )
        )
        aduanList.add(0, aduan)
    }

    // ── Batalkan aduan (hanya saat status Menunggu) ───────────────────────────
    fun batalkanAduan(id: String) {
        val index = aduanList.indexOfFirst { it.id == id }
        if (index == -1) return
        val aduan = aduanList[index]
        if (aduan.status != StatusAduan.MENUNGGU) return
        val waktuSekarang = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        aduanList[index] = aduan.copy(
            status = StatusAduan.BATAL,
            riwayatUpdate = listOf(
                RiwayatUpdate(StatusAduan.BATAL, "Dibatalkan oleh pelapor", "Hari ini, $waktuSekarang")
            ) + aduan.riwayatUpdate
        )
    }

    // ── Filter functions ──────────────────────────────────────────────────────

    fun getAduanForUser(): List<Aduan> {
        val userId = currentUser?.id ?: return emptyList()
        return aduanList.filter { aduan ->
            aduan.pelaporId == userId &&
                    (aduan.status == StatusAduan.MENUNGGU ||
                            aduan.status == StatusAduan.DITANGANI ||
                            aduan.status == StatusAduan.BATAL)
        }
    }

    fun getSelesaiForUser(): List<Aduan> {
        val userId = currentUser?.id ?: return emptyList()
        return aduanList.filter { aduan ->
            aduan.pelaporId == userId && aduan.status == StatusAduan.SELESAI
        }
    }

    fun getAduanForTeknisi(): List<Aduan> {
        val teknisiId = currentUser?.id ?: return emptyList()
        return aduanList.filter { aduan ->
            // Tampilkan: semua MENUNGGU (belum ada yang tangani)
            // + DITANGANI oleh teknisi ini saja
            aduan.status == StatusAduan.MENUNGGU ||
                    (aduan.status == StatusAduan.DITANGANI && aduan.teknisiId == teknisiId)
        }
    }

    fun getSelesaiForTeknisi(): List<Aduan> {
        val teknisiId = currentUser?.id ?: return emptyList()
        // Hanya riwayat yang ditangani oleh teknisi ini
        return aduanList.filter { aduan ->
            aduan.status == StatusAduan.SELESAI && aduan.teknisiId == teknisiId
        }
    }

    // ── Tangani aduan: ubah status MENUNGGU → DITANGANI, catat teknisiId ─────
    fun tanganiAduan(id: String) {
        val teknisi = currentUser ?: return
        val index = aduanList.indexOfFirst { it.id == id }
        if (index == -1) return
        val aduan = aduanList[index]
        if (aduan.status != StatusAduan.MENUNGGU) return
        val waktu = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        aduanList[index] = aduan.copy(
            status = StatusAduan.DITANGANI,
            teknisiId = teknisi.id,
            riwayatUpdate = listOf(
                RiwayatUpdate(StatusAduan.DITANGANI, "Ditangani oleh ${teknisi.namaLengkap}", "Hari ini, $waktu")
            ) + aduan.riwayatUpdate
        )
    }

    fun getAduanById(id: String): Aduan? = aduanList.find { it.id == id }

    // ── Update status + catatan dari teknisi ──────────────────────────────────
    fun updateAduan(id: String, statusBaru: StatusAduan, catatan: String) {
        val teknisi = currentUser ?: return
        val index = aduanList.indexOfFirst { it.id == id }
        if (index == -1) return
        val aduan = aduanList[index]
        val waktu = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        // Set teknisiId saat pertama kali ditangani (dari MENUNGGU)
        val teknisiId = if (aduan.teknisiId == null) teknisi.id else aduan.teknisiId

        aduanList[index] = aduan.copy(
            status = statusBaru,
            teknisiId = teknisiId,
            riwayatUpdate = listOf(
                RiwayatUpdate(
                    status = statusBaru,
                    catatan = catatan.ifBlank { "-" },
                    waktu = "Hari ini, $waktu"
                )
            ) + aduan.riwayatUpdate
        )
    }
}
