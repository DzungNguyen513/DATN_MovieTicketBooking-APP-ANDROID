package com.example.baseappandroid.ui.home.snack_order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseappandroid.base.viewmodel.BaseViewModel
import com.example.baseappandroid.data.model.Result
import com.example.baseappandroid.data.model.SnackItemModel
import com.example.baseappandroid.data.repository.SnackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SnackOrderViewModel @Inject constructor(
    private val snackRepository: SnackRepository
) : BaseViewModel() {
    private var initialMoney = 0.0
    private var _listOfSnacks = MutableLiveData<List<SnackItemModel>>()
    val listOfSnacks: LiveData<List<SnackItemModel>>
        get() = _listOfSnacks

    private var _totalMoneyResponse = MutableLiveData<Double>()
    val totalMoneyResponse: LiveData<Double>
        get() = _totalMoneyResponse

    fun setInitialTotalMoney(value: Double) {
        viewModelScope.launch {
            initialMoney = value
            _totalMoneyResponse.value = value
        }
    }

    private var _showDetailSnackAction = MutableLiveData<SnackItemModel?>()
    val showDetailSnackAction: LiveData<SnackItemModel?>
        get() = _showDetailSnackAction

    fun clearShowDetailSnackAction() {
        viewModelScope.launch {
            _showDetailSnackAction.value = null
        }
    }

    init {
        fetchSnacks()
    }

    private fun fetchSnacks() {
        viewModelScope.launch {
            when (val result = snackRepository.fetchSnacks()) {
                is Result.Success -> {
                    _listOfSnacks.value =
                        result.data.map { e ->
                            e.copy(
                                interact = this@SnackOrderViewModel,
                                chosenCount = 0
                            )
                        }
                }

                is Result.Error -> {
                    Log.d("checkError", result.errorMessage)
                }
            }
        }
    }

    override fun onMinus(id: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val newList = _listOfSnacks.value.orEmpty().toList().map { e ->
                if (e.id == id) {
                    e.copy(chosenCount = if (e.chosenCount == 0) 0 else e.chosenCount - 1)
                } else {
                    e
                }
            }
            var totalMoney = initialMoney
            for (i in newList) {
                totalMoney += (i.chosenCount * (i.price ?: 0.0))
            }
            withContext(Dispatchers.Main) {
                _listOfSnacks.value = newList
                _totalMoneyResponse.value = totalMoney
            }
        }
    }

    override fun onPlus(id: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val newList = _listOfSnacks.value.orEmpty().toList().map { e ->
                if (e.id == id) {
                    e.copy(chosenCount = if (e.chosenCount == 10) 10 else e.chosenCount + 1)
                } else {
                    e
                }
            }
            var totalMoney = initialMoney
            for (i in newList) {
                totalMoney += (i.chosenCount * (i.price ?: 0.0))
            }
            withContext(Dispatchers.Main) {
                _listOfSnacks.value = newList
                _totalMoneyResponse.value = totalMoney
            }
        }
    }

    override fun showSnackDetail(item: SnackItemModel) {
        viewModelScope.launch {
            _showDetailSnackAction.value = item
        }
    }
}