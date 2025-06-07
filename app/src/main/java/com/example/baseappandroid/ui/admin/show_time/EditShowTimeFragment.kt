package com.example.baseappandroid.ui.admin.show_time

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.baseappandroid.base.recyclerview.BaseAdapter
import com.example.baseappandroid.data.model.view_data_item.EditShowTimeItemModel
import com.example.baseappandroid.databinding.FragmentEditShowTimeBinding
import com.example.baseappandroid.utils.Extensions.convertListToString
import com.example.baseappandroid.utils.GlobalFunction
import com.example.travelappadmin.common.EventObserver
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class EditShowTimeFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentEditShowTimeBinding
    private val listAdapter = BaseAdapter()
    private val viewModel by activityViewModels<ShowTimeManagementViewModel>()
    private val editTimesNavArgs by navArgs<EditShowTimeFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditShowTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val times =
            editTimesNavArgs.movieTime.convertListToString().split(",")
                .map { e -> EditShowTimeItemModel(time = e, interact = viewModel) }
        viewModel.listOfTimes.clear()
        viewModel.listOfTimes.addAll(times)
        viewModel.editMovieId = editTimesNavArgs.movieId
        with(binding) {
            imageBack.setOnClickListener {
                findNavController().navigateUp()
            }
            listOfShowTimes.apply {
                layoutManager = setUpFlexBox()
                adapter = listAdapter
            }
            listAdapter.submitList(times.toList())
        }
        observeData()
    }

    private fun observeData() = with(viewModel) {
        editShowTimeResponse.observe(viewLifecycleOwner, EventObserver { data ->
            if (data != null && data.isEmpty()) {
                findNavController().navigateUp()
            } else {
                GlobalFunction.showToast(requireContext(), data.toString())
            }
        })
    }

    private fun setUpFlexBox(): FlexboxLayoutManager {
        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.flexDirection = FlexDirection.ROW
        return layoutManager
    }
}