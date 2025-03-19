package com.example.villagermockup3.profile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.villagermockup3.R
import com.example.villagermockup3.login.LoginActivity

class ProfileFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var tvFullName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvHouseNumber: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("user_prefs", 0)

        tvFullName = view.findViewById(R.id.tvFullName)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvPhone = view.findViewById(R.id.tvPhone)
        tvHouseNumber = view.findViewById(R.id.tvHouseNumber)
        val btnEditProfile = view.findViewById<Button>(R.id.btnEditProfile)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        // โหลดข้อมูลจาก SharedPreferences (ถ้าไม่มี ให้ใช้ Mockup)
        loadUserProfile()

        // ปุ่มแก้ไขโปรไฟล์
        btnEditProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        // ปุ่มออกจากระบบ
        btnLogout.setOnClickListener {
            logoutUser()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        loadUserProfile() // โหลดข้อมูลใหม่ทุกครั้งที่กลับมาที่หน้า Profile
    }

    private fun loadUserProfile() {
        val fullName = sharedPreferences.getString("fullName", "สมชาย ใจดี")
        val email = sharedPreferences.getString("email", "somchai@email.com")
        val phone = sharedPreferences.getString("phone", "081-234-5678")
        val houseNumber = sharedPreferences.getString("houseNumber", "บ้านเลขที่ 123/4")

        tvFullName.text = fullName
        tvEmail.text = email
        tvPhone.text = phone
        tvHouseNumber.text = houseNumber
    }

    private fun logoutUser() {
        sharedPreferences.edit().clear().apply()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
