package com.example.baseappandroid.ui.home.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseappandroid.base.viewmodel.BaseViewModel
import com.example.baseappandroid.data.model.Result
import com.example.baseappandroid.data.model.view_data_item.MovieItemModel
import com.example.baseappandroid.data.model.view_data_item.MovieType
import com.example.baseappandroid.data.repository.MovieRepository
import com.example.travelappadmin.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchMovieViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : BaseViewModel() {

    private var _searchMoviesResponse = MutableLiveData<List<MovieItemModel>>()
    val searchMoviesResponse: LiveData<List<MovieItemModel>>
        get() = _searchMoviesResponse
    private val listOfMovies = arrayListOf<MovieItemModel>()

    private var _clickOnMovieAction: MutableLiveData<Event<String?>> = MutableLiveData()
    val clickOnMovieAction: LiveData<Event<String?>>
        get() = _clickOnMovieAction

    init {
        viewModelScope.launch {
            movieRepository.getAlLMoviesRealTime {
                listOfMovies.clear()
                listOfMovies.addAll(it)
            }
        }
    }

    fun searchMovies(text: String) {
        viewModelScope.launch {
            val searchMovies = arrayListOf<MovieItemModel>()
            for (i in listOfMovies) {
                if (i.title?.lowercase()?.contains(text.lowercase()) == true) {
                    searchMovies.add(i.copy(movieType = MovieType.search, interact = this@SearchMovieViewModel))
                }
            }
            _searchMoviesResponse.value = searchMovies
        }
    }

    override fun clickOnMovieAction(value: String?) {
        _clickOnMovieAction.value = Event(value)
    }
}