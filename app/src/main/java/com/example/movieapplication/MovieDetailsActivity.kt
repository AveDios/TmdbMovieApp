package com.example.movieapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.movieapplication.databinding.ActivityMovieDetailsBinding
import java.util.Locale

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movie = intent.getParcelableExtra<Movie>(MOVIE_EXTRA)

        movie?.let {
            supportActionBar?.title = it.title
            binding.movieTitleTextView.text = it.title
            binding.movieOverviewTextView.text = it.overview

            // Set rating and release date
            binding.movieRatingBar.rating = (it.voteAverage / 2).toFloat()
            binding.movieRatingTextView.text = String.format(Locale.US, "%.1f/10", it.voteAverage)
            binding.releaseDateTextView.text = it.releaseDate

            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500${it.posterPath}")
                .into(binding.moviePosterImageView)
        }
    }

    companion object {
        const val MOVIE_EXTRA = "com.example.movieapplication.movie_extra"
    }
}
