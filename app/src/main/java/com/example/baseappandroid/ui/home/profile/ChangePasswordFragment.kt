package com.example.baseappandroid.ui.home.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.baseappandroid.R
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.databinding.FragmentChangePasswordBinding
import com.example.baseappandroid.utils.GlobalData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class ChangePasswordFragment : BaseFragment<FragmentChangePasswordBinding>() {

    private val viewModel by viewModels<ProfileViewModel>()

    override fun getContentLayout(): Int {
        return R.layout.fragment_change_password
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            btnSavePassword.setOnClickListener {
                if (inputPassword.text.toString().isEmpty() || inputNewPassword.text.toString().isEmpty()) {
                    Toast.makeText(requireContext(), "Bạn cần nhập đủ các trường yêu cầu!", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.checkPassword(GlobalData.user?.email.toString(), inputPassword.text.toString())
                }
            }
            imageBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
        observeData()
    }

    private fun observeData() = with(viewModel) {
        checkPasswordResponse.observe(viewLifecycleOwner) {
            if (!it) {
                Toast.makeText(requireContext(), "Sai mật khẩu cũ!", Toast.LENGTH_SHORT).show()
            } else {
                changePassword(GlobalData.user?.email.toString(), binding.inputNewPassword.text.toString())
            }
        }
        changePasswordResponse.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}