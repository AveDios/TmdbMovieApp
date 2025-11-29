package com.example.movieapplication

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

data class MovieResponse(
    val results: List<Movie>
)

@Parcelize
@Entity(tableName = "favorite_movies")
data class Movie(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "title")
    val title: String,
    @SerializedName("poster_path") 
    @ColumnInfo(name = "poster_path")
    val posterPath: String?,
    @ColumnInfo(name = "overview")
    val overview: String,
    @SerializedName("vote_average") 
    @ColumnInfo(name = "vote_average")
    val voteAverage: Double,
    @SerializedName("release_date") 
    @ColumnInfo(name = "release_date")
    val releaseDate: String
) : Parcelable
