package com.example.esemkavote.api.services

import com.example.esemkavote.api.model.AuthDTO
import com.example.esemkavote.api.model.Candidate
import com.example.esemkavote.api.model.LoginResponse
import com.example.esemkavote.api.model.VoteDTO
import com.example.esemkavote.api.model.VotingEvent
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    fun login(@Body request: AuthDTO): Call<LoginResponse>

    @GET("events")
    fun getEventDetail(): Call<List<VotingEvent>>

    @GET("events/{id}/candidates")
    fun getCandidate(@Path("id") eventId: Int): Call<List<Candidate>>

    @POST("vote")
    fun castVote(
        @Header("Authorization") token: String,
        @Body voteData: VoteDTO
    ): Call<Void>
}