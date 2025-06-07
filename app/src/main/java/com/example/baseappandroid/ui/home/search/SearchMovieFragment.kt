package com.example.baseappandroid.ui.home.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseAdapter
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.databinding.FragmentSearchMovieBinding
import com.example.baseappandroid.ui.home.main_home.MainHomeFragmentDirections
import com.example.travelappadmin.common.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchMovieFragment : BaseFragment<FragmentSearchMovieBinding>() {
    private val viewModel by viewModels<SearchMovieViewModel>()
    private val listAdapter = BaseAdapter()
    override fun getContentLayout(): Int {
        return R.layout.fragment_search_movie
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            imageBack.setOnClickListener {
                findNavController().navigateUp()
            }
            listOfSearchMovies.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = listAdapter
            }
            searchMovieBar.inputSearch.apply {
                isFocusable = true
            }
            searchMovieBar.inputSearch.addTextChangedListener {
                if (!it.isNullOrEmpty()) {
                    viewModel.searchMovies(it.toString())
                }
            }
        }
        observeData()
    }

    private fun observeData() = with(viewModel) {
        searchMoviesResponse.observe(viewLifecycleOwner) {
            val list = ArrayList(it)
            listAdapter.submitList(list.toList())
        }
        clickOnMovieAction.observe(viewLifecycleOwner, EventObserver {
            if (it != null && it.toString().isNotEmpty()) {
                val directions =
                    SearchMovieFragmentDirections.actionSearchMovieFragmentToMovieDetailFragment(movieId = it)
                findNavController().navigate(directions)
            }
        })
    }

}