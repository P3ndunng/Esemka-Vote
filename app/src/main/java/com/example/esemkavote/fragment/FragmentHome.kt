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
        val sharedPref = requireActivity()
            .getSharedPreferences("EsemkaPrefs", Context.MODE_PRIVATE)

        val empIdStr = sharedPref.getString("EMP_ID", "1") ?: "1"
        val empId    = empIdStr.toIntOrNull() ?: 1

        android.util.Log.d("DEBUG_VOTE", "Memanggil getEventDetail dengan empID=$empId")

        ApiClient.instance.getEventDetail(empId).enqueue(object : Callback<List<VotingEvent>> {

            override fun onResponse(
                call: Call<List<VotingEvent>>,
                response: Response<List<VotingEvent>>
            ) {
                android.util.Log.d("DEBUG_VOTE", "HTTP Code: ${response.code()}")
                android.util.Log.d("DEBUG_VOTE", "URL: ${response.raw().request.url}")

                if (response.isSuccessful) {
                    val events = response.body()
                    android.util.Log.d("DEBUG_VOTE", "Jumlah events: ${events?.size}")

                    if (events.isNullOrEmpty()) {
                        // Data kosong - coba dengan empID berbeda untuk debug
                        android.util.Log.w("DEBUG_VOTE", "List kosong untuk empID=$empId")
                        tvEmpty.visibility = View.VISIBLE
                        rvEvent.visibility = View.GONE
                        tvEmpty.text = "Tidak ada voting aktif.\n(empID=$empId)"
                    } else {
                        tvEmpty.visibility = View.GONE
                        rvEvent.visibility = View.VISIBLE
                        rvEvent.adapter = VotingEventAdapter(events) { selectedEvent ->
                            (activity as? HomeActivity)?.goToVoting(selectedEvent.voting_event_id)
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown"
                    android.util.Log.e("DEBUG_VOTE", "Error ${response.code()}: $errorBody")
                    Toast.makeText(context, "Gagal: ${response.code()} - $errorBody", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<VotingEvent>>, t: Throwable) {
                android.util.Log.e("DEBUG_VOTE", "Failure: ${t.message}")
                Toast.makeText(context, "Koneksi gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}