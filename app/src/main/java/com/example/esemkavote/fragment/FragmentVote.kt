package com.example.esemkavote.fragment

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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentVote : Fragment() {

    private lateinit var rvCandidates: RecyclerView
    private lateinit var layoutAction: LinearLayout
    private lateinit var tvSelectedCandidate: TextView
    private lateinit var btnVote: Button
    private var selectedCandidateId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Menghubungkan ke fragment_voting.xml
        return inflater.inflate(R.layout.fragment_voting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inisialisasi View
        rvCandidates = view.findViewById(R.id.rv_candidates)
        layoutAction = view.findViewById(R.id.layout_action_vote)
        tvSelectedCandidate = view.findViewById(R.id.tv_currently_voting)
        btnVote = view.findViewById(R.id.btn_vote)

        // 2. Set Grid 2 Kolom
        rvCandidates.layoutManager = GridLayoutManager(context, 2)

        // 3. Tangkap ID Event dari HomeActivity
        val eventId = arguments?.getInt(HomeActivity.EVENT_ID)

        if (eventId != null) {
            getCandidates(eventId)
        }

        // 4. Logika Tombol Vote
        btnVote.setOnClickListener {
            selectedCandidateId?.let { id ->
                // Panggil fungsi kirim vote ke API di sini
                Toast.makeText(context, "Voting for ID: $id", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCandidates(eventId: Int) {
        ApiClient.instance.getCandidate(eventId).enqueue(object : Callback<List<Candidate>> {
            override fun onResponse(call: Call<List<Candidate>>, response: Response<List<Candidate>>) {
                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()
                    val adapter = CandidateAdapter(list) { candidate ->
                        layoutAction.visibility = View.VISIBLE
                        tvSelectedCandidate.text = "Currently Voting: ${candidate.name}"
                        selectedCandidateId = candidate.id
                    }
                    rvCandidates.adapter = adapter
                }
            }
            override fun onFailure(call: Call<List<Candidate>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}