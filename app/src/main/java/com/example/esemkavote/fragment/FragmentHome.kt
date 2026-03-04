package com.example.esemkavote.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.esemkavote.HomeActivity
import com.example.esemkavote.R
import com.example.esemkavote.api.ApiClient
import com.example.esemkavote.api.adapter.VotingEventAdapter
import com.example.esemkavote.api.model.VotingEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentHome : Fragment() {

    private lateinit var rvEvent: RecyclerView
    private lateinit var tvEmpty: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvEvent = view.findViewById(R.id.rv_voting_events)
        tvEmpty = view.findViewById(R.id.tv_empty_state)
        rvEvent.layoutManager = LinearLayoutManager(context)

        getVotingEvents()
    }

    private fun getVotingEvents() {
        val prefs = requireActivity().getSharedPreferences("EsemkaPrefs", Context.MODE_PRIVATE)
        val empId = prefs.getInt("EMP_ID", 0)

        android.util.Log.d("DEBUG_VOTE", "getEventDetail empID=$empId")

        if (empId == 0) {
            tvEmpty.visibility = View.VISIBLE
            rvEvent.visibility = View.GONE
            tvEmpty.text = "Sesi login tidak valid. Silakan login ulang."
            return
        }

        ApiClient.instance.getEventDetail(empId).enqueue(object : Callback<List<VotingEvent>> {
            override fun onResponse(call: Call<List<VotingEvent>>, response: Response<List<VotingEvent>>) {
                android.util.Log.d("DEBUG_VOTE", "HTTP ${response.code()} URL=${response.raw().request.url}")

                if (response.isSuccessful) {
                    val events = response.body().orEmpty()
                    android.util.Log.d("DEBUG_VOTE", "events.size=${events.size}")

                    if (events.isEmpty()) {
                        tvEmpty.visibility = View.VISIBLE
                        rvEvent.visibility = View.GONE
                        tvEmpty.text = "Tidak ada voting aktif.\n(empID=$empId)"
                    } else {
                        tvEmpty.visibility = View.GONE
                        rvEvent.visibility = View.VISIBLE
                        rvEvent.adapter = VotingEventAdapter(events) { selected ->
                            (activity as? HomeActivity)?.goToVoting(selected.votingEventId)
                        }
                    }
                } else {
                    val err = response.errorBody()?.string()
                    android.util.Log.e("DEBUG_VOTE", "errorBody=$err")
                    Toast.makeText(context, "Gagal: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<VotingEvent>>, t: Throwable) {
                android.util.Log.e("DEBUG_VOTE", "failure=${t.message}", t)
                Toast.makeText(context, "Koneksi gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}