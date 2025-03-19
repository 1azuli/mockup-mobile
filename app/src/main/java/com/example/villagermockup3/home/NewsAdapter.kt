package com.example.villagermockup3.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.villagermockup3.R

class NewsAdapter(private val newsList: List<String>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNewsItem: TextView = view.findViewById(R.id.tvNewsItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.tvNewsItem.text = newsList[position]
    }

    override fun getItemCount() = newsList.size
}
