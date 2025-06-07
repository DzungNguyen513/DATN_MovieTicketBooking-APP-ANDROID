package com.example.baseappandroid.ui.login

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.baseappandroid.R
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.data.model.User
import com.example.baseappandroid.data.model.UserRole
import com.example.baseappandroid.databinding.FragmentLoginBinding
import com.example.baseappandroid.utils.GlobalFunction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    private val viewModel: LoginViewModel by viewModels()

    override fun getContentLayout(): Int {
        return R.layout.fragment_login
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.checkAutoLogin()
        with(binding) {
            imageShowPassword.setOnClickListener {
                viewModel.isHidePassword = !viewModel.isHidePassword
                if (viewModel.isHidePassword) {
                    imageShowPassword.setImageResource(R.drawable.ic_show_password)
                    inputPassword.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                } else {
                    imageShowPassword.setImageResource(R.drawable.ic_hide_password)
                    inputPassword.inputType =
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                }
            }
            buttonSignIn.setOnClickListener {
                if (inputEmail.text.toString().isEmpty() || inputPassword.text.toString()
                        .isEmpty()
                ) {
                    Toast.makeText(requireContext(), "Điền đầy đủ thông tin", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    viewModel.login(
                        user = User(
                            email = inputEmail.text.toString(),
                            password = inputPassword.text.toString()
                        )
                    )
                }
            }
            textSignUp.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
            }
        }
        observeData()
    }

    private fun observeData() = with(viewModel) {
        errorResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                GlobalFunction.showToast(requireContext(), it)
            }
        }
        loginResponse.observe(viewLifecycleOwner) {
            if (it != null && it.toString().isNotEmpty()) {
                when (it.role) {
                    UserRole.USER.value -> {
                        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
                    }

                    UserRole.ADMIN.value -> {
                        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToAdminPanelFragment())
                    }
                }
            }
        }
    }

}