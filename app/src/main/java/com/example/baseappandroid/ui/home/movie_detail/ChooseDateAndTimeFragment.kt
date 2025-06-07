package com.example.baseappandroid.ui.home.movie_detail

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseAdapter
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.databinding.FragmentChooseDateAndTimeBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseDateAndTimeFragment : BaseFragment<FragmentChooseDateAndTimeBinding>() {
    private val navArgsData by navArgs<ChooseDateAndTimeFragmentArgs>()
    private val viewModel by viewModels<MovieDetailViewModel>()
    private val datesAdapter = BaseAdapter()
    private val timeAdapter = BaseAdapter()

    override fun getContentLayout(): Int {
        return R.layout.fragment_choose_date_and_time
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            imageBack.setOnClickListener {
                findNavController().navigateUp()
            }
            listOfDates.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = datesAdapter
            }
            listOfTimes.apply {
                layoutManager = setUpFlexBox()
                adapter = timeAdapter
            }
        }
        observeData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeData() = with(viewModel) {
        listOfDates.observe(viewLifecycleOwner) {
            datesAdapter.submitList(it.toList())
        }
        chosenDate.observe(viewLifecycleOwner) {
            viewModel.getMovieTimesForDetailMovie(navArgsData.movieId, it)
        }
        listOfTimes.observe(viewLifecycleOwner) {
            timeAdapter.submitList(it.toList())
        }
        chosenMovie.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(
                    ChooseDateAndTimeFragmentDirections.actionChooseDateAndTimeFragmentToBookTicketFragment(
                        it
                    )
                )
                clearChosenMovie()
            }
        }
    }

    private fun setUpFlexBox(): FlexboxLayoutManager {
        val layoutManager = FlexboxLayoutManager(binding.root.context)
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.flexDirection = FlexDirection.ROW
        return layoutManager
    }
}