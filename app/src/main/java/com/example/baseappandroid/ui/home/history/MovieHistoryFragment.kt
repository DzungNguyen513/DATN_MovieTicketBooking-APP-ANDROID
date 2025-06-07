package com.example.baseappandroid.ui.home.history

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseAdapter
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.databinding.FragmentMovieHistoryBinding
import com.example.baseappandroid.utils.BillDetailScreenType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieHistoryFragment : BaseFragment<FragmentMovieHistoryBinding>() {
    private val movieAdapter = BaseAdapter()
    private val viewModel by viewModels<MovieHistoryViewModel>()
    override fun getContentLayout(): Int {
        return R.layout.fragment_movie_history
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchData()
        with(binding) {
            swipeRefreshHistoryMovie.setOnRefreshListener {
                onRefreshLoad()
            }
            listOfHistoryMovies.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = movieAdapter
            }
        }
        observeData()
    }

    private fun observeData() = with(viewModel) {
        listOfMoviesHistory.observe(viewLifecycleOwner) {
            movieAdapter.submitList(it.toList())
        }
        onBillDetailClick.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(
                    MovieHistoryFragmentDirections.actionMovieHistoryFragmentToPaymentSuccessfulFragment(
                        billId = it,
                        fromScreen = BillDetailScreenType.HISTORY.value
                    )
                )
                clearBillId()
            }
        }
    }

    private fun onRefreshLoad() {
        viewModel.fetchData()
        Handler(Looper.getMainLooper()).postDelayed({
            binding.swipeRefreshHistoryMovie.isRefreshing = false
        }, 1500)
    }
}