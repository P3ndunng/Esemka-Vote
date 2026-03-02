package com.example.esemkavote.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.esemkavote.HomeActivity
import com.example.esemkavote.R
import com.example.esemkavote.api.ApiClient
import com.example.esemkavote.api.adapter.CandidateAdapter
import com.example.esemkavote.api.model.Candidate
import com.example.esemkavote.api.model.VoteDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentVote : Fragment() {

    private lateinit var rvCandidates: RecyclerView
    private lateinit var layoutAction: LinearLayout
    private lateinit var tvSelectedCandidate: TextView
    private lateinit var btnVote: Button
    private var selectedCandidateId: Int? = null
    private var hasVoted = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_voting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvCandidates        = view.findViewById(R.id.rv_candidates)
        layoutAction        = view.findViewById(R.id.layout_action_vote)
        tvSelectedCandidate = view.findViewById(R.id.tv_currently_voting)
        btnVote             = view.findViewById(R.id.btn_vote)

        rvCandidates.layoutManager = GridLayoutManager(context, 2)

        val eventId = arguments?.getInt(HomeActivity.EVENT_ID)
        if (eventId != null && eventId != 0) {
            getCandidates(eventId)
        } else {
            Toast.makeText(context, "Event ID tidak valid", Toast.LENGTH_SHORT).show()
        }

        btnVote.setOnClickListener {
            if (hasVoted) return@setOnClickListener
            val candidateId = selectedCandidateId ?: run {
                Toast.makeText(context, "Pilih kandidat terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val token = getToken()
            if (token.isEmpty()) {
                Toast.makeText(context, "Sesi habis, silakan login ulang", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            castVote(token, candidateId)
        }
    }

    private fun getToken(): String {
        val sharedPref = requireActivity()
            .getSharedPreferences("EsemkaPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("TOKEN", "") ?: ""
    }

    private fun getCandidates(eventId: Int) {
        val token = getToken()

        // ← Token sekarang dikirim ke getCandidate()
        ApiClient.instance.getCandidate(token, eventId).enqueue(object : Callback<List<Candidate>> {

            override fun onResponse(call: Call<List<Candidate>>, response: Response<List<Candidate>>) {
                android.util.Log.d("DEBUG_VOTE", "Candidates code: ${response.code()}")

                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()
                    android.util.Log.d("DEBUG_VOTE", "Candidates count: ${list.size}")

                    if (list.isEmpty()) {
                        Toast.makeText(context, "Tidak ada kandidat tersedia", Toast.LENGTH_SHORT).show()
                        return
                    }

                    rvCandidates.adapter = CandidateAdapter(list) { candidate ->
                        layoutAction.visibility = View.VISIBLE
                        tvSelectedCandidate.text = "Memilih: ${candidate.name}"
                        selectedCandidateId = candidate.voting_candidate_id
                    }
                } else {
                    val err = response.errorBody()?.string()
                    android.util.Log.e("DEBUG_VOTE", "Candidates error: $err")
                    Toast.makeText(context, "Gagal memuat kandidat: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Candidate>>, t: Throwable) {
                android.util.Log.e("DEBUG_VOTE", "Candidates failure: ${t.message}")
                Toast.makeText(context, "Koneksi gagal: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun castVote(token: String, candidateId: Int) {
        btnVote.isEnabled = false

        ApiClient.instance.castVote(token, VoteDTO(candidateId)).enqueue(object : Callback<Void> {

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    hasVoted = true
                    Toast.makeText(context, "Vote berhasil dikirim!", Toast.LENGTH_SHORT).show()
                    layoutAction.visibility = View.GONE
                } else {
                    val err = response.errorBody()?.string()
                    android.util.Log.e("DEBUG_VOTE", "Vote error: $err")
                    Toast.makeText(context, "Gagal vote: ${response.code()}", Toast.LENGTH_SHORT).show()
                    btnVote.isEnabled = true
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                android.util.Log.e("DEBUG_VOTE", "Vote failure: ${t.message}")
                Toast.makeText(context, "Koneksi gagal: ${t.message}", Toast.LENGTH_SHORT).show()
                btnVote.isEnabled = true
            }
        })
    }
}