package com.example.movieapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapplication.databinding.FragmentMovieListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieListFragment : Fragment() {

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!

    private lateinit var movieAdapter: MovieAdapter
    private lateinit var category: String
    private var currentPage = 1
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            category = it.getString(ARG_CATEGORY) ?: "upcoming"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchMovies(currentPage)
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(mutableListOf()) { movie ->
            val intent = Intent(activity, MovieDetailsActivity::class.java).apply {
                putExtra(MovieDetailsActivity.MOVIE_EXTRA, movie)
            }
            startActivity(intent)
        }
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = movieAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                    if (!isLoading && totalItemCount <= (lastVisibleItemPosition + 2)) {
                        currentPage++
                        fetchMovies(currentPage)
                    }
                }
            })
        }
    }

    private fun fetchMovies(page: Int) {
        isLoading = true
        val api = RetrofitClient.instance.create(TmdbApi::class.java)
        val call = when (category) {
            "popular" -> api.getPopularMovies(BuildConfig.TMDB_API_KEY, page = page)
            "top_rated" -> api.getTopRatedMovies(BuildConfig.TMDB_API_KEY, page = page)
            else -> api.getUpcomingMovies(BuildConfig.TMDB_API_KEY, page = page)
        }

        call.enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    response.body()?.results?.let {
                        if (page == 1) {
                            movieAdapter.updateMovies(it)
                        } else {
                            movieAdapter.addMovies(it)
                        }
                    }
                }
                isLoading = false
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                isLoading = false
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CATEGORY = "category"

        @JvmStatic
        fun newInstance(category: String) =
            MovieListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CATEGORY, category)
                }
            }
    }
}
