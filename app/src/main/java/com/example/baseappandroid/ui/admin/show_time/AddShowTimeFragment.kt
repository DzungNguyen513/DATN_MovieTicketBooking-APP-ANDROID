package com.example.baseappandroid.ui.admin.show_time

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.baseappandroid.R
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.databinding.FragmentAddShowTimeBinding
import com.example.baseappandroid.utils.GlobalFunction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class AddShowTimeFragment : BaseFragment<FragmentAddShowTimeBinding>() {

    private val viewModel by viewModels<ShowTimeManagementViewModel>()
    private val listOfMoviesNameItem = arrayListOf("Chọn phim")
    private val listOfRoomsNameItem = arrayListOf("Chọn phòng")
    private lateinit var spinnerMovieAdapter: ArrayAdapter<String>
    private lateinit var spinnerRoomAdapter: ArrayAdapter<String>
    override fun getContentLayout(): Int {
        return R.layout.fragment_add_show_time
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.fetchMovies()
        viewModel.fetchRooms()
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            toolbarAddShowTime.textToolbarTitle.text = "Thêm lịch chiếu"
            toolbarAddShowTime.imageBack.setOnClickListener {
                findNavController().navigateUp()
            }
            spinnerRoomAdapter =
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    listOfRoomsNameItem
                ).also { it1 ->
                    it1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            inputChooseRoom.adapter = spinnerRoomAdapter
            spinnerMovieAdapter =
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    listOfMoviesNameItem
                ).also { it1 ->
                    it1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            inputMovie.adapter = spinnerMovieAdapter
            inputMovie.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    (parent?.getChildAt(0) as TextView).setTextColor(Color.BLACK)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }

            inputChooseRoom.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    (parent?.getChildAt(0) as TextView).setTextColor(Color.BLACK)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }

            inputReleaseDate.focusable = View.NOT_FOCUSABLE
            inputReleaseDate.setOnClickListener {
                GlobalFunction.showDatePickerDialog(requireContext()) {
                    inputReleaseDate.setText(it)
                }
            }

            inputTime.focusable = View.NOT_FOCUSABLE
            inputTime.setOnClickListener {
                GlobalFunction.showTimePickerDialog(requireContext()) {
                    inputTime.setText(it)
                }
            }

            buttonSaveShowTime.setOnClickListener {
                val date = GlobalFunction.formatString(
                    "dd/MM/yyyy",
                    "dd-MM-yyyy",
                    inputReleaseDate.text.toString()
                )
                val time = inputTime.text.toString()
                val room = inputChooseRoom.selectedItemPosition
                val movie = inputMovie.selectedItemPosition

                if (date.isEmpty() || time.isEmpty()) {
                    GlobalFunction.showToast(requireContext(), "Chọn đầy đủ các thông tin!")
                } else {
                    viewModel.addShowTime(
                        viewModel.listOfMovies[movie].id.toString(),
                        time,
                        date,
                        viewModel.listOfRooms[room].roomId.toString()
                    )
                }
            }
        }
        observeData()
    }

    private fun observeData() = with(viewModel) {
        listOfRoomsName.observe(viewLifecycleOwner) {
            listOfRoomsNameItem.clear()
            listOfRoomsNameItem.addAll(it)
            spinnerRoomAdapter.notifyDataSetChanged()

        }

        listOfMoviesName.observe(viewLifecycleOwner) {
            listOfMoviesNameItem.clear()
            listOfMoviesNameItem.addAll(it)
            spinnerMovieAdapter.notifyDataSetChanged()
        }

        addShowTimeAction.observe(viewLifecycleOwner) {
            if (it != null) {
                GlobalFunction.showToast(requireContext(), it.toString())
            }
        }
    }
}