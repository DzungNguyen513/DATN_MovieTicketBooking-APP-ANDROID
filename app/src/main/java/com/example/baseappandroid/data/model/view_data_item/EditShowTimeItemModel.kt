package com.example.baseappandroid.data.model.view_data_item

import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseViewData

data class EditShowTimeItemModel(
    val time: String,
    val interact: EditShowTimeInteract? = null
) : BaseViewData {
    override val layoutRes: Int
        get() = R.layout.layout_edit_show_time_item

    override fun areItemsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is EditShowTimeItemModel) false
        else newItem.time == time
    }

    override fun areContentsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is EditShowTimeItemModel) false
        else newItem.time == time
    }

}

interface EditShowTimeInteract {
    fun removeShowTime(time: String)
}