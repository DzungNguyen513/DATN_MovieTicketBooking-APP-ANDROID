package com.example.baseappandroid.ui.home.book_ticket

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseappandroid.base.viewmodel.BaseViewModel
import com.example.baseappandroid.data.model.Result
import com.example.baseappandroid.data.model.arg.NavigateToBookTicketModel
import com.example.baseappandroid.data.model.view_data_item.MovieSeatItemModel
import com.example.baseappandroid.data.model.view_data_item.MovieSeatType
import com.example.baseappandroid.data.model.view_data_item.SeatItemModel
import com.example.baseappandroid.data.repository.SeatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookTicketViewModel @Inject constructor(
    private val seatRepository: SeatRepository
) : BaseViewModel() {
    var seatItem: SeatItemModel? = null
    private var _listOfSeatsResponse = MutableLiveData<List<MovieSeatItemModel>>()
    val listOfSeatsResponse: LiveData<List<MovieSeatItemModel>>
        get() = _listOfSeatsResponse

    private var _seatResponse = MutableLiveData<SeatItemModel>()
    val seatResponse: LiveData<SeatItemModel>
        get() = _seatResponse

    private var _chooseSeatAction = MutableLiveData<String?>()
    val chooseSeatAction: LiveData<String?>
        get() = _chooseSeatAction

    fun clearChooseSeatAction() {
        _chooseSeatAction.value = null
    }

    private var idCounter = 0
    private val alphabetList = ('A'..'Z').map { it.toString() }

    private fun getSeats(rows: Int) {
        viewModelScope.launch {
            val list = arrayListOf<MovieSeatItemModel>()
            async(Dispatchers.Default) {
                for (i in 0 until rows) {
                    list.add(
                        MovieSeatItemModel(
                            id = idCounter++,
                            lineText = alphabetList[i],
                            movieSeatType = MovieSeatType.lineText,
                            isChosen = false,
                            interact = this@BookTicketViewModel
                        )
                    )

                    for (j in 1..10) {
                        list.add(
                            MovieSeatItemModel(
                                id = idCounter++,
                                lineText = "${alphabetList[i]}$j",
                                movieSeatType = MovieSeatType.chair,
                                isChosen = false,
                                interact = this@BookTicketViewModel
                            )
                        )
                    }
                    list.add(
                        MovieSeatItemModel(
                            id = idCounter++,
                            lineText = "  ${alphabetList[i]}",
                            movieSeatType = MovieSeatType.lineText,
                            isChosen = false,
                            interact = this@BookTicketViewModel
                        )
                    )
                }
            }.await()
            val information = _seatResponse.value?.bookedSeats.orEmpty()
            val afterCheckedList = arrayListOf<MovieSeatItemModel>()
            if (information.isNotEmpty()) {
                afterCheckedList.addAll(
                    list.map { e ->
                        if (information.contains(e.lineText.toString())) {
                            e.copy(isBooked = true)
                        } else {
                            e
                        }
                    }
                )
            } else {
                afterCheckedList.addAll(list)
            }
            _listOfSeatsResponse.value = afterCheckedList
        }
    }

    fun getSeatsInformation(model: NavigateToBookTicketModel) {
        viewModelScope.launch {
            when (val result = seatRepository.getSeatsInformation(model)) {
                is Result.Success -> {
                    _seatResponse.value = result.data
                    seatItem = result.data
                    getSeats(rows = (result.data.seats ?: 0).toInt() / 10)
                }

                is Result.Error -> {
                    Log.d("check seats", result.errorMessage)
                }
            }
        }
    }

    override fun chooseSeat(seatNumber: String) {
        viewModelScope.launch {
            val list = async {
                _listOfSeatsResponse.value?.map { e ->
                    if (e.lineText == seatNumber) {
                        e.copy(isChosen = !e.isChosen)
                    } else {
                        e
                    }
                }
            }.await()
            _listOfSeatsResponse.value = list.orEmpty().toList()
            _chooseSeatAction.value = seatNumber
        }
    }
}