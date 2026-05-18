package com.example.grameen_light.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grameen_light.database.PoleEntity
import com.example.grameen_light.databinding.ItemReportBinding

class TrackerAdapter(private var poles: List<PoleEntity>) : RecyclerView.Adapter<TrackerAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemReportBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pole = poles[position]
        holder.binding.tvItemComplaintId.text = "Complaint ID: ${pole.complaintId}"
        holder.binding.tvItemPoleId.text = "Pole ID: ${pole.poleId}"
        holder.binding.tvItemStatus.text = "Status: ${pole.status}"
    }

    override fun getItemCount(): Int = poles.size

    fun updateData(newPoles: List<PoleEntity>) {
        poles = newPoles
        notifyDataSetChanged()
    }
}
