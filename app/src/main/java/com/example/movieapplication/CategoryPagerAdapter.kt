package com.example.movieapplication

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class CategoryPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val categories = listOf("upcoming", "popular", "top_rated")

    override fun getItemCount(): Int = categories.size

    override fun createFragment(position: Int): Fragment {
        return MovieListFragment.newInstance(categories[position])
    }
}