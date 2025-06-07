package com.example.baseappandroid.ui.admin.show_time

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseAdapter
import com.example.baseappandroid.base.recyclerview.VerticalScrollingLinearLayoutManager
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.databinding.FragmentShowTimeManagementBinding
import com.example.baseappandroid.utils.GlobalFunction
import com.example.travelappadmin.common.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class ShowTimeManagementFragment : BaseFragment<FragmentShowTimeManagementBinding>() {
    private val listMoviesAdapter = BaseAdapter()
    private val listAdapter = BaseAdapter()
    private val viewModel by activityViewModels<ShowTimeManagementViewModel>()
    override fun getContentLayout(): Int {
        return R.layout.fragment_show_time_management
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            imageBack.setOnClickListener {
                findNavController().navigateUp()
            }
            listOfDates.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = listAdapter
            }
            imageAddMovie.setOnClickListener {
                findNavController().navigate(ShowTimeManagementFragmentDirections.actionShowTimeManagementFragmentToAddShowTimeFragment())
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

        deleteShowTimeAction.observe(viewLifecycleOwner) {
            if (it != null) {
                GlobalFunction.showDeleteConfirmationDialog(
                    "Xoá lịch chiếu",
                    "Bạn có chắc chắn muốn xoá lịch chiếu này?",
                    it.toString(),
                    requireContext()
                ) { _ ->
                    deleteShowTime(it.toString())
                }
                clearDeleteShowTimeAction()
            }
        }

        editShowTimeAction.observe(viewLifecycleOwner, EventObserver {
            if (it.first != null && it.second != null) {
                findNavController().navigate(
                    ShowTimeManagementFragmentDirections.actionShowTimeManagementFragmentToEditShowTimeFragment(
                        movieTime = it.first.toString(),
                        movieId = it.second.toString()
                    )
                )
            }
        })
    }

}