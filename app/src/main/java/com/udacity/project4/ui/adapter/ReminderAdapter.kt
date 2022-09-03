package com.udacity.project4.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.project4.model.Reminder
import com.udacity.project4.databinding.ReminderItemBinding

class ReminderAdapter(private val onClickListener: OnClickListener) :
    androidx.recyclerview.widget.ListAdapter<Reminder, ReminderAdapter.MyViewHolder>(DiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ReminderItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val reminder = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(reminder)
        }
        holder.bind(reminder)
    }

    inner class MyViewHolder(private var binding: ReminderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(reminder: Reminder) {
            binding.reminder = reminder
            binding.executePendingBindings()
        }


    }

    companion object DiffCallback : DiffUtil.ItemCallback<Reminder>() {
        override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
            return oldItem.id == newItem.id
        }
    }


    class OnClickListener(val clickListener: (reminder: Reminder) -> Unit) {
        fun onClick(reminder: Reminder) = clickListener(reminder)
    }
}