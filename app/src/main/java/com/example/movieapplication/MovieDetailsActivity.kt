package com.example.movieapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.movieapplication.databinding.ActivityMovieDetailsBinding
import kotlinx.coroutines.launch
import java.util.Locale

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding
    private lateinit var viewModel: MovieDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movie = intent.getParcelableExtra<Movie>(MOVIE_EXTRA)

        if (movie == null) {
            finish()
            return
        }

        val movieDao = (application as MovieApplication).database.movieDao()
        val viewModelFactory = MovieDetailsViewModelFactory(movieDao, movie.id)
        viewModel = ViewModelProvider(this, viewModelFactory)[MovieDetailsViewModel::class.java]

        supportActionBar?.title = movie.title
        binding.movieTitleTextView.text = movie.title
        binding.movieOverviewTextView.text = movie.overview

        binding.movieRatingBar.rating = (movie.voteAverage / 2).toFloat()
        binding.movieRatingTextView.text = String.format(Locale.US, "%.1f/10", movie.voteAverage)
        binding.releaseDateTextView.text = movie.releaseDate

        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
            .into(binding.moviePosterImageView)

        binding.favoriteFab.setOnClickListener {
            viewModel.toggleFavorite(movie)
        }

        lifecycleScope.launch {
            viewModel.isFavorite.collect { isFavorite ->
                if (isFavorite) {
                    binding.favoriteFab.setImageResource(android.R.drawable.btn_star_big_on)
                } else {
                    binding.favoriteFab.setImageResource(android.R.drawable.btn_star_big_off)
                }
            }
        }
    }

    companion object {
        const val MOVIE_EXTRA = "com.example.movieapplication.movie_extra"
    }
}