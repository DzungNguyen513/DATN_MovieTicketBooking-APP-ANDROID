package com.example.baseappandroid.ui.home.payment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.baseappandroid.R
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.data.api.AppInfo
import com.example.baseappandroid.data.api.CreateOrder
import com.example.baseappandroid.databinding.FragmentPaymentBinding
import com.example.baseappandroid.utils.BillDetailScreenType
import com.example.baseappandroid.utils.GlobalFunction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.interfaces.MerchantService
import vn.zalopay.sdk.listeners.PayOrderListener

@AndroidEntryPoint
class PaymentFragment : BaseFragment<FragmentPaymentBinding>() {
    private val viewModel by viewModels<PaymentViewModel>()
    override fun getContentLayout(): Int {
        return R.layout.fragment_payment
    }

    private lateinit var zaloPay: MerchantService
    private val args: PaymentFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val movie = args.bookMovie
        ZaloPaySDK.init(AppInfo.APP_ID, Environment.SANDBOX)
        zaloPay = ZaloPaySDK.getInstance()
        with(binding) {
            imageBack.setOnClickListener {
                findNavController().navigateUp()
            }
            cardViewZaloPay.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    val orderApi = CreateOrder()
                    try {
                        val money = movie.totalMoney.toDouble()
                        val data = orderApi.createOrder((money.toLong()).toString())
                        val code = data.getString("return_code")
                        if (code == "1") {
                            val token = data.getString("zp_trans_token")
                            withContext(Dispatchers.Main) {
                                zaloPay.payOrder(
                                    requireActivity(),
                                    token,
                                    "demozpdk://app",
                                    object : PayOrderListener {
                                        override fun onPaymentSucceeded(
                                            s: String,
                                            s1: String,
                                            s2: String
                                        ) {
                                            viewModel.updateSeatAndAddBill(movie)
                                            GlobalFunction.showToast(
                                                requireActivity(),
                                                "Thanh toán thành công"
                                            )
                                        }

                                        override fun onPaymentCanceled(s: String, s1: String) {
                                            GlobalFunction.showToast(
                                                requireActivity(),
                                                "Huỷ thanh toán!"
                                            )
                                        }

                                        override fun onPaymentError(
                                            zaloPayError: ZaloPayError,
                                            s: String,
                                            s1: String
                                        ) {
                                            GlobalFunction.showToast(
                                                requireActivity(),
                                                "Thanh toán thất bại do có lỗi xảy ra, vui lòng thử lại"
                                            )
                                        }
                                    })
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        observeData()
    }

    private fun observeData() = with(viewModel) {
        paymentResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(
                    PaymentFragmentDirections.actionPaymentFragmentToPaymentSuccessfulFragment(
                        billId = it,
                        fromScreen = BillDetailScreenType.AFTER_PAYMENT.value
                    )
                )
            }
        }
    }
}