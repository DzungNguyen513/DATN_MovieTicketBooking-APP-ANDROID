package com.example.baseappandroid.ui.admin.statistic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseappandroid.base.viewmodel.BaseViewModel
import com.example.baseappandroid.data.model.BillItemModel
import com.example.baseappandroid.data.model.Result
import com.example.baseappandroid.data.model.TicketStatusType
import com.example.baseappandroid.data.model.view_data_item.MovieItemModel
import com.example.baseappandroid.data.model.view_data_item.StatisticItemViewData
import com.example.baseappandroid.data.repository.BillRepository
import com.example.baseappandroid.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val billRepository: BillRepository,
    private val movieRepository: MovieRepository
) : BaseViewModel() {

    private var _movieNumberResponse = MutableLiveData<Int>()
    val movieNumberResponse: LiveData<Int>
        get() = _movieNumberResponse

    private var _incomeResponse = MutableLiveData<Double>()
    val incomeResponse: LiveData<Double>
        get() = _incomeResponse

    private var _listOfDetailMovies = MutableLiveData<List<StatisticItemViewData>>()
    val listOfDetailMovies: LiveData<List<StatisticItemViewData>>
        get() = _listOfDetailMovies
    private val movies = arrayListOf<MovieItemModel>()
    private val bills = arrayListOf<BillItemModel>()
    fun getStatisticInformation() {
        viewModelScope.launch {
            val movieTask = async {
                try {
                    when (val moviesResult = movieRepository.fetchMoviesData()) {
                        is Result.Success -> {
                            _movieNumberResponse.value = moviesResult.data.size
                            movies.clear()
                            movies.addAll(moviesResult.data)
                        }

                        is Result.Error -> {}
                    }
                } catch (e: Exception) {
                    // xử lý exception nếu cần
                }
            }

            val billTask = async {
                try {
                    when (val billsResult = billRepository.fetchListBills("")) {
                        is Result.Success -> {
                            val confirmedCount = billsResult.data
                                .count { it.status == TicketStatusType.CONFIRM.value }
                            _incomeResponse.value = confirmedCount * 50_000.0
                            bills.clear()
                            bills.addAll(billsResult.data)
                        }

                        is Result.Error -> {}
                    }
                } catch (e: Exception) {
                    // xử lý exception nếu cần
                }
            }
            movieTask.await()
            billTask.await()
            val list = arrayListOf<StatisticItemViewData>()
            for (i in movies) {
                val tickets = (bills.filter { e -> e.movieId == i.id })
                val income = (tickets.filter { e -> e.status == TicketStatusType.CONFIRM.value }).size * 50_000.0
                //if (tickets.isNotEmpty()) {
                    val data = StatisticItemViewData(
                        id = i.id.toString(),
                        movieName = i.title.toString(),
                        numberOfTickets = tickets.size,
                        movieImage = i.posterUrl.toString(),
                        income = income
                    )
                    list.add(data)
                //}
            }
            _listOfDetailMovies.value = list
        }
    }

}