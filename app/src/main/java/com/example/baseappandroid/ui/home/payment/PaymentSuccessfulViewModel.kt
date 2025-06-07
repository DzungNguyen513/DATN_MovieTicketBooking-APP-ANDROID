package com.example.baseappandroid.ui.home.payment

import android.graphics.Bitmap
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
class PaymentSuccessfulViewModel @Inject constructor(
    private val billRepository: BillRepository
) : BaseViewModel() {

    private var _detailBillResponse = MutableLiveData<BillItemModel>()
    val detailBillResponse: LiveData<BillItemModel>
        get() = _detailBillResponse


    private var _qrCodeImage = MutableLiveData<Bitmap?>()
    val qrCodeImage: LiveData<Bitmap?>
        get() = _qrCodeImage

    fun fetchDetailBill(id: String) {
        viewModelScope.launch {
            when (val result = billRepository.fetchDetailBill(id)) {
                is Result.Success -> {
                    _detailBillResponse.value = result.data
                }

                is Result.Error -> {
                    // handle error here
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

    fun clearQRCodeImageData() {
        viewModelScope.launch {
            _qrCodeImage.value = null
        }
    }
}