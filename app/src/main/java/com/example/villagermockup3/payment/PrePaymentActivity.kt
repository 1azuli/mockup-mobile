package com.example.villagermockup3.payment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.villagermockup3.MainActivity
import com.example.villagermockup3.R

class PrePaymentActivity : AppCompatActivity() {

    private lateinit var tvPlaceName: TextView
    private lateinit var ivPlaceImage: ImageView
    private lateinit var tvSelectedDate: TextView
    private lateinit var tvTimeRange: TextView
    private lateinit var tvTotalPrice: TextView
    private lateinit var ivQrCode: ImageView
    private lateinit var btnUploadProof: Button
    private lateinit var btnProceedToPayment: Button
    private lateinit var btnCancel: Button
    private lateinit var btnByPass: Button // ✅ ปุ่ม ByPass
    private lateinit var uploadedImageView: ImageView

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var sharedPreferences: SharedPreferences
    private var proofImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_payment)

        sharedPreferences = getSharedPreferences("BookingData", Context.MODE_PRIVATE)

        tvPlaceName = findViewById(R.id.tvPlaceName)
        ivPlaceImage = findViewById(R.id.ivPlaceImage)
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        tvTimeRange = findViewById(R.id.tvTimeRange)
        tvTotalPrice = findViewById(R.id.tvTotalPrice)
        ivQrCode = findViewById(R.id.ivQrCode)
        btnUploadProof = findViewById(R.id.btnUploadProof)
        btnProceedToPayment = findViewById(R.id.btnProceedToPayment)
        btnCancel = findViewById(R.id.btnCancel)
        btnByPass = findViewById(R.id.btnByPass) // ✅ เชื่อมปุ่ม ByPass
        uploadedImageView = findViewById(R.id.uploadedImageView)

        val placeName = intent.getStringExtra("placeName") ?: "ไม่ระบุ"
        val imageRes = intent.getIntExtra("imageRes", R.drawable.ic_placeholder)
        val selectedDate = intent.getStringExtra("selectedDate") ?: "-"
        val startTime = intent.getStringExtra("startTime") ?: "-"
        val endTime = intent.getStringExtra("endTime") ?: "-"
        val totalPrice = intent.getIntExtra("totalPrice", 0)

        tvPlaceName.text = "สถานที่: $placeName"
        ivPlaceImage.setImageResource(imageRes)
        tvSelectedDate.text = "วันที่จอง: $selectedDate"
        tvTimeRange.text = "เวลา: $startTime - $endTime"
        tvTotalPrice.text = "ราคารวม: ${totalPrice} บาท"

        btnProceedToPayment.setOnClickListener {
            if (proofImageUri == null) {
                showAlert("กรุณาอัปโหลดหลักฐานการโอนเงินก่อนทำการจอง")
            } else {
                saveBookingData(placeName, selectedDate, startTime, endTime, totalPrice)
                showSuccessDialog()
            }
        }

        btnByPass.setOnClickListener {
            showByPassDialog()
        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnUploadProof.setOnClickListener {
            openFileChooser()
        }
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            proofImageUri = data.data
            uploadedImageView.setImageURI(proofImageUri)
            uploadedImageView.visibility = ImageView.VISIBLE
        }
    }

    private fun showByPassDialog() {
        AlertDialog.Builder(this)
            .setTitle("ยืนยันการข้าม")
            .setMessage("คุณแน่ใจหรือไม่ที่จะทำการจองโดยไม่ต้องใช้หลักฐานการโอนเงิน?")
            .setPositiveButton("ยืนยัน") { _, _ ->
                saveBookingData(
                    tvPlaceName.text.toString(),
                    tvSelectedDate.text.toString(),
                    tvTimeRange.text.toString().split(" - ")[0],
                    tvTimeRange.text.toString().split(" - ")[1],
                    tvTotalPrice.text.toString().replace("ราคารวม: ", "").replace(" บาท", "").toInt()
                )
                showSuccessDialog()
            }
            .setNegativeButton("ยกเลิก", null)
            .show()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("ทำการจองสำเร็จ")
            .setMessage("คุณได้ทำการจองเรียบร้อยแล้ว ขอบคุณที่ใช้บริการ")
            .setPositiveButton("ตกลง") { _, _ ->
                navigateToHome() // ✅ เมื่อกดตกลง ให้กลับไปหน้า Home
            }
            .setCancelable(false)
            .show()
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java) // ✅ กลับไปหน้า MainActivity
        intent.putExtra("navigateTo", "home") // ✅ ส่งค่าไปให้เปิด HomeFragment
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showAlert(message: String) {
        AlertDialog.Builder(this)
            .setTitle("แจ้งเตือน")
            .setMessage(message)
            .setPositiveButton("ตกลง", null)
            .show()
    }

    private fun saveBookingData(placeName: String, date: String, startTime: String, endTime: String, totalPrice: Int) {
        val sharedPreferences = getSharedPreferences("BookingHistory", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val bookingData = "$placeName|$date|$startTime - $endTime"

        val existingBookings = sharedPreferences.getStringSet("bookings", mutableSetOf()) ?: mutableSetOf()
        existingBookings.add(bookingData)

        editor.putStringSet("bookings", existingBookings)
        editor.apply()

        Log.d("BookingDebug", "Saved booking: $bookingData") // ✅ ตรวจสอบค่าที่ถูกบันทึก
    }

}
