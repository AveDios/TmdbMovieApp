package com.example.movieapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.movieapplication.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var apiService: TmdbApi
    private val apiKey = "fc492a84408390f05354c5e045ae5027"

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupRetrofit()
        setupSearchView()

        fetchUpcomingMovies()
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(emptyList()) { movie ->
            val intent = Intent(this, MovieDetailsActivity::class.java)
            intent.putExtra(MovieDetailsActivity.MOVIE_EXTRA, movie)
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerView.adapter = movieAdapter
    }

    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(TmdbApi::class.java)
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Remove any pending search callbacks
                searchRunnable?.let { handler.removeCallbacks(it) }
                if (!query.isNullOrEmpty()) {
                    // Perform search immediately on submit
                    searchMovies(query)
                } else {
                    fetchUpcomingMovies()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Remove any pending search callbacks
                searchRunnable?.let { handler.removeCallbacks(it) }

                if (newText.isNullOrEmpty()) {
                    // If the text is empty, clear the search and show upcoming movies
                    fetchUpcomingMovies()
                    return true
                }

                // Create a new runnable to perform the search after a delay
                searchRunnable = Runnable {
                    searchMovies(newText)
                }

                // Post the runnable with a 1-second delay
                handler.postDelayed(searchRunnable!!, 1000)

                return true
            }
        })
    }

    private fun fetchUpcomingMovies() {
        apiService.getUpcomingMovies(apiKey).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    val movies = response.body()?.results ?: emptyList()
                    movieAdapter.updateMovies(movies)
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun searchMovies(query: String) {
        apiService.searchMovies(apiKey, query).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    val movies = response.body()?.results ?: emptyList()
                    movieAdapter.updateMovies(movies)
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}
