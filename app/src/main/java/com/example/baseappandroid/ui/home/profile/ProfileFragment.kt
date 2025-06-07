package com.example.baseappandroid.ui.home.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.baseappandroid.R
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.databinding.FragmentProfileBinding
import com.example.baseappandroid.utils.GlobalData
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    override fun getContentLayout(): Int {
        return R.layout.fragment_profile
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            textProfileInfo.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToProfileInformationFragment())
            }
            textProfileChangePassword.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToChangePasswordFragment())
            }
            textProfileLogout.setOnClickListener {
                GlobalData.user = null
                sharedPreferences.edit().remove("remember_user").apply()

                val options = navOptions {
                    // popUpTo về homeFragment và xoá nó luôn (inclusive = true)
                    popUpTo(R.id.homeFragment) { inclusive = true }
                    launchSingleTop = true  // tránh tạo nhiều instance của loginFragment
                }

                requireActivity().findNavController(R.id.fragment_container_all)
                    .navigate(R.id.loginFragment, null, options)
            }
        }
    }

}