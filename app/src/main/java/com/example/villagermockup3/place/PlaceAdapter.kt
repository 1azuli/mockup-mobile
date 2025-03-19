package com.example.villagermockup3.place

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.villagermockup3.R

class PlaceAdapter(private val placeList: List<Place>, private val onItemClick: (Place) -> Unit) :
    RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    class PlaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPlaceName: TextView = view.findViewById(R.id.tvPlaceName) // ✅ ใช้ id ที่ถูกต้อง
        val ivPlaceImage: ImageView = view.findViewById(R.id.ivPlaceImage) // ✅ ใช้ id ที่ถูกต้อง
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = placeList[position]
        holder.tvPlaceName.text = place.name
        holder.ivPlaceImage.setImageResource(place.imageRes)

        holder.itemView.setOnClickListener {
            onItemClick(place)
        }
    }

    override fun getItemCount() = placeList.size
}
