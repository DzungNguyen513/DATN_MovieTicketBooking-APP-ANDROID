package com.example.baseappandroid.ui.home.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseappandroid.base.viewmodel.BaseViewModel
import com.example.baseappandroid.data.model.BookTicketModel
import com.example.baseappandroid.data.model.request.FcmMessageRequest
import com.example.baseappandroid.data.repository.FCMRepository
import com.example.baseappandroid.data.repository.SeatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val seatRepository: SeatRepository,
    private val fcmRepository: FCMRepository
) : BaseViewModel() {

    private var _paymentResponse = MutableLiveData<String?>()
    val paymentResponse: LiveData<String?>
        get() = _paymentResponse

    fun updateSeatAndAddBill(model: BookTicketModel) {
        viewModelScope.launch {
            seatRepository.updateSeatsAndBillAfterSuccessfulPayment(model) {
                try {
                    viewModelScope.launch(Dispatchers.IO) {
                        fcmRepository.sendFcm(
                            FcmMessageRequest(
                                "BOOK_TICKETS",
                                "Đặt vé",
                                "Vừa có người dùng đặt vé, hãy vào xác nhận ngay!"
                            )
                        )
                    }
                } catch (ex: Exception) {
                    print(ex.message)
                }
                _paymentResponse.value = it
            }
        }
    }
}