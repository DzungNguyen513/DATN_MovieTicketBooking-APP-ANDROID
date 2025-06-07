package com.example.baseappandroid.ui.admin.movie

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseAdapter
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.data.model.view_data_item.MovieImageItemModel
import com.example.baseappandroid.data.model.view_data_item.MovieItemModel
import com.example.baseappandroid.databinding.FragmentAddNewAndEditMovieBinding
import com.example.baseappandroid.utils.AdminToAddEditMovieScreenType
import com.example.baseappandroid.utils.Extensions.convertListToString
import com.example.baseappandroid.utils.GlobalFunction
//import com.google.firebase.functions.ktx.functions
//import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNewAndEditMovieFragment : BaseFragment<FragmentAddNewAndEditMovieBinding>() {
    //    val functions = Firebase.functions
    private val viewModel by viewModels<MovieManagementViewModel>()
    private val movieArgs by navArgs<AddNewAndEditMovieFragmentArgs>()
    private val baseAdapter = BaseAdapter()
    private val listOfMovieType =
        arrayListOf("Phim phổ biến", "Phim sắp ra mắt", "Phim mới")
    private lateinit var spinnerAdapter: ArrayAdapter<String>

    override fun getContentLayout(): Int {
        return R.layout.fragment_add_new_and_edit_movie
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            if (movieArgs.fromScreen == AdminToAddEditMovieScreenType.EDIT.value && movieArgs.movieId.toString()
                    .isNotEmpty()
            ) {
                viewModel.getDetailMovie(movieArgs.movieId.toString())
                listOfImages.visibility = View.VISIBLE
                textEditImages.visibility = View.VISIBLE
                cardviewAddImage.visibility = View.INVISIBLE
            }
            toolbarAddEditMovie.imageBack.setOnClickListener {
                findNavController().navigateUp()
            }
            toolbarAddEditMovie.textToolbarTitle.text =
                if (movieArgs.fromScreen == AdminToAddEditMovieScreenType.ADD.value) {
                    "Thêm phim"
                } else {
                    "Sửa thông tin phim"
                }
            spinnerAdapter =
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    listOfMovieType
                ).also {
                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            inputMovieType.adapter = spinnerAdapter
            listOfImages.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = baseAdapter
            }
            inputMovieType.onItemSelectedListener = object :
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
            cardviewAddImage.setOnClickListener {
                // Nếu Photo Picker khả dụng (Android 13+), dùng PickVisualMedia
                isPhotoPickerAvailable()
            }
            textEditImages.setOnClickListener {
                isPhotoPickerAvailable()
            }
            inputReleaseDate.focusable = View.NOT_FOCUSABLE
            inputReleaseDate.setOnClickListener {
                GlobalFunction.showDatePickerDialog(requireContext()) {
                    inputReleaseDate.setText(it)
                }
            }

            btnSave.setOnClickListener {
                if (inputMovieName.text.toString()
                        .isEmpty() || inputMovieDescription.text.toString().isEmpty() ||
                    inputAgeLimit.text.toString().isEmpty() || inputDuration.text.toString()
                        .isEmpty() || inputReleaseDate.text.toString().isEmpty()
                ) {
                    GlobalFunction.showToast(requireContext(), "Nhập đủ thông tin")
                } else {
                    try {
                        progressBar.visibility = View.VISIBLE
                        val movie = MovieItemModel(
                            id = null,
                            title = inputMovieName.text.toString(),
                            images = null,
                            posterUrl = null,
                            genre = inputGenre.text.toString().split(",").map { e -> e.trim() },
                            ageLimit = inputAgeLimit.text.toString().toInt(),
                            description = inputMovieDescription.text.toString(),
                            rating = inputRating.text.toString().toDouble(),
                            duration = inputDuration.text.toString().toLong(),
                            releaseDate = inputReleaseDate.text.toString(),
                            interact = null,
                            movieType = null,
                            type = inputMovieType.selectedItemPosition + 1
                        )
                        if (movieArgs.fromScreen == AdminToAddEditMovieScreenType.EDIT.value) {
                            viewModel.updateMovie(movie)
                        } else {
                            viewModel.addNewMovie(movie)
                        }
                    } catch (_: Exception) {
                    }
                }
            }
        }
        observeData()
    }

    private fun observeData() = with(viewModel) {
        addMovieResponse.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = View.GONE
            if (it != null) {
                GlobalFunction.showToast(requireContext(), it)
            } else {
                try {
                    sendFCM()
                } catch (ex: Exception) {
                    Log.d("Error Check : ", ex.message.toString())
                }
                findNavController().navigateUp()
            }
        }

        updateMovieResponse.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = View.GONE
            if (it != null) {
                GlobalFunction.showToast(requireContext(), it)
            } else {
                findNavController().navigateUp()
            }
        }

        movieDetailResponse.observe(viewLifecycleOwner) {
            with(binding) {
                inputMovieName.setText(it.title.toString())
                inputDuration.setText(it.duration.toString())
                inputMovieDescription.setText(it.description.toString())
                inputRating.setText(it.rating.toString())
                inputReleaseDate.setText(it.releaseDate.toString())
                inputGenre.setText(it.genre.toString().convertListToString())
                inputAgeLimit.setText(it.ageLimit.toString())
                baseAdapter.submitList(
                    it.images.orEmpty()
                        .map { e -> MovieImageItemModel(uri = null, imagesString = e) })
            }
        }
    }

    // 1. Khai báo launcher cho Photo Picker (Android 13+)
//    private val pickImagePhotoPicker = registerForActivityResult(
//            viewModel.listOfImages.clear()
//        ActivityResultContracts.PickMultipleVisualMedia()
//    ) { uri ->
//        if (uri != null && uri.isNotEmpty()) {
//            viewModel.listOfImages.addAll(uri.map { e -> MovieImageItemModel(e) })
//        } else {
//            GlobalFunction.showToast(requireContext(), "Chưa chọn ảnh nào!")
//        }
//    }

    // 2. Khai báo launcher fallback với GetContent (tương thích API 19+)
    private val pickImageGetContent = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uri ->
        if (uri.isNotEmpty()) {
            viewModel.listOfImages.clear()
            viewModel.listOfImages.addAll(uri.map { e -> MovieImageItemModel(uri = e) })
            setListVisibility(true)
        } else {
            GlobalFunction.showToast(requireContext(), "Chưa chọn ảnh nào!")
        }
    }

    // Kiểm tra Photo Picker API có sẵn không
    private fun isPhotoPickerAvailable() {
//        val check = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
//        if (check) {
//            pickImagePhotoPicker.launch(
//                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
//            )
//        } else {
        // Fallback cho các phiên bản cũ
        pickImageGetContent.launch("image/*")
        //}
    }

    private fun setListVisibility(value: Boolean) = with(binding) {
        if (value) {
            listOfImages.visibility = View.VISIBLE
            textEditImages.visibility = View.VISIBLE
            cardviewAddImage.visibility = View.INVISIBLE
            baseAdapter.submitList(viewModel.listOfImages.toList())
        } else {
            listOfImages.visibility = View.INVISIBLE
            textEditImages.visibility = View.INVISIBLE
            cardviewAddImage.visibility = View.VISIBLE
        }
    }
}
