package com.example.baseappandroid.ui.admin.movie

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseAdapter
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.databinding.FragmentMovieManagementBinding
import com.example.baseappandroid.utils.AdminToAddEditMovieScreenType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieManagementFragment : BaseFragment<FragmentMovieManagementBinding>() {
    private val viewModel by viewModels<MovieManagementViewModel>()
    private val movieAdapter = BaseAdapter()
    override fun getContentLayout(): Int {
        return R.layout.fragment_movie_management
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            listOfMovies.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = movieAdapter
            }
            imageAddMovie.setOnClickListener {
                findNavController().navigate(
                    MovieManagementFragmentDirections.actionMovieManagementFragmentToAddNewAndEditMovieFragment(
                        fromScreen = AdminToAddEditMovieScreenType.ADD.value,
                        movieId = null
                    )
                )
            }
            imageBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
        observeData()
    }

    private fun observeData() = with(viewModel) {
        movieList.observe(viewLifecycleOwner) {
            movieAdapter.submitList(it.toList())
        }
        deleteMovieAction.observe(viewLifecycleOwner) {
            if (it != null) {
                //show dialog
                showDeleteMovieDialog(it)
                clearDeleteFeature()
            }
        }

        editMovieAction.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                clearEditFeature()
                findNavController().navigate(
                    MovieManagementFragmentDirections.actionMovieManagementFragmentToAddNewAndEditMovieFragment(
                        fromScreen = AdminToAddEditMovieScreenType.EDIT.value,
                        movieId = it.toString()
                    )
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchMoviesData()
    }

    private fun showDeleteMovieDialog(id: String) {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle("Xoá phim!")
        dialog.setMessage("Bạn có chắc chắn muốn xoá phim này không")
        dialog.setPositiveButton(
            "Xoá"
        ) { p0, _ ->
            viewModel.deleteMovie(id)
            viewModel.clearDeleteFeature()
            p0.dismiss()
        }

        dialog.setNegativeButton(
            "Huỷ"
        ) { p0, _ -> p0.dismiss() }

        val alertDialog = dialog.create()
        alertDialog.show()
    }
}