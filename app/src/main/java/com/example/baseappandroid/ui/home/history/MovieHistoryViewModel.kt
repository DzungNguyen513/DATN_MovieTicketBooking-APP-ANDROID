package com.example.baseappandroid.ui.home.history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseappandroid.base.viewmodel.BaseViewModel
import com.example.baseappandroid.data.model.BillItemModel
import com.example.baseappandroid.data.model.Result
import com.example.baseappandroid.data.repository.BillRepository
import com.example.baseappandroid.utils.GlobalData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieHistoryViewModel @Inject constructor(
    private val billRepository: BillRepository
) : BaseViewModel() {

    private var _listOfMoviesHistory = MutableLiveData<List<BillItemModel>>()
    val listOfMoviesHistory: LiveData<List<BillItemModel>>
        get() = _listOfMoviesHistory

    private var _onBillDetailClick = MutableLiveData<String?>()
    val onBillDetailClick: LiveData<String?>
        get() = _onBillDetailClick

    fun clearBillId() {
        _onBillDetailClick.value = null
    }

    fun fetchData() {
        viewModelScope.launch {
            when (val result = billRepository.fetchListBills(GlobalData.user?.id.toString())) {
                is Result.Success -> {
                    Log.d("check hrere","ahdasdsa")
                    _listOfMoviesHistory.value = result.data.map {
                        e -> e.copy(interact = this@MovieHistoryViewModel)
                    }
                }

                is Result.Error -> {
                    // handle error here
                }
            }
        }
    }

    override fun onBillClick(id: String) {
        _onBillDetailClick.value = id
    }
}