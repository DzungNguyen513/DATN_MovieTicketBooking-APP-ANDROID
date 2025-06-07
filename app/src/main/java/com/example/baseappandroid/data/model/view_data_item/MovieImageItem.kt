package com.example.baseappandroid.data.model.view_data_item

import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseViewData

data class MovieImageItem(
    val id: Int,
    val image: String
) : BaseViewData {
    override val layoutRes: Int
        get() = R.layout.layout_detail_movie_image_item

    override fun areItemsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is MovieImageItem) false
        else newItem.id == id
    }

    override fun areContentsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is MovieImageItem) false
        else newItem.image == image
    }
}