package com.example.baseappandroid.data.model.view_data_item

import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseViewData

data class SearchItemModel(
    val id: Int,
    val interact: SearchItemModelInteract
) : BaseViewData {
    override val layoutRes: Int
        get() = R.layout.layout_edt_search_item

    override fun areItemsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is SearchItemModel) false
        else newItem.id == id
    }

    override fun areContentsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is SearchItemModel) false
        else newItem == this
    }

}

interface SearchItemModelInteract {
    fun searchMovieAction()
}