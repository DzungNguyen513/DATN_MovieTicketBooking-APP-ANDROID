package com.example.baseappandroid.ui.admin.show_time

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseappandroid.base.viewmodel.BaseViewModel
import com.example.baseappandroid.data.model.Result
import com.example.baseappandroid.data.model.RoomItemModel
import com.example.baseappandroid.data.model.arg.NavigateToBookTicketModel
import com.example.baseappandroid.data.model.view_data_item.DateItemModel
import com.example.baseappandroid.data.model.view_data_item.EditShowTimeItemModel
import com.example.baseappandroid.data.model.view_data_item.MovieInteractType
import com.example.baseappandroid.data.model.view_data_item.MovieItemModel
import com.example.baseappandroid.data.model.view_data_item.SeatItemModel
import com.example.baseappandroid.data.model.view_data_item.WeeklyMovieItemModel
import com.example.baseappandroid.data.model.view_data_item.WeeklyShowTimeItemModel
import com.example.baseappandroid.data.repository.MovieRepository
import com.example.baseappandroid.data.repository.RoomRepository
import com.example.baseappandroid.data.repository.ShowTimeRepository
import com.example.baseappandroid.data.repository.WeeklyDateRepository
import com.example.baseappandroid.utils.Extensions.convertListToString
import com.example.baseappandroid.utils.GlobalFunction.convertLocalDateToString
import com.example.travelappadmin.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class ShowTimeManagementViewModel @Inject constructor(
    private val showTimeRepository: ShowTimeRepository,
    private val dateTimeRepository: WeeklyDateRepository,
    private val movieRepository: MovieRepository,
    private val roomRepository: RoomRepository
) : BaseViewModel() {

    var listOfTimes = arrayListOf<EditShowTimeItemModel>()
    var editMovieId = ""

    private var _addShowTimeAction = MutableLiveData<String?>()
    val addShowTimeAction: LiveData<String?>
        get() = _addShowTimeAction

    private var _deleteShowTimeAction = MutableLiveData<String?>()
    val deleteShowTimeAction: LiveData<String?>
        get() = _deleteShowTimeAction

    fun clearDeleteShowTimeAction() {
        _deleteShowTimeAction.value = null
    }

    private var _editShowTimeAction = MutableLiveData<Event<Pair<String?, String?>>>()
    val editShowTimeAction: LiveData<Event<Pair<String?, String?>>>
        get() = _editShowTimeAction

    private var _editShowTimeResponse = MutableLiveData<Event<String?>>()
    val editShowTimeResponse: LiveData<Event<String?>>
        get() = _editShowTimeResponse

    private var _chosenDate = MutableLiveData<String>()
    val chosenDate: LiveData<String>
        get() = _chosenDate

    private var _listOfDates = MutableLiveData<List<DateItemModel>>()
    val listOfDates: LiveData<List<DateItemModel>>
        get() = _listOfDates

    private var _listOfMovieTimes = MutableLiveData<List<WeeklyMovieItemModel>>()
    val listOfMovieTimes: LiveData<List<WeeklyMovieItemModel>>
        get() = _listOfMovieTimes

    private var _listOfMoviesName = MutableLiveData<List<String>>()
    val listOfMoviesName: LiveData<List<String>>
        get() = _listOfMoviesName
    val listOfMovies: ArrayList<MovieItemModel> = arrayListOf()

    private var _listOfRoomsName = MutableLiveData<List<String>>()
    val listOfRoomsName: LiveData<List<String>>
        get() = _listOfRoomsName
    val listOfRooms: ArrayList<RoomItemModel> = arrayListOf()

    init {
        getDates()
    }

    private fun getDates() {
        viewModelScope.launch {
            _chosenDate.value = LocalDate.now().convertLocalDateToString()
            _listOfDates.value = dateTimeRepository.getWeeklyDates().map { e ->
                e.copy(interact = this@ShowTimeManagementViewModel)
            }
        }
    }

    fun getWeeklyMovies(date: String) {
        viewModelScope.launch {
            when (val result = showTimeRepository.getWeeklyMovieData(date)) {
                is Result.Success -> {
                    _listOfMovieTimes.value = result.data.map { e ->
                        e.copy(
                            movieInteractType = MovieInteractType.ADMIN,
                            timesModel = e.times.orEmpty().mapIndexed { index, t ->
                                WeeklyShowTimeItemModel(
                                    id = index,
                                    time = t.convertListToString(),
                                    interact = this@ShowTimeManagementViewModel,
                                    weeklyItemId = e.id.toString(),
                                )
                            },
                            interact = this@ShowTimeManagementViewModel
                        )
                    }
                }

                is Result.Error -> {
                    Log.d("check err", result.errorMessage)
                }
            }
        }
    }

    fun fetchMovies() {
        viewModelScope.launch {
            when (val result = movieRepository.fetchMoviesData()) {
                is Result.Success -> {
                    listOfMovies.clear()
                    listOfMovies.addAll(result.data)
                    _listOfMoviesName.value = result.data.map { e -> e.title.toString() }
                }

                is Result.Error -> {

                }
            }
        }
    }

    fun fetchRooms() {
        viewModelScope.launch {
            when (val result = roomRepository.fetchAllRooms()) {
                is Result.Success -> {
                    listOfRooms.clear()
                    listOfRooms.addAll(result.data)
                    _listOfRoomsName.value = result.data.map { e -> "Phòng ${e.name.toString()}" }
                }

                is Result.Error -> {

                }
            }
        }
    }

    fun addShowTime(movieId: String, time: String, date: String, roomId: String) {
        viewModelScope.launch {
            val movieDuration = (listOfMovies.find { e -> e.id == movieId }?.duration) ?: 0
            val checkExist =
                showTimeRepository.checkIfExist(time, date, roomId, movieDuration.toInt())
            if (checkExist is Result.Success) {
                if (checkExist.data) {
                    _addShowTimeAction.value = "Giờ chiếu này đã bị trùng, vui lòng chọn giờ khác!"
                } else {
                    when (val result = showTimeRepository.addWeeklyMovie(
                        WeeklyMovieItemModel(
                            id = null,
                            movieId = movieId,
                            date = date,
                            times = listOf(time),
                            interact = null,
                            timesModel = null,
                            movie = null
                        ),
                        SeatItemModel(
                            time = null,
                            roomId = roomId,
                            seats = null,
                            availableSeats = null,
                            bookedSeats = emptyList(),
                            roomName = null
                        ),
                        time,
                        movieDuration.toInt()
                    )) {
                        is Result.Success -> {
                            _addShowTimeAction.value = result.data
                        }

                        is Result.Error -> {
                            _addShowTimeAction.value = "Đã có lỗi xảy ra, vui lòng thử lại!"
                        }
                    }
                }
            } else {
                _addShowTimeAction.value = "Đã có lỗi xảy ra, vui lòng thử lại!"
            }

        }
    }

    fun deleteShowTime(id: String) {
        viewModelScope.launch {
            when (val result = showTimeRepository.deleteWeeklyMovieData(id)) {
                is Result.Success -> {
                    _chosenDate.value?.let { getWeeklyMovies(it) }
                }

                is Result.Error -> {
                    Log.d("CheckError", result.errorMessage)
                }
            }
        }
    }

    override fun chooseMovie(movieArg: NavigateToBookTicketModel) {

    }

    override fun onDateChosen(date: LocalDate) {
        viewModelScope.launch {
            val list = arrayListOf<DateItemModel>()
            list.addAll(
                _listOfDates.value.orEmpty().map { e ->
                    if (e.localDate == date) e.copy(
                        chosen = true,
                        interact = this@ShowTimeManagementViewModel
                    )
                    else e.copy(chosen = false, interact = this@ShowTimeManagementViewModel)
                }
            )
            _chosenDate.value = date.convertLocalDateToString()
            _listOfDates.value = list
        }
    }

    override fun deleteMovieShowTimeAction(id: String) {
        _deleteShowTimeAction.value = id
    }

    override fun editMovieShowTimeAction(time: String, id: String) {
        _editShowTimeAction.value = Event(Pair(time, id))
    }

    override fun removeShowTime(time: String) {
        viewModelScope.launch {
            val position = listOfTimes.find { e -> e.time == time }
            if (position != null) listOfTimes.remove(position)
            if (listOfTimes.isEmpty()) {
                when (val result = showTimeRepository.deleteWeeklyMovieData(editMovieId)) {
                    is Result.Success -> {
                        _editShowTimeResponse.value = Event(result.data)
                        _chosenDate.value?.let { getWeeklyMovies(it) }
                    }

                    is Result.Error -> {
                        _editShowTimeResponse.value = Event(result.errorMessage)
                    }
                }
            } else {
                when (val result =
                    showTimeRepository.updateWeeklyMovieData(
                        editMovieId,
                        listOfTimes.map { e -> e.time },
                        time
                    )) {
                    is Result.Success -> {
                        _editShowTimeResponse.value = Event(result.data)
                        _chosenDate.value?.let { getWeeklyMovies(it) }
                    }

                    is Result.Error -> {
                        _editShowTimeResponse.value = Event(result.errorMessage)
                    }
                }
            }
        }
    }

}