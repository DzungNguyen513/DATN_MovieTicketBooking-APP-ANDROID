package com.example.baseappandroid.ui.admin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.GridLayoutManager
import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseAdapter
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.data.model.view_data_item.AdminFeature
import com.example.baseappandroid.databinding.FragmentAdminPanelBinding
import com.example.baseappandroid.utils.GlobalData
import com.google.firebase.messaging.FirebaseMessaging

class AdminPanelFragment : BaseFragment<FragmentAdminPanelBinding>() {
    private val viewModel by viewModels<AdminPanelViewModel>()
    private val featuresAdapter = BaseAdapter()
    override fun getContentLayout(): Int {
        return R.layout.fragment_admin_panel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            listOfAdminFeatures.apply {
                layoutManager = GridLayoutManager(requireContext(), 2)
                adapter = featuresAdapter
            }
            imageLogout.setOnClickListener {
                findNavController().navigate(AdminPanelFragmentDirections.actionAdminPanelFragmentToLoginFragment())
            }
        }
        observeData()
        checkNotificationPermission()
        registerToFirebaseMessagingTopic()
    }

    private fun observeData() = with(viewModel) {
        listOfFeatures.observe(viewLifecycleOwner) {
            featuresAdapter.submitList(it.toList())
        }
        actionFeature.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it) {
                    AdminFeature.MOVIE_MANAGEMENT -> {
                        findNavController().navigate(AdminPanelFragmentDirections.actionAdminPanelFragmentToMovieManagementFragment())
                    }

                    AdminFeature.ROOM_MANAGEMENT -> {
                        findNavController().navigate(AdminPanelFragmentDirections.actionAdminPanelFragmentToRoomManagementFragment())
                    }

                    AdminFeature.SHOW_TIME_MANAGEMENT -> {
                        findNavController().navigate(AdminPanelFragmentDirections.actionAdminPanelFragmentToShowTimeManagementFragment())
                    }

                    AdminFeature.TICKET_MANAGEMENT -> {
                        findNavController().navigate(AdminPanelFragmentDirections.actionAdminPanelFragmentToTicketManagementFragment())
                    }

                    AdminFeature.INCOME_MANAGEMENT -> {
                        findNavController().navigate(AdminPanelFragmentDirections.actionAdminPanelFragmentToStatisticFragment())
                    }

                    AdminFeature.SNACK_MANAGEMENT -> {
                        findNavController().navigate(AdminPanelFragmentDirections.actionAdminPanelFragmentToSnackManagementFragment())
                    }
                }
                clearActionFeature()
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }

    private fun registerToFirebaseMessagingTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("BOOK_TICKETS")
            .addOnCompleteListener { _ -> }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1001 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Quyền đã được cấp, bạn có thể gửi thông báo
                } else {
                    // Quyền bị từ chối, bạn có thể thông báo cho người dùng
                    Toast.makeText(
                        requireContext(),
                        "Để nhận thông báo mới nhất, bạn nên vào cài đặt cấp quyền thông báo nhé!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}