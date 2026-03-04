package com.example.esemkavote.api.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.esemkavote.R
import com.example.esemkavote.api.model.VotingEvent

class VotingEventAdapter(
    private val events: List<VotingEvent>,
    private val onClick: (VotingEvent) -> Unit
) : RecyclerView.Adapter<VotingEventAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView   = view.findViewById(R.id.judul_content)
        val tvDesc: TextView    = view.findViewById(R.id.deskripsi)
        val tvDate: TextView    = view.findViewById(R.id.tanggal)
        val tvVoters: TextView  = view.findViewById(R.id.jumlah_vote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_home, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]

        holder.tvTitle.text   = event.title
        holder.tvDesc.text    = event.description
        holder.tvDate.text    = "${event.startDate} - ${event.endDate}"
        holder.tvVoters.text  = "${event.totalVoters} voters"

        holder.itemView.setOnClickListener { onClick(event) }

        holder.itemView.setOnClickListener {
            android.util.Log.d("DEBUG_VOTE", "Click event id=${event.votingEventId} title=${event.title}")
            onClick(event)
        }

    }

    override fun getItemCount(): Int = events.size
}