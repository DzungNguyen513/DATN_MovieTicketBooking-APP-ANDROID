package com.example.baseappandroid.data.model.view_data_item

import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseViewData

data class MovieModel(
    val id: Int,
    val movies: List<MovieItemModel>?,
    val interact: MovieModelInteract,
    val listType: MovieType
) : BaseViewData {
    override val layoutRes: Int
        get() = if (listType != MovieType.popular) R.layout.layout_movie_list else R.layout.layout_movie_popular_list

    override fun areItemsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is MovieModel) false
        else newItem.id == id
    }

    override fun areContentsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is MovieModel) false
        else newItem.movies == movies && newItem.listType != listType
    }

}

interface MovieModelInteract {
    fun clickOnMovieListAction()
}