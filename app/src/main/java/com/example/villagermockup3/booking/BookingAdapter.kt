package com.example.villagermockup3.booking

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.villagermockup3.databinding.ItemBookingBinding

class BookingAdapter(private val bookingList: List<BookingItem>) :
    RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(private val binding: ItemBookingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BookingItem) {
            binding.tvPlaceName.text = item.placeName
            binding.tvTimeRange.text = item.timeRange

            Log.d("BookingDebug", "Binding item: ${item.placeName}, ${item.timeRange}") // ✅ ตรวจสอบค่าที่แสดงใน UI
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(bookingList[position])
    }

    override fun getItemCount(): Int = bookingList.size
}

