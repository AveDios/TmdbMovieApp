package com.example.movieapplication

import android.app.Application

class MovieApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
