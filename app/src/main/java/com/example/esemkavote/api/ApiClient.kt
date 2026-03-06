package com.example.esemkavote.api

import android.os.Build
import android.util.Log
import com.example.esemkavote.api.services.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val HOST_EMULATOR = "10.0.2.2"
    private const val HOST_PHONE = "192.168.30.118"

    private fun isEmulator(): Boolean {
        val fp = Build.FINGERPRINT.lowercase()
        val model = Build.MODEL.lowercase()
        val brand = Build.BRAND.lowercase()
        val device = Build.DEVICE.lowercase()
        val product = Build.PRODUCT.lowercase()
        val manufacturer = Build.MANUFACTURER.lowercase()

        return fp.contains("generic") ||
                fp.contains("unknown") ||
                model.contains("google_sdk") ||
                model.contains("emulator") ||
                model.contains("android sdk built for") ||
                manufacturer.contains("genymotion") ||
                (brand.contains("generic") && device.contains("generic")) ||
                product.contains("sdk") ||
                product.contains("sdk_gphone") ||
                product.contains("emulator") ||
                product.contains("simulator")
    }

    private val host: String
        get() {
            val h = if (isEmulator()) HOST_EMULATOR else HOST_PHONE
            Log.d("DEBUG_API", "Host used = $h")
            return h
        }

    val baseUrl: String
        get() = "http://$host:5000/api/"

    val imageBaseUrl: String
        get() = "http://$host:5000/images/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }
}