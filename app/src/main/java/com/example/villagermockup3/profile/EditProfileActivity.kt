package com.example.villagermockup3.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.villagermockup3.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

        // โหลดข้อมูลปัจจุบัน
        binding.etFullName.setText(sharedPreferences.getString("fullName", ""))
        binding.etEmail.setText(sharedPreferences.getString("email", ""))
        binding.etPhone.setText(sharedPreferences.getString("phone", ""))

        // ปุ่มบันทึกข้อมูล
        binding.btnSave.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putString("fullName", binding.etFullName.text.toString())
            editor.putString("email", binding.etEmail.text.toString())
            editor.putString("phone", binding.etPhone.text.toString())
            editor.apply()

            Toast.makeText(this, "บันทึกข้อมูลเรียบร้อย", Toast.LENGTH_SHORT).show()
            finish()
        }

        // ปุ่มยกเลิก
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
}
