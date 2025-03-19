package com.example.villagermockup3.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.villagermockup3.MainActivity
import com.example.villagermockup3.R

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

        // ถ้าผู้ใช้เคยล็อกอิน ให้ข้ามไปหน้า MainActivity
        if (sharedPreferences.getBoolean("is_logged_in", false)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (username == "admin" && password == "123456") {
                // บันทึกสถานะล็อกอิน
                sharedPreferences.edit().putBoolean("is_logged_in", true).apply()

                // ไปหน้า MainActivity
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Username หรือ Password ไม่ถูกต้อง", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
