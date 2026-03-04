package com.example.esemkavote.api.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("employeeId") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("accessToken") val token: String?,
    @SerializedName("division") val division: String?
)