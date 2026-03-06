package com.example.esemkavote.api.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.esemkavote.R
import com.example.esemkavote.api.model.VotingEvent
import java.text.SimpleDateFormat
import java.util.Locale

class VotingEventAdapter(
    private val events: List<VotingEvent>,
    private val onClick: (VotingEvent) -> Unit
) : RecyclerView.Adapter<VotingEventAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView  = view.findViewById(R.id.judul_content)
        val tvDesc: TextView   = view.findViewById(R.id.deskripsi)
        val tvDate: TextView   = view.findViewById(R.id.tanggal)
        val tvVoters: TextView = view.findViewById(R.id.jumlah_vote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_home, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]

        holder.tvTitle.text = event.title
        holder.tvDesc.text = event.description

        val start = formatDate(event.startDate)
        val end = formatDate(event.endDate)
        holder.tvDate.text = "$start - $end"

        holder.tvVoters.text = "${event.totalVoters} voters"

        holder.itemView.setOnClickListener {
            android.util.Log.d("DEBUG_VOTE", "Click event id=${event.votingEventId} title=${event.title}")
            onClick(event)
        }
    }

    private fun formatDate(date: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val parsed = parser.parse(date)
            if (parsed != null) formatter.format(parsed) else date
        } catch (e: Exception) {
            date
        }
    }

    override fun getItemCount(): Int = events.size
}