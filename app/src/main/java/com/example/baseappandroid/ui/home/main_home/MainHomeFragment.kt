package com.example.baseappandroid.ui.home.main_home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseAdapter
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.databinding.FragmentMainHomeBinding
import com.example.baseappandroid.utils.GlobalData
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainHomeFragment : BaseFragment<FragmentMainHomeBinding>() {
    private lateinit var baseAdapter: BaseAdapter
    private val viewModel by viewModels<MainHomeViewModel>()
    override fun getContentLayout(): Int {
        return R.layout.fragment_main_home
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseAdapter = BaseAdapter()
        with(binding) {
            textHelloHome.text = "Xin chào, ${GlobalData.user?.fullName}"
            listOfHome.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = baseAdapter
                hasFixedSize()
            }
        }
        observeData()
        checkNotificationPermission()
        registerToFirebaseMessagingTopic()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.POST_NOTIFICATIONS) !=
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
        FirebaseMessaging.getInstance().subscribeToTopic("NEW_MOVIES").addOnCompleteListener { _ -> }
    }

    private fun observeData() = with(viewModel) {
        searchAction.observe(viewLifecycleOwner) {
            if (it) {
                val directions =
                    MainHomeFragmentDirections.actionMainHomeFragmentToSearchMovieFragment()
                findNavController().navigate(directions)
                setSearchAction(false)
            }
        }
        listOfItems.observe(viewLifecycleOwner) {
            val list = ArrayList(it.toList())
            baseAdapter.submitList(list.toList())
        }

        clickOnMovieAction.observe(viewLifecycleOwner) {
            if (it != null) {
                val directions =
                    MainHomeFragmentDirections.actionMainHomeFragmentToMovieDetailFragment(movieId = it)
                findNavController().navigate(directions)
                setClickOnMovieAction(null)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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