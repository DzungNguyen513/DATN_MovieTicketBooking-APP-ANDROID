package com.example.baseappandroid.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.baseappandroid.R
import com.example.baseappandroid.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding!!
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            val navHostFragment =
                childFragmentManager.findFragmentById(R.id.fragment_container_home) as NavHostFragment
            navController = navHostFragment.findNavController()
            homeBottomNav.setupWithNavController(navController)

            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.mainHomeFragment -> {
                        homeBottomNav.visibility = View.VISIBLE
                    }

                    R.id.weeklyMovieFragment -> {
                        homeBottomNav.visibility = View.VISIBLE
                    }

                    R.id.movieHistoryFragment -> {
                        homeBottomNav.visibility = View.VISIBLE
                    }

                    R.id.profileFragment -> {
                        homeBottomNav.visibility = View.VISIBLE
                    }

                    else -> {
                        homeBottomNav.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}