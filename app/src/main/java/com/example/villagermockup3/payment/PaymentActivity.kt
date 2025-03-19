package com.example.villagermockup3.payment

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.villagermockup3.R

class PaymentActivity : AppCompatActivity() {

    private lateinit var tvPlaceName: TextView
    private lateinit var tvSelectedDate: TextView
    private lateinit var tvStartTime: TextView
    private lateinit var btnConfirmPayment: Button
    private lateinit var btnCancelPayment: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        tvPlaceName = findViewById(R.id.tvPlaceName)
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        tvStartTime = findViewById(R.id.tvStartTime)
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment)
        btnCancelPayment = findViewById(R.id.btnCancelPayment)

        val placeName = intent.getStringExtra("placeName") ?: "ไม่ระบุ"
        val selectedDate = intent.getStringExtra("selectedDate") ?: "-"
        val startTime = intent.getStringExtra("startTime") ?: "-"

        tvPlaceName.text = "สถานที่: $placeName"
        tvSelectedDate.text = "วันที่จอง: $selectedDate"
        tvStartTime.text = "เวลาเริ่ม: $startTime"

        btnConfirmPayment.setOnClickListener {
            Toast.makeText(this, "ชำระเงินสำเร็จ!", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnCancelPayment.setOnClickListener {
            finish()
        }
    }
}
