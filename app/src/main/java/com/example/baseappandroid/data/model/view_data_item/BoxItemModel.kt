package com.example.baseappandroid.data.model.view_data_item

import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseViewData

data class BoxItemModel(
    val id: Int,
    val height: Int?,
    val width: Int?,
    val backgroundColor: Int? = null,
    override val layoutRes: Int = R.layout.layout_box_height_item
) : BaseViewData {
    override fun areItemsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is BoxItemModel) false
        else newItem.id == id
    }

    override fun areContentsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is BoxItemModel) false
        else newItem.height == height && newItem.width == width && newItem.backgroundColor == backgroundColor
    }

}