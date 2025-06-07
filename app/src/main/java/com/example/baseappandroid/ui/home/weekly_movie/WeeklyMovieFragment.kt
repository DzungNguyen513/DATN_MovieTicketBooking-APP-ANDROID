package com.example.baseappandroid.ui.home.weekly_movie

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseAdapter
import com.example.baseappandroid.base.recyclerview.VerticalScrollingLinearLayoutManager
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.databinding.FragmentWeeklyMovieBinding
import dagger.hilt.android.AndroidEntryPoint

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class WeeklyMovieFragment : BaseFragment<FragmentWeeklyMovieBinding>() {
    private val viewModel by viewModels<WeeklyMovieViewModel>()
    private val listAdapter = BaseAdapter()
    private val listMoviesAdapter = BaseAdapter()
    override fun getContentLayout(): Int {
        return R.layout.fragment_weekly_movie
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            listOfDates.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = listAdapter
            }
            listOfMovies.apply {
                layoutManager = VerticalScrollingLinearLayoutManager(requireContext())
                adapter = listMoviesAdapter
            }
        }
        observeData()
    }

    private fun observeData() = with(viewModel) {
        listOfDates.observe(viewLifecycleOwner) {
            listAdapter.submitList(it.toList())
        }
        listOfMovieTimes.observe(viewLifecycleOwner) {
            listMoviesAdapter.submitList(it.toList())
        }
        chosenDate.observe(viewLifecycleOwner) {
            viewModel.getWeeklyMovies(it)
        }
        chosenMovie.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(
                    WeeklyMovieFragmentDirections.actionWeeklyMovieFragmentToBookTicketFragment(
                        it
                    )
                )
                clearChosenMovie()
            }
        }
    }


}