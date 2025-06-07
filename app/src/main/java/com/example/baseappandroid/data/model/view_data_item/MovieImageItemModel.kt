package com.example.baseappandroid.data.model.view_data_item

import android.net.Uri
import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseViewData

data class MovieImageItemModel(
    val uri: Uri?,
    val imagesString: String? = null,
) : BaseViewData {
    override val layoutRes: Int
        get() = R.layout.layout_movie_image_item

    override fun areItemsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is MovieImageItemModel) false
        else if(uri != null) newItem.uri == uri
        else newItem.imagesString == imagesString
    }

    override fun areContentsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is MovieImageItemModel) false
        else if(uri != null) newItem.uri == uri
        else newItem.imagesString == imagesString
    }
}