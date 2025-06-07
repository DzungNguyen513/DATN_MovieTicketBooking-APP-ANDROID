package com.example.baseappandroid.ui.admin.statistic

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseAdapter
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.databinding.FragmentStatisticBinding
import com.example.baseappandroid.utils.Extensions.formatMoney
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticFragment : BaseFragment<FragmentStatisticBinding>() {

    private val listAdapter = BaseAdapter()
    private val viewModel by viewModels<StatisticsViewModel>()
    override fun getContentLayout(): Int {
        return R.layout.fragment_statistic
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getStatisticInformation()
        with(binding) {
            toolbarStatistic.imageBack.setOnClickListener {
                findNavController().navigateUp()
            }
            toolbarStatistic.textToolbarTitle.text = "Thống kê"
            listOfMoviesDetailIncome.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = listAdapter
            }
        }
        observeData()
    }

    @SuppressLint("SetTextI18n")
    private fun observeData() = with(viewModel) {
        incomeResponse.observe(viewLifecycleOwner) {
            binding.textTicketIncomeNumber.text = "${it.formatMoney()}"
        }
        movieNumberResponse.observe(viewLifecycleOwner) {
            binding.textTicketNumberInput.text = "$it"
        }
        listOfDetailMovies.observe(viewLifecycleOwner) {
            listAdapter.submitList(it.toList())
        }
    }
}