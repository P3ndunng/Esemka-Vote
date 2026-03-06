package com.example.esemkavote.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
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

class FragmentHome : Fragment(R.layout.fragment_home) {

    private lateinit var rvEvents: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvEvents = view.findViewById(R.id.rv_voting_events)

        rvEvents.layoutManager = LinearLayoutManager(context)

        loadEvents()
    }

    fun loadEvents() {

        val prefs = requireContext()
            .getSharedPreferences("EsemkaPrefs", Context.MODE_PRIVATE)

        val empId = prefs.getInt("EMP_ID", 0)

        ApiClient.instance.getEventDetail(empId)
            .enqueue(object : Callback<List<VotingEvent>> {

                override fun onResponse(
                    call: Call<List<VotingEvent>>,
                    response: Response<List<VotingEvent>>
                ) {

                    if (!response.isSuccessful) {
                        Toast.makeText(context, "Gagal mengambil event", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val events = response.body().orEmpty()

                    rvEvents.adapter = VotingEventAdapter(events) { event ->

                        (activity as? HomeActivity)?.goToVoting(event.votingEventId)

                    }
                }

                override fun onFailure(call: Call<List<VotingEvent>>, t: Throwable) {

                    Toast.makeText(context, "Koneksi gagal: ${t.message}", Toast.LENGTH_SHORT).show()

                }
            })
    }
}