package com.masit.hub.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import kotlin.random.Random

// ─── Konfigurasi EmailJS ──────────────────────────────────────────────────────
// Daftar gratis di https://www.emailjs.com
// Ganti nilai di bawah dengan kredensial milik Anda:
object EmailJsConfig {
    const val SERVICE_ID  = "service_6r7fqko"
    const val TEMPLATE_ID = "template_y3o7oqf"
    const val PUBLIC_KEY  = "ysUsL7LLTD2HD-p29"
}

// ─── OTP State ────────────────────────────────────────────────────────────────
object OtpState {
    var kodeOtp: String = ""
    var emailTarget: String = ""
    var expiredAt: Long = 0L               // timestamp ms, berlaku 10 menit

    fun generate(email: String): String {
        kodeOtp   = Random.nextInt(100000, 999999).toString()
        emailTarget = email
        expiredAt = System.currentTimeMillis() + 10 * 60 * 1000   // 10 menit
        return kodeOtp
    }

    fun verify(input: String): VerifyResult {
        return when {
            System.currentTimeMillis() > expiredAt -> VerifyResult.EXPIRED
            input == kodeOtp                       -> VerifyResult.VALID
            else                                   -> VerifyResult.INVALID
        }
    }

    fun clear() {
        kodeOtp = ""; emailTarget = ""; expiredAt = 0L
    }
}

enum class VerifyResult { VALID, INVALID, EXPIRED }

// ─── OTP Service ─────────────────────────────────────────────────────────────
object OtpService {
    private val client = OkHttpClient()

    // Return true jika email berhasil dikirim
    suspend fun kirimOtp(email: String): Boolean = withContext(Dispatchers.IO) {
        val otp = OtpState.generate(email)

        val json = JSONObject().apply {
            put("service_id",  EmailJsConfig.SERVICE_ID)
            put("template_id", EmailJsConfig.TEMPLATE_ID)
            put("user_id",     EmailJsConfig.PUBLIC_KEY)
            put("template_params", JSONObject().apply {
                put("to_email", email)
                put("otp_code", otp)
                put("app_name", "MAS IT")
            })
        }

        val body = json.toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://api.emailjs.com/api/v1.0/email/send")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()

        return@withContext try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            android.util.Log.d("OtpService", "Status: ${response.code}")
            android.util.Log.d("OtpService", "Body: $responseBody")
            response.isSuccessful
        } catch (e: Exception) {
            android.util.Log.e("OtpService", "Exception: ${e.message}", e)
            false
        }
    }
}
