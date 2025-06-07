package com.example.baseappandroid.ui.admin.ticket

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseappandroid.base.viewmodel.BaseViewModel
import com.example.baseappandroid.data.model.BillItemModel
import com.example.baseappandroid.data.model.Result
import com.example.baseappandroid.data.repository.BillRepository
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class TicketManagementViewModel @Inject constructor(
    private val billRepository: BillRepository
) : BaseViewModel() {

    private var _listOfTickets = MutableLiveData<List<BillItemModel>>()
    val listOfTickets: LiveData<List<BillItemModel>>
        get() = _listOfTickets

    private var _detailOfTickets = MutableLiveData<BillItemModel>()
    val detailOfTickets: LiveData<BillItemModel>
        get() = _detailOfTickets

    private var _updateTicketResponse = MutableLiveData<String>()
    val updateTicketResponse: LiveData<String>
        get() = _updateTicketResponse

    private var _onTicketChosenAction = MutableLiveData<String?>()
    val onTicketChosenAction: LiveData<String?>
        get() = _onTicketChosenAction

    fun clearTicketChosen() {
        _onTicketChosenAction.value = null
    }

    private var _qrCodeImage = MutableLiveData<Bitmap?>()
    val qrCodeImage: LiveData<Bitmap?>
        get() = _qrCodeImage

    fun clearQRCodeImageData() {
        viewModelScope.launch {
            _qrCodeImage.value = null
        }
    }
    init {
        firstSearchTickets()
    }

    fun searchTickets(phoneNumber: String) {
        viewModelScope.launch {
            when (val result = billRepository.fetchListBillsByPhone(phoneNumber)) {
                is Result.Success -> {
                    _listOfTickets.value =
                        result.data.map { e -> e.copy(interact = this@TicketManagementViewModel) }
                }

                is Result.Error -> {
                    Log.d("Check error", result.errorMessage)
                }
            }
        }
    }

    private fun firstSearchTickets() {
        viewModelScope.launch {
            when (val result = billRepository.fetchListBillsByPhone("")) {
                is Result.Success -> {
                    _listOfTickets.value =
                        result.data.map { e -> e.copy(interact = this@TicketManagementViewModel) }
                }

                is Result.Error -> {
                    Log.d("Check error", result.errorMessage)
                }
            }
        }
    }

    fun getDetailTicket(id: String) {
        viewModelScope.launch {
            when (val result = billRepository.fetchDetailBill(id)) {
                is Result.Success -> {
                    _detailOfTickets.value = result.data
                }

                is Result.Error -> {
                    Log.d("Check error", result.errorMessage)
                }
            }
        }
    }

    fun updateDetailTicket(id: String, status: Int) {
        viewModelScope.launch {
            when (val result = billRepository.updateDetailBillStatus(id, status, _detailOfTickets.value)) {
                is Result.Success -> {
                    _updateTicketResponse.value = result.data
                }

                is Result.Error -> {
                    Log.d("Check error", result.errorMessage)
                }
            }
        }
    }

    fun generateBarcodeImage(widthPixels: Int, ticketId: String) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                var width = 400
                if ((widthPixels * 0.35).roundToInt() < 400) {
                    width = (widthPixels * 0.35).roundToInt()
                }
                val barcode = BarcodeEncoder()
                val bitmap = barcode.encodeBitmap(
                    ticketId,
                    BarcodeFormat.QR_CODE,
                    width,
                    width
                )
                withContext(Dispatchers.Main) {
                    _qrCodeImage.value = bitmap
                }
            } catch (_: Exception) {
            }
        }
    }

    override fun onBillClick(id: String) {
        _onTicketChosenAction.value = id
    }
}