package com.example.baseappandroid.ui.admin.room

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.baseappandroid.R
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.data.model.RoomItemModel
import com.example.baseappandroid.databinding.FragmentEditRoomBinding
import com.example.baseappandroid.utils.GlobalFunction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditRoomFragment : BaseFragment<FragmentEditRoomBinding>() {

    private val viewModel by viewModels<RoomManagementViewModel>()
    private val roomNavArgs by navArgs<EditRoomFragmentArgs>()
    override fun getContentLayout(): Int {
        return R.layout.fragment_edit_room
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            inputRoomName.setText(roomNavArgs.roomName)
            inputRoomSeats.setText(roomNavArgs.roomSeats.toString())

            buttonDeleteRoom.setOnClickListener {
                viewModel.deleteRoom(roomNavArgs.roomID)
            }
            buttonEditRoom.setOnClickListener {
                val name = inputRoomName.text.toString()
                val seats = inputRoomSeats.text.toString()
                if (name.isEmpty() || seats.isEmpty()) {
                    GlobalFunction.showToast(requireContext(), "Bạn cần nhập đủ các trường!")
                } else {
                    try {
                        viewModel.updateRoom(RoomItemModel(roomId = roomNavArgs.roomID, name = name, seats = seats.toLong()))
                    } catch (_: Exception) {
                    }
                }
            }
            imageBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
        observeData()
    }

    private fun observeData() = with(viewModel) {
        editRoomResponse.observe(viewLifecycleOwner) {
            if (it == true) {
                findNavController().navigateUp()
            }
        }
    }
}