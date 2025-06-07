package com.example.baseappandroid.ui.home.main_home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView.LayoutParams
import com.example.baseappandroid.base.recyclerview.BaseViewData
import com.example.baseappandroid.base.viewmodel.BaseViewModel
import com.example.baseappandroid.data.model.Result
import com.example.baseappandroid.data.model.view_data_item.BoxItemModel
import com.example.baseappandroid.data.model.view_data_item.HeaderTitleItemModel
import com.example.baseappandroid.data.model.view_data_item.MovieItemModel
import com.example.baseappandroid.data.model.view_data_item.MovieModel
import com.example.baseappandroid.data.model.view_data_item.MovieType
import com.example.baseappandroid.data.model.view_data_item.SearchItemModel
import com.example.baseappandroid.data.repository.MovieRepository
import com.example.baseappandroid.data.repository.ShowTimeRepository
import com.example.baseappandroid.utils.GlobalData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainHomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val showTimeRepository: ShowTimeRepository
) : BaseViewModel() {
    private var idIndex = 0
    private var _searchAction: MutableLiveData<Boolean> = MutableLiveData()
    val searchAction: LiveData<Boolean>
        get() = _searchAction

    fun setSearchAction(value: Boolean) {
        _searchAction.value = value
    }

    private var _clickOnMovieAction: MutableLiveData<String?> = MutableLiveData()
    val clickOnMovieAction: LiveData<String?>
        get() = _clickOnMovieAction

    fun setClickOnMovieAction(value: String?) {
        _clickOnMovieAction.value = null
    }

    private val items = arrayListOf<BaseViewData>()
    private var _listOfItems = MutableLiveData<List<BaseViewData>>()
    val listOfItems: LiveData<List<BaseViewData>>
        get() = _listOfItems

    private val listOfMovies = arrayListOf<MovieItemModel>()

    init {
        items.clear()
        idIndex = 0
        getSearchItem()
        viewModelScope.launch {
            getAllMovies()
            getPopularMovies()
            getNewMovies()
            getComingSoonMovies()
        }
    }

    private suspend fun getAllMovies() {
        when (val result = movieRepository.fetchMoviesData()) {
            is Result.Success -> {
                listOfMovies.clear()
                listOfMovies.addAll(result.data)
            }

            is Result.Error -> {
                // show error here
            }
        }
    }

    private fun getSearchItem() {
        items.add(SearchItemModel(idIndex++, interact = this@MainHomeViewModel))
        items.add(BoxItemModel(idIndex++, height = 60, width = null))
        _listOfItems.value = items.toList()
    }

    private fun getPopularMovies() {
        val list = arrayListOf<MovieItemModel>()
        items.add(
            HeaderTitleItemModel(
                idIndex++,
                interact = this@MainHomeViewModel,
                title = "Phim Phổ biến",
                showForward = true
            )
        )
        items.add(BoxItemModel(idIndex++, height = 45, width = null))
        list.addAll(listOfMovies.filter { e -> e.type == MovieType.popular.num }.map { e ->
            e.copy(movieType = MovieType.popular, interact = this@MainHomeViewModel)
        }.take(15))
        items.add(MovieModel(idIndex++, list, this, listType = MovieType.popular))
        _listOfItems.value = items.toList()
    }

    private fun getNewMovies() {
        items.add(
            BoxItemModel(
                idIndex++,
                height = 45,
                width = null,
                backgroundColor = null
            )
        )
        items.add(
            BoxItemModel(
                idIndex++,
                height = 10,
                width = LayoutParams.MATCH_PARENT,
                backgroundColor = GlobalData.dividerBackgroundColor
            )
        )
        items.add(
            BoxItemModel(
                idIndex++,
                height = 45,
                width = null,
                backgroundColor = null
            )
        )
        val list = arrayListOf<MovieItemModel>()
        items.add(
            HeaderTitleItemModel(
                idIndex++,
                interact = this@MainHomeViewModel,
                title = "Phim mới",
                showForward = true
            )
        )
        items.add(BoxItemModel(idIndex++, height = 40, width = null))
        list.addAll(listOfMovies.filter { e -> e.type == MovieType.newMovie.num }.map { e ->
            e.copy(movieType = MovieType.newMovie, interact = this@MainHomeViewModel)
        }.take(10))
        items.add(
            MovieModel(
                idIndex++,
                list,
                this,
                listType = MovieType.newMovie
            )
        )
        _listOfItems.value = items.toList()
    }

    private fun getComingSoonMovies() {
        items.add(
            BoxItemModel(
                idIndex++,
                height = 45,
                width = null,
                backgroundColor = null
            )
        )
        items.add(
            BoxItemModel(
                idIndex++,
                height = 10,
                width = LayoutParams.MATCH_PARENT,
                backgroundColor = GlobalData.dividerBackgroundColor
            )
        )
        items.add(
            BoxItemModel(
                idIndex++,
                height = 45,
                width = null,
                backgroundColor = null
            )
        )
        val list = arrayListOf<MovieItemModel>()
        items.add(
            HeaderTitleItemModel(
                idIndex++,
                interact = this@MainHomeViewModel,
                title = "Phim sắp chiếu",
                showForward = true
            )
        )
        items.add(BoxItemModel(idIndex++, height = 45, width = null))
        list.addAll(listOfMovies.filter { e -> e.type == MovieType.comingSoon.num }.map { e ->
            e.copy(movieType = MovieType.comingSoon, interact = this@MainHomeViewModel)
        }.take(10))
        items.add(
            MovieModel(
                idIndex++,
                list,
                this,
                listType = MovieType.comingSoon
            )
        )
        _listOfItems.value = items.toList()
    }

    override fun searchMovieAction() {
        _searchAction.value = true
    }

    override fun clickOnMovieAction(value: String?) {
        _clickOnMovieAction.value = value
    }

    override fun clickOnMovieListAction() {
        _clickOnMovieAction.value = "abc"
    }
}