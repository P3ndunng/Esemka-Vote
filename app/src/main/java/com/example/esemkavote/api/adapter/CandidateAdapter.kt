package com.example.esemkavote.api.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.esemkavote.R
import com.example.esemkavote.api.model.Candidate

class CandidateAdapter(
    private val candidates: List<Candidate>,
    private val onClick: (Candidate) -> Unit
) : RecyclerView.Adapter<CandidateAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivFoto: ImageView = view.findViewById(R.id.foto)
        val tvName: TextView = view.findViewById(R.id.candidate_name)
        val tvDept: TextView = view.findViewById(R.id.candidate_dept)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_candidate, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val candidate = candidates[position]

        holder.tvName.text = candidate.name
        holder.tvDept.text = candidate.division

        Glide.with(holder.itemView.context)
            .load(com.example.esemkavote.api.ApiHost.imageBaseUrl + candidate.photo)
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.ivFoto)

        holder.itemView.setOnClickListener {
            onClick(candidate)
        }

        holder.itemView.setOnClickListener {
            android.util.Log.d("DEBUG_VOTE", "Click candidate id=${candidate.voting_candidate_id} name=${candidate.name}")
            onClick(candidate)
        }

    }

    override fun getItemCount(): Int = candidates.size
}