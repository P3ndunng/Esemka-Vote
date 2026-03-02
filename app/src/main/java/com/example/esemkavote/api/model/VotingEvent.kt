package com.example.esemkavote.api.model

import com.google.gson.annotations.SerializedName

data class VotingEvent(
    @SerializedName("voting_event_id") val voting_event_id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("start_date") val start_date: String,
    @SerializedName("end_date") val end_date: String,
    @SerializedName("total_voters") val total_voters: Int  // Diubah ke Int agar aman
)