package com.example.villagermockup3.place

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
    private lateinit var tvBookedTimes: TextView // ✅ แสดงเวลาที่ถูกจอง

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
        tvBookedTimes = view.findViewById(R.id.tvBookedTimes) // ✅ แสดงเวลาที่ถูกจอง

        val placeName = arguments?.getString("placeName") ?: "ไม่ระบุ"
        val imageRes = arguments?.getInt("imageRes") ?: R.drawable.ic_placeholder

        tvPlaceName.text = placeName
        ivPlaceImage.setImageResource(imageRes)

        btnSelectDate.setOnClickListener { showDatePicker() }
        btnStartTime.setOnClickListener { showTimePicker(isStartTime = true) }
        btnEndTime.setOnClickListener { showTimePicker(isStartTime = false) }
        btnProceedToPayment.setOnClickListener { navigateToPrePayment(placeName, imageRes) }
    }

    override fun onResume() {
        super.onResume()
        if (selectedDate.isNotEmpty()) {
            loadBookedTimes(selectedDate) // ✅ โหลดเวลาที่ถูกจองทุกครั้งที่กลับมาที่หน้านี้
        }
    }

    private fun showDatePicker() {
        val datePicker = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            selectedDate = dateFormat.format(selectedCalendar.time)
            tvSelectedDate.text = "วันที่: $selectedDate"

            // ✅ โหลดเวลาที่ถูกจองแล้ว
            loadBookedTimes(selectedDate)
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
        datePicker.show()
    }

    private fun showTimePicker(isStartTime: Boolean) {
        if (selectedDate.isEmpty()) {
            showAlertDialog("แจ้งเตือน", "กรุณาเลือกวันที่ก่อน")
            return
        }

        val timePicker = TimePickerDialog(requireContext(), { _, hourOfDay, _ ->
            val formattedTime = String.format(Locale.getDefault(), "%02d:00", hourOfDay)

            // ✅ ป้องกันการเลือกเวลาที่ถูกจองไปแล้ว
            val bookedTimes = getBookedTimes(selectedDate)
            if (bookedTimes.contains(formattedTime)) {
                showAlertDialog("แจ้งเตือน", "เวลานี้ถูกจองไปแล้ว กรุณาเลือกเวลาอื่น")
                return@TimePickerDialog
            }

            if (isStartTime) {
                startTime = formattedTime
                tvSelectedStartTime.text = "เวลาเริ่ม: $startTime"
            } else {
                endTime = formattedTime
                tvSelectedEndTime.text = "จองจนถึงเวลา: $endTime"
            }

            calculateTotalPrice()
        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), 0, true)

        timePicker.show()
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
    }

    private fun getBookedTimes(date: String): Set<String> {
        val sharedPreferences = requireContext().getSharedPreferences("BookingData", Context.MODE_PRIVATE)
        return sharedPreferences.getStringSet(date, emptySet()) ?: emptySet()
    }

    private fun navigateToPrePayment(placeName: String, imageRes: Int) {
        if (selectedDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            showAlertDialog("กรุณากรอกข้อมูลให้ครบ", "โปรดเลือกวันและเวลาจองให้ครบก่อนดำเนินการไปหน้าชำระเงิน")
            return
        }

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
        android.app.AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("ตกลง") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
