package com.example.esemkavote.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        rvEvent.layoutManager = LinearLayoutManager(context)
        getVotingEvents()
    }

    private fun getVotingEvents() {
        ApiClient.instance.getEventDetail().enqueue(object : Callback<List<VotingEvent>> {
            override fun onResponse(call: Call<List<VotingEvent>>, response: Response<List<VotingEvent>>) {
                if (response.isSuccessful) {
                    val events = response.body() ?: emptyList()

                    val adapter = VotingEventAdapter(events) { selectedEvent ->
                        (activity as HomeActivity).goToVoting(selectedEvent.id)
                    }

                    rvEvent.adapter = adapter
                }
            }
            override fun onFailure(call: Call<List<VotingEvent>>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}