package com.example.baseappandroid.ui.home.profile

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.baseappandroid.R
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.databinding.FragmentProfileInformationBinding
import com.example.baseappandroid.utils.GlobalData
import com.example.baseappandroid.utils.GlobalFunction
import com.example.travelappadmin.common.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileInformationFragment : BaseFragment<FragmentProfileInformationBinding>() {
    private val viewModel by viewModels<ProfileViewModel>()
    override fun getContentLayout(): Int {
        return R.layout.fragment_profile_information
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            GlobalData.user?.let {
                inputEmail.setText(it.email)
                inputMobilePhone.setText(it.mobilePhone)
                inputName.setText(it.fullName)
                Glide.with(requireContext()).load(it.image).transform(
                    CenterCrop(), RoundedCorners(20)
                ).into(binding.imageProfile)
            }
            imageBack.setOnClickListener {
                findNavController().navigateUp()
            }
            imageProfile.setOnClickListener {
                launchPhotoPicker()
            }
            buttonSaveInfo.setOnClickListener {
                if (inputMobilePhone.text.toString()
                        .isNotEmpty() || viewModel.selectedImage != null
                ) {
                    GlobalData.user?.id?.let { it1 ->
                        viewModel.changeInformation(
                            inputMobilePhone.text.toString(),
                            it1
                        )
                    }
                }
            }
        }
        observeData()
    }

    private val pickImageGetContent = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.selectedImage = uri
            Glide.with(requireContext()).load(uri).transform(
                CenterCrop(), RoundedCorners(20)
            ).into(binding.imageProfile)
        } else {
            GlobalFunction.showToast(requireContext(), "Chưa chọn ảnh nào!")
        }
    }

    private fun launchPhotoPicker() {
        pickImageGetContent.launch("image/*")
    }

    private fun observeData() = with(viewModel) {
        getInfoResponse.observe(viewLifecycleOwner, EventObserver {
            with(binding) {
                inputEmail.setText(it.email)
                inputMobilePhone.setText(it.mobilePhone)
                inputName.setText(it.fullName)
                Glide.with(requireContext()).load(it.image).transform(
                    CenterCrop(), RoundedCorners(20)
                ).into(binding.imageProfile)
            }
            GlobalFunction.showToast(requireContext(), "Thành công!")
        })
    }
}