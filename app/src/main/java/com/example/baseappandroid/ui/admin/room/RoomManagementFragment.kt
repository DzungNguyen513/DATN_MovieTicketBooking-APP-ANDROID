package com.example.baseappandroid.ui.admin.room

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseAdapter
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.databinding.FragmentRoomManagementBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoomManagementFragment : BaseFragment<FragmentRoomManagementBinding>() {
    private val roomAdapter = BaseAdapter()
    private val viewModel by activityViewModels<RoomManagementViewModel>()
    override fun getContentLayout(): Int {
        return R.layout.fragment_room_management
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            listOfRooms.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = roomAdapter
            }
            imageBack.setOnClickListener {
                findNavController().navigateUp()
            }
            imageAddRoom.setOnClickListener {
                findNavController().navigate(RoomManagementFragmentDirections.actionRoomManagementFragmentToAddRoomBottomFragment())
            }
        }
        observeData()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.shouldCallFetchData) {
            viewModel.shouldCallFetchData = false
            viewModel.fetchRooms()
        }
    }

    private fun observeData() = with(viewModel) {
        listOfRooms.observe(viewLifecycleOwner) {
            roomAdapter.submitList(it.sortedBy { e -> e.name }.toList())
        }
        onRoomClickAction.observe(viewLifecycleOwner) {
            if (it != null) {
                clearCLickRoomAction()
                shouldCallFetchData = true
                findNavController().navigate(
                    RoomManagementFragmentDirections.actionRoomManagementFragmentToEditRoomFragment(
                        roomName = it.name.toString(),
                        roomID = it.roomId.toString(),
                        roomSeats = it.seats ?: 0
                    )
                )
            }
        }
    }
}