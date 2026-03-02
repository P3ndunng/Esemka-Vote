package com.example.esemkavote.api.model

import com.google.gson.annotations.SerializedName

data class Candidate(
    @SerializedName("voting_candidate_id") val voting_candidate_id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("division") val division: String,
    @SerializedName("photo") val photo: String
)