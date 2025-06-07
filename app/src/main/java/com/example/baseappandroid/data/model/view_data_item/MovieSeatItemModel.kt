package com.example.baseappandroid.data.model.view_data_item

import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseViewData

data class MovieSeatItemModel(
    val id: Int,
    val lineText: String?,
    val movieSeatType: MovieSeatType,
    var isChosen: Boolean = false,
    var isBooked: Boolean = false,
    val interact: MovieSeatInteract
) : BaseViewData {
    override val layoutRes: Int
        get() = when (movieSeatType) {
            MovieSeatType.chair -> {
                R.layout.layout_movie_seat_item
            }

            MovieSeatType.lineText -> {
                R.layout.layout_movie_seat_text_item
            }
        }

    override fun areItemsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is MovieSeatItemModel) return false
        else newItem.id == id
    }

    override fun areContentsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is MovieSeatItemModel) return false
        else newItem.isChosen == isChosen
                && newItem.movieSeatType == movieSeatType
                && newItem.lineText == lineText
    }
}

enum class MovieSeatType {
    chair,
    lineText
}

interface MovieSeatInteract {
    fun chooseSeat(id: String)
}