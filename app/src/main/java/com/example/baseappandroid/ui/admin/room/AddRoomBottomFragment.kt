package com.example.baseappandroid.ui.admin.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.baseappandroid.data.model.RoomItemModel
import com.example.baseappandroid.databinding.FragmentAddRoomBottomBinding
import com.example.baseappandroid.utils.GlobalFunction
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddRoomBottomFragment : BottomSheetDialogFragment() {
    private val viewModel by activityViewModels<RoomManagementViewModel>()
    private var _binding: FragmentAddRoomBottomBinding? = null
    val binding: FragmentAddRoomBottomBinding
        get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddRoomBottomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            imageBack.setOnClickListener {
                findNavController().navigateUp()
            }
            buttonAddRoom.setOnClickListener {
                val name = inputRoomName.text.toString()
                val seats = inputRoomSeats.text.toString()
                if (name.isEmpty() || seats.isEmpty()) {
                    GlobalFunction.showToast(requireContext(), "Bạn cần nhập đủ các trường!")
                } else {
                    try {
                        viewModel.addRoom(RoomItemModel(roomId = null, name = name, seats = seats.toLong()))
                    } catch (_: Exception) {
                    }
                }
            }
        }
        observeData()
    }

    private fun observeData() = with(viewModel) {
        addRoomResponse.observe(viewLifecycleOwner) {
            if (it == true) {
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}