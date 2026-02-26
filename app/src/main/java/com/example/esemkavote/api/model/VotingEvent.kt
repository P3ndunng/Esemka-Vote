package com.example.esemkavote.api.model

data class VotingEvent(
    val id: Int,
    val title: String,
    val description: String,
    val start_date: String,
    val end_date: String,
    val total_voters: String
)