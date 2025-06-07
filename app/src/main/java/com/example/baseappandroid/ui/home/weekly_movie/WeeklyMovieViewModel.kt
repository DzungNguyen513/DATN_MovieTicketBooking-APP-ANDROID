package com.example.baseappandroid.ui.home.weekly_movie

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseappandroid.base.viewmodel.BaseViewModel
import com.example.baseappandroid.data.model.Result
import com.example.baseappandroid.data.model.arg.NavigateToBookTicketModel
import com.example.baseappandroid.data.model.view_data_item.DateItemModel
import com.example.baseappandroid.data.model.view_data_item.WeeklyMovieItemModel
import com.example.baseappandroid.data.model.view_data_item.WeeklyShowTimeItemModel
import com.example.baseappandroid.data.repository.ShowTimeRepository
import com.example.baseappandroid.data.repository.WeeklyDateRepository
import com.example.baseappandroid.utils.GlobalFunction
import com.example.baseappandroid.utils.GlobalFunction.convertLocalDateToString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class WeeklyMovieViewModel @Inject constructor(
    private val showTimeRepository: ShowTimeRepository,
    private val dateTimeRepository: WeeklyDateRepository
) : BaseViewModel() {
    private var _chosenMovie = MutableLiveData<NavigateToBookTicketModel?>()
    val chosenMovie: LiveData<NavigateToBookTicketModel?>
        get() = _chosenMovie

    fun clearChosenMovie() {
        _chosenMovie.value = null
    }

    private var _chosenDate = MutableLiveData<String>()
    val chosenDate: LiveData<String>
        get() = _chosenDate

    private var _listOfDates = MutableLiveData<List<DateItemModel>>()
    val listOfDates: LiveData<List<DateItemModel>>
        get() = _listOfDates

    private var _listOfMovieTimes = MutableLiveData<List<WeeklyMovieItemModel>>()
    val listOfMovieTimes: LiveData<List<WeeklyMovieItemModel>>
        get() = _listOfMovieTimes

    init {
        getDates()
    }

    private fun getDates() {
        viewModelScope.launch {
            _chosenDate.value = LocalDate.now().convertLocalDateToString()
            _listOfDates.value = dateTimeRepository.getWeeklyDates().map { e ->
                e.copy(interact = this@WeeklyMovieViewModel)
            }
        }
    }

    fun getWeeklyMovies(date: String) {
        viewModelScope.launch {
            when (val result = showTimeRepository.getWeeklyMovieData(date)) {
                is Result.Success -> {
                    _listOfMovieTimes.value = result.data.map { e ->
                        e.copy(timesModel = e.times.orEmpty().mapIndexed { index, t ->
                            WeeklyShowTimeItemModel(
                                id = index,
                                time = t,
                                interact = this@WeeklyMovieViewModel,
                                weeklyItemId = e.id.toString()
                            )
                        })
                    }
                }

                is Result.Error -> {
                    Log.d(" check err", result.errorMessage)
                }
            }
        }
    }

    override fun chooseMovie(movieArg: NavigateToBookTicketModel) {

    }

    override fun chooseTime(time: WeeklyShowTimeItemModel) {
        val navModel =
            _listOfMovieTimes.value.orEmpty().firstOrNull { e -> e.id == time.weeklyItemId }
        if (navModel != null) {
            _chosenMovie.value = NavigateToBookTicketModel(
                movieId = navModel.movieId.toString(),
                movieName = navModel.movie?.title.toString(),
                bookDate = navModel.date.toString(),
                bookTime = time.time,
            )
        }
    }

    override fun onDateChosen(date: LocalDate) {
        viewModelScope.launch {
            val list = arrayListOf<DateItemModel>()
            list.addAll(
                _listOfDates.value.orEmpty().map { e ->
                    if (e.localDate == date) e.copy(
                        chosen = true,
                        interact = this@WeeklyMovieViewModel
                    )
                    else e.copy(chosen = false, interact = this@WeeklyMovieViewModel)
                }
            )
            _chosenDate.value = date.convertLocalDateToString()
            _listOfDates.value = list
        }
    }
}