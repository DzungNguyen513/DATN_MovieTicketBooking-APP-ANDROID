package com.example.baseappandroid.ui.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.baseappandroid.R
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.data.model.User
import com.example.baseappandroid.data.model.UserRole
import com.example.baseappandroid.databinding.FragmentRegisterBinding
import com.example.baseappandroid.utils.GlobalFunction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {
    private val viewModel by viewModels<RegisterViewModel>()
    override fun getContentLayout(): Int {
        return R.layout.fragment_register
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            textSignIn.setOnClickListener {
                findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
            }

            buttonSignUp.setOnClickListener {
                val name = inputFullName.text.toString()
                val email = inputEmail.text.toString()
                val phoneNumber = inputMobilePhone.text.toString()
                val password = inputPassword.text.toString()
                val repeatPassword = inputPassword1.text.toString()

                if (name.isEmpty() || email.isEmpty() || repeatPassword.isEmpty() || phoneNumber.isEmpty() || password.isEmpty()) {
                    Toast.makeText(requireContext(), "Điền đầy đủ thông tin", Toast.LENGTH_SHORT)
                        .show()
                } else if (password != repeatPassword) {
                    Toast.makeText(
                        requireContext(),
                        "Mật khẩu đã nhập và mật khẩu nhập lại khác nhau!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    viewModel.registerUser(
                        User(
                            id = null,
                            fullName = name,
                            mobilePhone = phoneNumber,
                            email = email,
                            password = password,
                            role = UserRole.USER.value
                        )
                    )
                }
            }
            textChooseImage.setOnClickListener {
                launchPhotoPicker()
            }
            imageProfile.setOnClickListener {
                launchPhotoPicker()
            }
            imageBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
        observeData()
    }

    private fun observeData() = with(viewModel) {
        registerUserResponse.observe(viewLifecycleOwner) {
            if (it == null) {
                GlobalFunction.showToast(requireContext(), "Thành công")
                findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
            } else {
                GlobalFunction.showToast(requireContext(), it)
            }
        }
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
}