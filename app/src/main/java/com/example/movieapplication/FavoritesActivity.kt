package com.example.movieapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.movieapplication.databinding.ActivityFavoritesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var movieAdapter: MovieAdapter

    private val viewModel: FavoritesViewModel by viewModels { 
        val movieDao = (application as MovieApplication).database.movieDao()
        FavoritesViewModelFactory(movieDao)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Favorite Movies"

        setupRecyclerView()

        lifecycleScope.launch {
            viewModel.favoriteMovies.collect { movies ->
                movieAdapter.updateMovies(movies)
            }
        }
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(mutableListOf()) { movie ->
            val intent = Intent(this, MovieDetailsActivity::class.java).apply {
                putExtra(MovieDetailsActivity.MOVIE_EXTRA, movie)
            }
            startActivity(intent)
        }
        binding.favoritesRecyclerView.apply {
            layoutManager = GridLayoutManager(this@FavoritesActivity, 2)
            adapter = movieAdapter
        }
    }
}

class FavoritesViewModel(movieDao: MovieDao) : ViewModel() {
    val favoriteMovies: Flow<List<Movie>> = movieDao.getAllFavoriteMovies()
}

class FavoritesViewModelFactory(private val movieDao: MovieDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(movieDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

