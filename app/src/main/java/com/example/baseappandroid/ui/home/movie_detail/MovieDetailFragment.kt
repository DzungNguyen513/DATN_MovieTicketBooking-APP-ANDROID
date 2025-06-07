package com.example.baseappandroid.ui.home.movie_detail

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.RoundedCorner
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseAdapter
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.data.model.view_data_item.MovieImageItem
import com.example.baseappandroid.data.model.view_data_item.MovieType
import com.example.baseappandroid.databinding.FragmentMovieDetailBinding
import com.example.baseappandroid.utils.Extensions.convertListToString
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class MovieDetailFragment : BaseFragment<FragmentMovieDetailBinding>() {
    private val viewModel by viewModels<MovieDetailViewModel>()
    private val movieAdapter = BaseAdapter()
    override fun getContentLayout(): Int {
        return R.layout.fragment_movie_detail
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args by navArgs<MovieDetailFragmentArgs>()
        viewModel.getDetailMovie(args.movieId)
        with(binding) {
            listOfImages.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = movieAdapter
            }
            imageBack.setOnClickListener {
                findNavController().navigateUp()
            }
            buttonBuyTicket.setOnClickListener {
                findNavController().navigate(
                    MovieDetailFragmentDirections.actionMovieDetailFragmentToChooseDateAndTimeFragment(
                        viewModel.movieDetailId
                    )
                )
            }
        }
        observeData()
    }

    @SuppressLint("SetTextI18n")
    private fun observeData() = with(viewModel) {
        movieDetail.observe(viewLifecycleOwner) { data ->
            with(binding) {
                if (data.type == MovieType.comingSoon.num) {
                    buttonBuyTicket.visibility = View.GONE
                    guidelineMovieDetail.visibility = View.GONE
                } else {
                    buttonBuyTicket.visibility = View.VISIBLE
                    guidelineMovieDetail.visibility = View.VISIBLE
                }
                Glide.with(requireContext()).load(data.posterUrl.toString()).transform(
                    CenterCrop(),
                    RoundedCorners(20)
                )
                    .into(imageDetailMovie)
                textMovieDetailName.text = data.title
                textMovieDetailAgeLimit.text = "P${data.ageLimit.toString()}"
                textMovieDetailGenre.text = data.genre.toString().convertListToString()
                textMovieDetailDescription.text = data.description
                textMovieDetailLength.text = "${data.duration} phút"
                textMovieDetailReleaseDate.text = data.releaseDate.toString()
                val list = arrayListOf<MovieImageItem>()
                list.addAll(data.images?.mapIndexed { index, s ->
                    MovieImageItem(
                        id = index,
                        image = s
                    )
                }?.toList().orEmpty())
                movieAdapter.submitList(list.toList())
            }
        }
    }
}