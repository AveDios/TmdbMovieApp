package com.example.movieapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.movieapplication.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var searchAdapter: MovieAdapter
    private lateinit var apiService: TmdbApi

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = RetrofitClient.instance.create(TmdbApi::class.java)

        setupSearchRecyclerView()
        setupViewPager()
        setupTabLayout()
        setupSearchView()
    }

    private fun setupViewPager() {
        val pagerAdapter = CategoryPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
    }

    private fun setupTabLayout() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Upcoming"
                1 -> "Popular"
                2 -> "Top Rated"
                else -> null
            }
        }.attach()
    }

    private fun setupSearchRecyclerView() {
        searchAdapter = MovieAdapter(mutableListOf()) { movie ->
            val intent = Intent(this, MovieDetailsActivity::class.java).apply {
                putExtra(MOVIE_EXTRA, movie)
            }
            startActivity(intent)
        }
        binding.searchRecyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = searchAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchRunnable?.let { handler.removeCallbacks(it) }
                if (!query.isNullOrEmpty()) {
                    searchMovies(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchRunnable?.let { handler.removeCallbacks(it) }

                if (newText.isNullOrEmpty()) {
                    showCategoriesView()
                    return true
                }

                searchRunnable = Runnable { searchMovies(newText) }
                handler.postDelayed(searchRunnable!!, 1000)

                return true
            }
        })
    }

    private fun searchMovies(query: String) {
        showSearchView()
        apiService.searchMovies("fc492a84408390f05354c5e045ae5027", query).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    response.body()?.results?.let { searchAdapter.updateMovies(it) }
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun showSearchView() {
        binding.tabLayout.visibility = View.GONE
        binding.viewPager.visibility = View.GONE
        binding.searchRecyclerView.visibility = View.VISIBLE
    }

    private fun showCategoriesView() {
        binding.searchRecyclerView.visibility = View.GONE
        binding.tabLayout.visibility = View.VISIBLE
        binding.viewPager.visibility = View.VISIBLE
    }

    companion object {
        const val MOVIE_EXTRA = "movie_extra"
    }
}