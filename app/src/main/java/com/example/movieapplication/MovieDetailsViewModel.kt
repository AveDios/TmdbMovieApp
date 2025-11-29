package com.example.movieapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovieDetailsViewModel(private val movieDao: MovieDao, private val movieId: Int) : ViewModel() {

    val isFavorite: StateFlow<Boolean> =
        movieDao.getMovieById(movieId)
            .map { it != null }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            if (isFavorite.value) {
                movieDao.delete(movie)
            } else {
                movieDao.insert(movie)
            }
        }
    }
}
