package com.example.esemkavote.api

import android.os.Build

object ApiHost {
    private const val HOST_EMULATOR = "10.0.2.2"
    private const val HOST_PHONE = "192.168.30.99" // IP laptop kamu

    private fun isEmulator(): Boolean {
        val fp = Build.FINGERPRINT.lowercase()
        val model = Build.MODEL.lowercase()
        val brand = Build.BRAND.lowercase()
        val device = Build.DEVICE.lowercase()
        val product = Build.PRODUCT.lowercase()

        return fp.contains("generic") ||
                model.contains("google_sdk") ||
                model.contains("emulator") ||
                model.contains("android sdk built for") ||
                brand.contains("generic") ||
                device.contains("generic") ||
                product.contains("sdk")
    }

    val host: String
        get() = if (isEmulator()) HOST_EMULATOR else HOST_PHONE

    val baseUrl: String
        get() = "http://$host:5000/api/"

    val imageBaseUrl: String
        get() = "http://$host:5000/images/"
}