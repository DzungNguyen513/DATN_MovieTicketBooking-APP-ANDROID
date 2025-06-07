package com.example.baseappandroid.data.model.view_data_item

import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseViewData
import java.time.LocalDate

data class DateItemModel(
    val days: String,
    val dates: String,
    val localDate: LocalDate,
    val interact: DateItemInteract?,
    val chosen: Boolean = false
) : BaseViewData {
    override val layoutRes: Int
        get() = R.layout.layout_date_item

    override fun areItemsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is DateItemModel) false
        else newItem.dates == dates
    }

    override fun areContentsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is DateItemModel) false
        else newItem.days == days && newItem.chosen == chosen
    }

}

interface DateItemInteract {
    fun onDateChosen(date: LocalDate)
}
