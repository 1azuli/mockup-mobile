package com.example.villagermockup3.place

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.villagermockup3.R
import com.example.villagermockup3.payment.PrePaymentActivity
import java.text.SimpleDateFormat
import java.util.*

class PlaceDetailFragment : Fragment() {

    private lateinit var tvPlaceName: TextView
    private lateinit var ivPlaceImage: ImageView
    private lateinit var btnSelectDate: Button
    private lateinit var btnStartTime: Button
    private lateinit var btnEndTime: Button
    private lateinit var btnProceedToPayment: Button
    private lateinit var tvSelectedDate: TextView
    private lateinit var tvSelectedStartTime: TextView
    private lateinit var tvSelectedEndTime: TextView
    private lateinit var tvTotalPrice: TextView
    private lateinit var tvBookedTimes: TextView

    private lateinit var placeName: String
    private var selectedDate: String = ""
    private var startTime: String = ""
    private var endTime: String = ""
    private val pricePerHour = 150

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_place_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvPlaceName = view.findViewById(R.id.tvPlaceName)
        ivPlaceImage = view.findViewById(R.id.ivPlaceImage)
        btnSelectDate = view.findViewById(R.id.btnSelectDate)
        btnStartTime = view.findViewById(R.id.btnStartTime)
        btnEndTime = view.findViewById(R.id.btnEndTime)
        btnProceedToPayment = view.findViewById(R.id.btnProceedToPayment)
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate)
        tvSelectedStartTime = view.findViewById(R.id.tvSelectedStartTime)
        tvSelectedEndTime = view.findViewById(R.id.tvSelectedEndTime)
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice)
        tvBookedTimes = view.findViewById(R.id.tvBookedTimes)

        placeName = arguments?.getString("placeName") ?: "ไม่ระบุ"
        val imageRes = arguments?.getInt("imageRes") ?: R.drawable.ic_placeholder

        tvPlaceName.text = placeName
        ivPlaceImage.setImageResource(imageRes)

        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        selectedDate = today
        tvSelectedDate.text = "วันที่: $selectedDate"
        loadBookedTimes(selectedDate)

        btnSelectDate.setOnClickListener { showDatePicker() }
        btnStartTime.setOnClickListener { showTimePicker(isStartTime = true) }
        btnEndTime.setOnClickListener { showTimePicker(isStartTime = false) }
        btnProceedToPayment.setOnClickListener { navigateToPrePayment(placeName, imageRes) }
    }

    override fun onResume() {
        super.onResume()
        loadExistingBookings()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val today = calendar.timeInMillis

        val datePicker = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            selectedDate = dateFormat.format(selectedCalendar.time)
            tvSelectedDate.text = "วันที่: $selectedDate"
            loadBookedTimes(selectedDate)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePicker.datePicker.minDate = today
        datePicker.show()
    }

    private fun showTimePicker(isStartTime: Boolean) {
        if (selectedDate.isEmpty()) {
            showAlertDialog("แจ้งเตือน", "กรุณาเลือกวันที่ก่อน")
            return
        }

        val timePicker = TimePickerDialog(requireContext(), { _, hourOfDay, _ ->
            val formattedTime = String.format(Locale.getDefault(), "%02d:00", hourOfDay)
            val bookedTimes = getBookedTimes(selectedDate, placeName)

            if (isStartTime) {
                if (endTime.isNotEmpty() && hourOfDay >= endTime.substring(0, 2).toInt()) {
                    showAlertDialog("แจ้งเตือน", "เวลาเริ่มต้องน้อยกว่าจองจนถึงเวลา")
                    return@TimePickerDialog
                }
                if (!isTimeSlotAvailable(formattedTime, endTime.ifEmpty { formattedTime }, bookedTimes)) {
                    showAlertDialog("แจ้งเตือน", "เวลาที่เลือกทับกับการจองอื่น กรุณาเลือกเวลาใหม่")
                    return@TimePickerDialog
                }
                startTime = formattedTime
                tvSelectedStartTime.text = "เวลาเริ่ม: $startTime"
            } else {
                if (startTime.isNotEmpty() && hourOfDay <= startTime.substring(0, 2).toInt()) {
                    showAlertDialog("แจ้งเตือน", "จองจนถึงเวลาต้องมากกว่าเวลาเริ่ม")
                    return@TimePickerDialog
                }
                if (!isTimeSlotAvailable(startTime.ifEmpty { formattedTime }, formattedTime, bookedTimes)) {
                    showAlertDialog("แจ้งเตือน", "เวลาที่เลือกทับกับการจองอื่น กรุณาเลือกเวลาใหม่")
                    return@TimePickerDialog
                }
                endTime = formattedTime
                tvSelectedEndTime.text = "จองจนถึงเวลา: $endTime"
            }

            calculateTotalPrice()
        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), 0, true)

        timePicker.show()
    }

    private fun isTimeSlotAvailable(selectedStart: String, selectedEnd: String, bookedTimes: Set<String>): Boolean {
        val selectedStartHour = selectedStart.substring(0, 2).toInt()
        val selectedEndHour = selectedEnd.substring(0, 2).toInt()

        for (booked in bookedTimes) {
            val bookedRange = booked.split("-")
            if (bookedRange.size == 2) {
                val bookedStart = bookedRange[0].trim().substring(0, 2).toInt()
                val bookedEnd = bookedRange[1].trim().substring(0, 2).toInt()

                // ตรวจสอบว่าช่วงเวลาที่เลือกซ้อนทับกับช่วงเวลาที่จองไปแล้วหรือไม่
                if ((selectedStartHour >= bookedStart && selectedStartHour < bookedEnd) ||  // เริ่มต้นอยู่ในช่วงที่จอง
                    (selectedEndHour > bookedStart && selectedEndHour <= bookedEnd) || // สิ้นสุดอยู่ในช่วงที่จอง
                    (selectedStartHour <= bookedStart && selectedEndHour >= bookedEnd)) { // ครอบคลุมทั้งช่วง
                    Log.d("BookingCheck", "เวลา $selectedStart - $selectedEnd ทับซ้อนกับ $bookedStart - $bookedEnd")
                    return false
                }
            }
        }
        return true
    }

    private fun saveBooking(placeName: String, date: String, startTime: String, endTime: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("BookingHistory", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val bookings = sharedPreferences.getStringSet("bookings", mutableSetOf()) ?: mutableSetOf()
        val newBooking = "สถานที่: $placeName|วันที่จอง: $date|เวลา: $startTime - $endTime"

        val bookedTimes = getBookedTimes(date, placeName)
        if (!isTimeSlotAvailable(startTime, endTime, bookedTimes)) {
            showAlertDialog("แจ้งเตือน", "ไม่สามารถจองเวลานี้ได้ เนื่องจากมีการจองแล้ว กรุณาเลือกเวลาใหม่")
            return
        }

        bookings.add(newBooking)
        editor.putStringSet("bookings", bookings)
        editor.apply()

        Log.d("BookingDebug", "Saved booking: $newBooking") // ✅ Debug
    }

    private fun calculateTotalPrice() {
        if (startTime.isNotEmpty() && endTime.isNotEmpty()) {
            val startHour = startTime.substring(0, 2).toInt()
            val endHour = endTime.substring(0, 2).toInt()
            val totalHours = endHour - startHour
            val totalPrice = totalHours * pricePerHour
            tvTotalPrice.text = "ราคารวม: $totalPrice บาท"
        }
    }

    private fun loadBookedTimes(date: String) {
        val bookedTimes = getBookedTimes(date)
        if (bookedTimes.isEmpty()) {
            tvBookedTimes.text = "ยังไม่มีการจองในวันนี้"
        } else {
            tvBookedTimes.text = "เวลาที่ถูกจองไปแล้ว:\n" + bookedTimes.joinToString("\n") { "เวลา: $it" }
        }
        loadExistingBookings()
    }

    private fun getBookedTimes(date: String, place: String = placeName): Set<String> {
        val sharedPreferences = requireContext().getSharedPreferences("BookingHistory", Context.MODE_PRIVATE)
        val allBookings = sharedPreferences.getStringSet("bookings", emptySet()) ?: emptySet()

        return allBookings.filter {
            it.contains("วันที่จอง: $date") && it.contains("สถานที่: $place")
        }.map {
            it.split("|").getOrNull(2)?.replace("เวลา: ", "") ?: ""
        }.filter { it.isNotEmpty() }.toSet()
    }

    private fun loadExistingBookings() {
        val sharedPreferences = requireActivity().getSharedPreferences("BookingHistory", Context.MODE_PRIVATE)
        val bookings = sharedPreferences.getStringSet("bookings", emptySet()) ?: emptySet()

        val today = getCurrentDate()
        val currentPlaceName = placeName

        val filteredBookings = bookings.filter {
            it.contains("วันที่จอง: $today") && it.contains(currentPlaceName)
        }

        if (filteredBookings.isNotEmpty()) {
            val displayText = filteredBookings.joinToString("\n") {
                val details = it.split("|")
                val time = details.getOrNull(2)?.replace("เวลา: ", "") ?: "ไม่ระบุ"
                "$currentPlaceName : $time"
            }
            tvBookedTimes.text = displayText
            tvBookedTimes.setTextColor(Color.BLACK)
        } else {
            tvBookedTimes.text = "ยังไม่มีการจองในวันนี้"
            tvBookedTimes.setTextColor(Color.RED)
        }
    }


    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date())
    }


    private fun navigateToPrePayment(placeName: String, imageRes: Int) {
        if (selectedDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            showAlertDialog("แจ้งเตือน", "กรุณากรอกข้อมูลให้ครบก่อนทำรายการ")
            return
        }

        val bookedTimes = getBookedTimes(selectedDate, placeName)
        if (!isTimeSlotAvailable(startTime, endTime, bookedTimes)) {
            showAlertDialog("แจ้งเตือน", "ไม่สามารถจองช่วงเวลานี้ได้ เนื่องจากมีการจองแล้ว กรุณาเลือกเวลาใหม่")
            return
        }

        // ถ้าผ่านการตรวจสอบทั้งหมด ไปที่หน้าชำระเงิน
        val intent = Intent(requireContext(), PrePaymentActivity::class.java).apply {
            putExtra("placeName", placeName)
            putExtra("imageRes", imageRes)
            putExtra("selectedDate", selectedDate)
            putExtra("startTime", startTime)
            putExtra("endTime", endTime)
            putExtra("totalPrice", calculateFinalPrice())
        }
        startActivity(intent)
    }

    private fun calculateFinalPrice(): Int {
        return if (startTime.isNotEmpty() && endTime.isNotEmpty()) {
            val startHour = startTime.substring(0, 2).toInt()
            val endHour = endTime.substring(0, 2).toInt()
            (endHour - startHour) * pricePerHour
        } else 0
    }

    private fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("ตกลง") { dialog, _ -> dialog.dismiss() }
            .show()
    }

}
