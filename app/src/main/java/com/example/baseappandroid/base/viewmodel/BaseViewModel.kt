package com.example.baseappandroid.base.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.baseappandroid.base.network.BaseNetworkException
import com.example.baseappandroid.base.network.NetworkErrorException
import com.example.baseappandroid.base.recyclerview.HomeInteract
import com.example.baseappandroid.data.model.RoomItemModel
import com.example.baseappandroid.data.model.SnackItemModel
import com.example.baseappandroid.data.model.arg.NavigateToBookTicketModel
import com.example.baseappandroid.data.model.view_data_item.AdminFeature
import com.example.baseappandroid.data.model.view_data_item.WeeklyShowTimeItemModel
import com.example.travelappadmin.common.Event
import kotlinx.coroutines.CoroutineExceptionHandler
import java.time.LocalDate

abstract class BaseViewModel : ViewModel(), HomeInteract {

    var baseNetworkException = MutableLiveData<Event<BaseNetworkException>>()
        protected set

    var networkException = MutableLiveData<Event<NetworkErrorException>>()
        protected set

    val handler = CoroutineExceptionHandler { _, exception ->
        parseErrorCallApi(exception)
    }

    protected open fun parseErrorCallApi(e: Throwable) {
        when (e) {
            is BaseNetworkException -> {
                baseNetworkException.postValue(Event(e))
            }

            is NetworkErrorException -> {
                networkException.postValue(Event(e))
            }

            else -> {
                val unknownException = BaseNetworkException()
                unknownException.mainMessage = e.message ?: "Something went wrong"
                baseNetworkException.postValue(Event(unknownException))
            }
        }
    }

    override fun searchMovieAction() {

    }

    override fun onForwardAction(title: String) {

    }

    override fun clickOnMovieListAction() {

    }

    override fun clickOnMovieAction(value: String?) {

    }

    override fun chooseSeat(id: String) {

    }

    override fun chooseMovie(movieArg: NavigateToBookTicketModel) {

    }

    override fun chooseTime(time: WeeklyShowTimeItemModel) {

    }

    override fun onDateChosen(date: LocalDate) {

    }

    override fun onBillClick(id: String) {

    }

    override fun onFeatureClick(feature: AdminFeature) {

    }

    override fun deleteMovieAction(value: String?) {

    }

    override fun editMovieAction(value: String?) {

    }

    override fun onRoomItemClick(id: RoomItemModel) {

    }

    override fun deleteMovieShowTimeAction(id: String) {

    }

    override fun editMovieShowTimeAction(time: String, id: String) {

    }

    override fun removeShowTime(time: String) {

    }

    override fun onMinus(id: String) {

    }

    override fun onPlus(id: String) {

    }

    override fun deleteSnack(id: String) {

    }

    override fun editSnack(id: String) {

    }

    override fun showSnackDetail(item: SnackItemModel) {

    }
}