package com.example.villagermockup3.booking

import android.os.Bundle
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.villagermockup3.R
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
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            loadBookingData(selectedDate)
        }

        // โหลดข้อมูลเริ่มต้น (วันที่ปัจจุบัน)
        val currentDate = getCurrentDate()
        loadBookingData(currentDate)
    }

    private fun loadBookingData(date: String) {
        // จำลองข้อมูลการจอง
        bookingList.clear()
        val mockData = listOf(
            BookingItem("สนามเทนนิส", "10:00 - 12:00", "19/03/2025"),
            BookingItem("ห้องจัดเลี้ยง", "14:00 - 18:00", "19/03/2025"),
            BookingItem("โต๊ะพูล", "16:00 - 17:30", "20/03/2025")
        )

        bookingList.addAll(mockData.filter { it.date == date })
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
