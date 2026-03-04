package com.example.esemkavote.api.services

import com.example.esemkavote.api.model.AuthDTO
import com.example.esemkavote.api.model.Candidate
import com.example.esemkavote.api.model.LoginResponse
import com.example.esemkavote.api.model.VoteDTO
import com.example.esemkavote.api.model.VotingEvent
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("auth")
    fun login(@Body request: AuthDTO): Call<LoginResponse>

    @GET("voting-events")
    fun getEventDetail(@Query("empID") empId: Int): Call<List<VotingEvent>>

    @GET("voting-candidates/{votingEventID}")
    fun getCandidate(
        @Header("Authorization") auth: String,
        @Path("votingEventID") eventId: Int
    ): Call<List<Candidate>>

    @POST("vote/{votingEventID}")
    fun castVote(
        @Header("Authorization") auth: String,
        @Path("votingEventID") eventId: Int,
        @Body voteData: VoteDTO
    ): Call<Void>
}