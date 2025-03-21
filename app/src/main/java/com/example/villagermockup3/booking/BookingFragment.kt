package com.example.villagermockup3.booking

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.villagermockup3.databinding.FragmentBookingBinding
import java.text.SimpleDateFormat
import java.util.*

class BookingFragment : Fragment() {

    private var _binding: FragmentBookingBinding? = null
    private val binding get() = _binding!!

    private lateinit var bookingAdapter: BookingAdapter
    private val bookingList = mutableListOf<BookingItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ตั้งค่า RecyclerView
        bookingAdapter = BookingAdapter(bookingList)
        binding.recyclerViewBooking.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookingAdapter
        }

        // ตั้งค่าปฏิทิน
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
            loadBookingData(selectedDate)
        }

        // โหลดข้อมูลเริ่มต้น (วันที่ปัจจุบัน)
        val currentDate = getCurrentDate()
        loadBookingData(currentDate)
    }

    private fun loadBookingData(date: String) {
        val sharedPreferences = requireContext().getSharedPreferences("BookingHistory", Context.MODE_PRIVATE)
        val bookings = sharedPreferences.getStringSet("bookings", emptySet()) ?: emptySet()

        bookingList.clear()

        Log.d("BookingDebug", "Loaded bookings: $bookings") // ✅ ตรวจสอบค่าที่โหลดมา

        bookings.forEach { booking ->
            val parts = booking.split("|")
            if (parts.size >= 3) {
                val placeName = parts[0].replace("สถานที่: ", "").trim()
                val bookingDate = parts[1].replace("วันที่จอง: ", "").trim()
                val timeRange = parts[2].replace("เวลา: ", "").trim()

                if (bookingDate == date) {
                    val bookingItem = BookingItem(placeName, timeRange, bookingDate)
                    bookingList.add(bookingItem)
                    Log.d("BookingDebug", "Added to list: $bookingItem") // ✅ Debug
                }
            } else {
                Log.e("BookingDebug", "Invalid booking format: $booking") // 🚨 Debug
            }
        }

        bookingAdapter.notifyDataSetChanged()
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
