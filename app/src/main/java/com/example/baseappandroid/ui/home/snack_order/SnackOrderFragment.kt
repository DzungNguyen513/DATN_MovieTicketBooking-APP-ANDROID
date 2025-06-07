package com.example.baseappandroid.ui.home.snack_order

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseAdapter
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.databinding.FragmentSnackOrderBinding
import com.example.baseappandroid.utils.Extensions.formatMoney
import com.example.baseappandroid.utils.GlobalFunction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SnackOrderFragment : BaseFragment<FragmentSnackOrderBinding>() {
    private val viewModel by viewModels<SnackOrderViewModel>()
    private val snackAdapter = BaseAdapter()
    private val snackArgs by navArgs<SnackOrderFragmentArgs>()
    override fun getContentLayout(): Int {
        return R.layout.fragment_snack_order
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val argModel = snackArgs.bookMovie
        try {
            if (viewModel.totalMoneyResponse.value == null) {
                viewModel.setInitialTotalMoney(argModel.totalMoney.toDouble())
            }
        } catch (_: Exception) {
        }
        with(binding) {
            listOfSnacks.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = snackAdapter
            }
            imageBack.setOnClickListener {
                findNavController().navigateUp()
            }
            buttonContinue.setOnClickListener {
                findNavController().navigate(
                    SnackOrderFragmentDirections.actionSnackOrderFragmentToPaymentFragment(
                        bookMovie = argModel.copy(totalMoney = viewModel.totalMoneyResponse.value.toString())
                    )
                )
            }
        }
        observeData()
    }

    private fun observeData() = with(viewModel) {
        totalMoneyResponse.observe(viewLifecycleOwner) {
            binding.textTotal.text = "${it.formatMoney()}"
        }
        listOfSnacks.observe(viewLifecycleOwner) {
            snackAdapter.submitList(it.toList())
        }
    }
}