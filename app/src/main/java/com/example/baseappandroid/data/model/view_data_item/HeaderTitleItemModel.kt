package com.example.baseappandroid.data.model.view_data_item

import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseViewData

data class HeaderTitleItemModel(
    val id: Int,
    val title: String,
    val showForward: Boolean?,
    val interact: HeaderTitleInteract,
    override val layoutRes: Int = R.layout.layout_header_title_item
) :
    BaseViewData {
    override fun areItemsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is HeaderTitleItemModel) false
        else newItem.id == id
    }

    override fun areContentsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is HeaderTitleItemModel) false
        else newItem.title == title && newItem.showForward == showForward
    }

}

interface HeaderTitleInteract {
    fun onForwardAction(title: String)
}