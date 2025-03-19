package com.example.villagermockup3.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.villagermockup3.R
import com.example.villagermockup3.place.Place
import com.example.villagermockup3.place.PlaceAdapter

class HomeFragment : Fragment() {

    private lateinit var recyclerViewPlaces: RecyclerView
    private lateinit var recyclerViewNews: RecyclerView
    private lateinit var viewPagerBanner: ViewPager2
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ตั้งค่า Banner (ViewPager2)
        viewPagerBanner = view.findViewById(R.id.viewPagerBanner)
        val bannerImages = listOf(
            R.drawable.banner1,
            R.drawable.banner2,
            R.drawable.banner3
        )
        val bannerAdapter = BannerAdapter(bannerImages)
        viewPagerBanner.adapter = bannerAdapter

        startAutoScrollBanner()

        // ตั้งค่า GridView สำหรับสถานที่
        recyclerViewPlaces = view.findViewById(R.id.recyclerViewPlaces)
        recyclerViewPlaces.layoutManager = GridLayoutManager(requireContext(), 2)

        val places = listOf(
            Place("สนามเทนนิส", R.drawable.ic_tennis),
            Place("ห้องจัดเลี้ยง", R.drawable.ic_roomevent),
            Place("สนามฟุตบอล", R.drawable.ic_soccer),
            Place("โต๊ะพูล", R.drawable.ic_pooltable),
            Place("สระว่ายน้ำ", R.drawable.ic_pool),
            Place("สนามบาสเกตบอล", R.drawable.ic_basketball)
        )

        val placeAdapter = PlaceAdapter(places) { place ->
            val bundle = Bundle().apply {
                putString("placeName", place.name)
                putInt("imageRes", place.imageRes)
            }
            findNavController().navigate(R.id.placeDetailFragment, bundle)
        }

        recyclerViewPlaces.adapter = placeAdapter

        // ตั้งค่า RecyclerView ข่าวสาร
        recyclerViewNews = view.findViewById(R.id.recyclerViewNews)
        recyclerViewNews.layoutManager = LinearLayoutManager(requireContext())

        val newsList = listOf(
            "ประกาศ: ปรับปรุงสนามเทนนิสวันที่ 25 มี.ค. เป็นต้นไป",
        )

        val newsAdapter = NewsAdapter(newsList)
        recyclerViewNews.adapter = newsAdapter
    }

    private fun startAutoScrollBanner() {
        val bannerRunnable = object : Runnable {
            override fun run() {
                if (viewPagerBanner.adapter != null) {
                    currentPage = (currentPage + 1) % (viewPagerBanner.adapter!!.itemCount)
                    viewPagerBanner.setCurrentItem(currentPage, true)
                    handler.postDelayed(this, 3000)
                }
            }
        }
        handler.postDelayed(bannerRunnable, 3000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }
}
