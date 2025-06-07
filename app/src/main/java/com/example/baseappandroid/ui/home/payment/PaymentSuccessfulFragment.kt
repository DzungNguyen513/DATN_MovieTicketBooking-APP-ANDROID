package com.example.baseappandroid.ui.home.payment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.baseappandroid.R
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.data.model.TicketStatusType
import com.example.baseappandroid.databinding.FragmentPaymentSuccessfulBinding
import com.example.baseappandroid.utils.BillDetailScreenType
import com.example.baseappandroid.utils.Extensions.convertListToString
import com.example.baseappandroid.utils.GlobalFunction
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class PaymentSuccessfulFragment : BaseFragment<FragmentPaymentSuccessfulBinding>() {
    private val navArgsData by navArgs<PaymentSuccessfulFragmentArgs>()
    private val viewModel by viewModels<PaymentSuccessfulViewModel>()
    override fun getContentLayout(): Int {
        return R.layout.fragment_payment_successful
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchDetailBill(navArgsData.billId)
        with(binding) {
            imageBack.setOnClickListener {
                if (navArgsData.fromScreen == BillDetailScreenType.AFTER_PAYMENT.value) {
                    findNavController().navigate(PaymentSuccessfulFragmentDirections.actionPaymentSuccessfulFragmentToMainHomeFragment())
                } else {
                    findNavController().navigateUp()
                }
            }
        }
        observeData()
    }

    @SuppressLint("SetTextI18n")
    private fun observeData() = with(viewModel) {
        detailBillResponse.observe(viewLifecycleOwner) {
            with(binding) {
                when (it.status) {
                    TicketStatusType.CANCEL_TICKET.value -> {
                        textTicketStatus.text = "Trạng thái vé: Đã huỷ"
                    }

                    TicketStatusType.CONFIRM.value -> {
                        textTicketStatus.text = "Trạng thái vé: Đã xác nhận"
                    }

                    else -> {
                        textTicketStatus.text = "Trạng thái vé: Đang chờ xác nhận"
                    }
                }
                viewModel.generateBarcodeImage(
                    widthPixels = resources.displayMetrics.widthPixels,
                    it.id.toString()
                )
                textDetailTicketMovieName.text = it.movieName.toString()
                textDetailTicketId.text = it.id.toString()
                textDetailTicketTime.text = "${it.bookedTime}, ${it.bookedDate}"
                textDetailTicketRoom.text = "Phòng chiếu: ${it.roomName}"
                textDetailTicketCreatedTime.text =
                    "Thời gian giao dịch: ${
                        GlobalFunction.convertSystemMillisecondsIntoFormat(
                            it.createdAt.toString().toLong()
                        )
                    }"
                textDetailTicketNumber.text = "Số vé: ${it.seats?.size}"
                textDetailTicketSeats.text = "Số ghế: ${it.seats.toString().convertListToString()}"
                textTicketUserName.text = it.userName.toString()
                textTicketUserPhone.text = it.userPhone.toString()
            }
        }

        qrCodeImage.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.imageBarcodeTicket.setImageBitmap(it)
                clearQRCodeImageData()
            }
        }
    }

}