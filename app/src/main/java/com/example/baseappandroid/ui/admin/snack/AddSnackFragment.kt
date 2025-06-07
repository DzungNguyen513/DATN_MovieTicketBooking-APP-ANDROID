package com.example.baseappandroid.ui.admin.snack

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.baseappandroid.R
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.data.model.SnackItemModel
import com.example.baseappandroid.databinding.FragmentAddSnackBinding
import com.example.baseappandroid.utils.AdminToAddEditSnackScreenType
import com.example.baseappandroid.utils.GlobalFunction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddSnackFragment : BaseFragment<FragmentAddSnackBinding>() {
    private val viewModel by viewModels<SnackManagementViewModel>()
    private val snackArgs by navArgs<AddSnackFragmentArgs>()
    override fun getContentLayout(): Int {
        return R.layout.fragment_add_snack
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (snackArgs.editType == AdminToAddEditSnackScreenType.EDIT) {
            viewModel.fetchDetailSnack(snackArgs.snackId.toString())
        }
        with(binding) {
            toolbarAddEditMovie.imageBack.setOnClickListener {
                findNavController().navigateUp()
            }
            toolbarAddEditMovie.textToolbarTitle.text =
                if (snackArgs.editType == AdminToAddEditSnackScreenType.ADD) {
                    "Thêm đồ ăn - uống"
                } else {
                    "Sửa thông tin phim"
                }
            cardviewAddImage.setOnClickListener {
                launchPhotoPicker()
            }
            btnSave.setOnClickListener {
                val name = inputName.text.toString()
                val price = inputPrice.text.toString()
                val description = inputDescription.text.toString()
                if (viewModel.selectedSnackImage == null || name.isEmpty() || price.isEmpty() || description.isEmpty()) {
                    GlobalFunction.showToast(requireContext(), "Bạn phải nhập đầy đủ!")
                } else {
                    try {
                        if (snackArgs.editType == AdminToAddEditSnackScreenType.EDIT) {
                            val model = viewModel.detailSnackResponse.value?.copy(
                                name = name,
                                description = description,
                                price = price.toDouble()
                            )
                            if (model != null) viewModel.editSnack(model)
                        } else {
                            viewModel.addSnack(
                                SnackItemModel(
                                    id = null,
                                    image = null,
                                    name = name,
                                    price = price.toDouble(),
                                    description = description
                                )
                            )
                        }
                    } catch (_: Exception) {
                    }
                }
            }
        }
        observeData()
    }

    private fun observeData() = with(viewModel) {
        addSnackResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                GlobalFunction.showToast(requireContext(), it.toString())
            }
        }
        editSnackResponse.observe(viewLifecycleOwner) {
            if (it == "") {
                findNavController().navigateUp()
            }
        }
        detailSnackResponse.observe(viewLifecycleOwner) {
            binding.imageSnack.visibility = View.VISIBLE
            binding.constraintAddImage.visibility = View.GONE
            Glide.with(requireContext()).load(it.image.toString()).transform(
                CenterCrop(), RoundedCorners(20)
            ).into(binding.imageSnack)
            binding.inputName.setText(it.name.toString())
            binding.inputPrice.setText(it.price.toString())
            binding.inputDescription.setText(it.description.toString())
        }
    }

    private val pickImageGetContent = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.selectedSnackImage = uri
            binding.imageSnack.visibility = View.VISIBLE
            Glide.with(requireContext()).load(uri).transform(
                CenterCrop(), RoundedCorners(20)
            ).into(binding.imageSnack)
            binding.constraintAddImage.visibility = View.GONE
        } else {
            GlobalFunction.showToast(requireContext(), "Chưa chọn ảnh nào!")
        }
    }

    private fun launchPhotoPicker() {
        pickImageGetContent.launch("image/*")
    }
}