package com.example.esemkavote.api.model

import com.google.gson.annotations.SerializedName

data class VoteDTO(
    @SerializedName("votingCandidateId") val votingCandidateId: Int
)