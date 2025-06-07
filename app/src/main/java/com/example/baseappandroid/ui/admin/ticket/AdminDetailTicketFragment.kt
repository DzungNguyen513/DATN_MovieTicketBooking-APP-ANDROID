package com.example.baseappandroid.ui.admin.ticket

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.baseappandroid.R
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.data.model.TicketStatusType
import com.example.baseappandroid.databinding.FragmentAdminDetailTicketBinding
import com.example.baseappandroid.utils.Extensions.convertListToString
import com.example.baseappandroid.utils.GlobalFunction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class AdminDetailTicketFragment : BaseFragment<FragmentAdminDetailTicketBinding>() {

    private val viewModel by viewModels<TicketManagementViewModel>()
    private val ticketNavArgs by navArgs<AdminDetailTicketFragmentArgs>()
    override fun getContentLayout(): Int {
        return R.layout.fragment_admin_detail_ticket
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getDetailTicket(ticketNavArgs.id)
        with(binding) {
            imageBack.setOnClickListener {
                findNavController().navigateUp()
            }
            buttonCancelTicket.setOnClickListener {
                viewModel.updateDetailTicket(ticketNavArgs.id, TicketStatusType.CANCEL_TICKET.value)
            }
            buttonConfirmTicket.setOnClickListener {
                viewModel.updateDetailTicket(ticketNavArgs.id, TicketStatusType.CONFIRM.value)
            }
        }
        observeData()
    }


    @SuppressLint("SetTextI18n")
    private fun observeData() = with(viewModel) {
        detailOfTickets.observe(viewLifecycleOwner) {
            with(binding) {
                if (it.status == TicketStatusType.CANCEL_TICKET.value || it.status == TicketStatusType.CONFIRM.value) {
                    buttonCancelTicket.visibility = View.GONE
                    buttonConfirmTicket.visibility = View.GONE
                } else {
                    buttonCancelTicket.visibility = View.VISIBLE
                    buttonConfirmTicket.visibility = View.VISIBLE
                }
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
        updateTicketResponse.observe(viewLifecycleOwner) {
            viewModel.getDetailTicket(ticketNavArgs.id)
            GlobalFunction.showToast(requireContext(), it)
        }
    }

}