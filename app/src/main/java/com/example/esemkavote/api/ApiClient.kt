package com.example.esemkavote.api

import android.os.Build
import com.example.esemkavote.api.services.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val HOST_EMULATOR = "10.0.2.2"
    private const val HOST_PHONE = "192.168.30.99"

    private val host: String
        get() = if (Build.FINGERPRINT.contains("generic", ignoreCase = true)) HOST_EMULATOR else HOST_PHONE

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