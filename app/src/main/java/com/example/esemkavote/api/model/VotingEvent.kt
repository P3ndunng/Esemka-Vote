package com.example.esemkavote.api.model

import com.google.gson.annotations.SerializedName

data class VotingEvent(
    @SerializedName("votingEventId") val votingEventId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("totalVoters") val totalVoters: Int
)