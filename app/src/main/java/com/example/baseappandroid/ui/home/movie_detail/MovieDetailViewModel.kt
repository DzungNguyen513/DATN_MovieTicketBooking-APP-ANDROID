package com.example.baseappandroid.ui.home.movie_detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseappandroid.base.viewmodel.BaseViewModel
import com.example.baseappandroid.data.model.Result
import com.example.baseappandroid.data.model.arg.NavigateToBookTicketModel
import com.example.baseappandroid.data.model.view_data_item.DateItemModel
import com.example.baseappandroid.data.model.view_data_item.MovieItemModel
import com.example.baseappandroid.data.model.view_data_item.WeeklyMovieItemModel
import com.example.baseappandroid.data.model.view_data_item.WeeklyShowTimeItemModel
import com.example.baseappandroid.data.repository.MovieRepository
import com.example.baseappandroid.data.repository.ShowTimeRepository
import com.example.baseappandroid.data.repository.WeeklyDateRepository
import com.example.baseappandroid.utils.GlobalFunction.convertLocalDateToString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val weeklyDateRepository: WeeklyDateRepository,
    private val showTimeRepository: ShowTimeRepository
) : BaseViewModel() {
    private var _movieDetail: MutableLiveData<MovieItemModel> = MutableLiveData()
    val movieDetail: LiveData<MovieItemModel>
        get() = _movieDetail
    var movieDetailId = ""
    private var _listOfDates = MutableLiveData<List<DateItemModel>>()
    val listOfDates: LiveData<List<DateItemModel>>
        get() = _listOfDates

    private var _listOfTimes = MutableLiveData<List<WeeklyShowTimeItemModel>>()
    val listOfTimes: LiveData<List<WeeklyShowTimeItemModel>>
        get() = _listOfTimes

    private var _chosenDate = MutableLiveData<String>()
    val chosenDate: LiveData<String>
        get() = _chosenDate

    private var _chosenMovie = MutableLiveData<NavigateToBookTicketModel?>()
    val chosenMovie: LiveData<NavigateToBookTicketModel?>
        get() = _chosenMovie

    private var detailMovieItem: WeeklyMovieItemModel? = null

    init {
        getDates()
    }

    private fun getDates() {
        viewModelScope.launch {
            _chosenDate.value = LocalDate.now().convertLocalDateToString()
            _listOfDates.value = weeklyDateRepository.getWeeklyDates().map { e ->
                e.copy(interact = this@MovieDetailViewModel)
            }
        }
    }

    fun getMovieTimesForDetailMovie(movieId: String, date: String) {
        viewModelScope.launch {
            when (val result = showTimeRepository.getDetailMovieTimeData(movieId, date)) {
                is Result.Success -> {
                    detailMovieItem = result.data
                    _listOfTimes.value = result.data.times.orEmpty().mapIndexed { index, t ->
                        WeeklyShowTimeItemModel(
                            id = index,
                            time = t,
                            interact = this@MovieDetailViewModel,
                            weeklyItemId = result.data.id.toString()
                        )
                    }
                }

                is Result.Error -> {
                    //log error
                    detailMovieItem = null
                    _listOfTimes.value = emptyList()
                }
            }
        }
    }

    fun getDetailMovie(id: String) {
        viewModelScope.launch {
            when (val result = movieRepository.getDetailMovie(id)) {
                is Result.Success -> {
                    _movieDetail.value = result.data
                    movieDetailId = result.data.id.toString()
                }

                is Result.Error -> {
                    //log error
                }
            }
        }
    }

    override fun chooseTime(time: WeeklyShowTimeItemModel) {
        if (detailMovieItem != null) {
            _chosenMovie.value = NavigateToBookTicketModel(
                movieId = detailMovieItem?.movieId.toString(),
                movieName = detailMovieItem?.movie?.title.toString(),
                bookDate = detailMovieItem?.date.toString(),
                bookTime = time.time,
            )
        }
    }

    fun clearChosenMovie() {
        _chosenMovie.value = null
    }

    override fun onDateChosen(date: LocalDate) {
        viewModelScope.launch {
            val list = arrayListOf<DateItemModel>()
            list.addAll(
                _listOfDates.value.orEmpty().map { e ->
                    if (e.localDate == date) e.copy(
                        chosen = true,
                        interact = this@MovieDetailViewModel
                    )
                    else e.copy(chosen = false, interact = this@MovieDetailViewModel)
                }
            )
            _chosenDate.value = date.convertLocalDateToString()
            _listOfDates.value = list
        }
    }
}