package com.example.villagermockup3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.villagermockup3.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ซ่อน ActionBar เพื่อป้องกันการซ้อนกับ Toolbar ของ Fragment
        supportActionBar?.hide()

        // ใช้ NavHostFragment อย่างถูกต้อง
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as? NavHostFragment
        val navController = navHostFragment?.navController

        if (navController != null) {
            binding.bottomNavigationView.setupWithNavController(navController)

            // ตรวจสอบค่า navigateTo และเปิด HomeFragment ถ้าจำเป็น
            val navigateTo = intent.getStringExtra("navigateTo")
            if (navigateTo == "home") {
                navController.navigate(R.id.homeFragment)
            }

            // เพิ่มฟังก์ชันให้ปุ่ม Navigation
            binding.bottomNavigationView.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.homeFragment -> {
                        navController.popBackStack(R.id.homeFragment, false)
                        true
                    }
                    R.id.bookingFragment -> {
                        navController.navigate(R.id.bookingFragment)
                        true
                    }
                    R.id.profileFragment -> {
                        navController.navigate(R.id.profileFragment)
                        true
                    }
                    else -> false
                }
            }
        }
    }
}
