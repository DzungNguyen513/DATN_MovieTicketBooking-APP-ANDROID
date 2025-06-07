package com.example.baseappandroid.data.model.view_data_item

import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseViewData

data class StatisticItemViewData(
    val id: String,
    val movieName: String,
    val movieImage: String,
    val numberOfTickets: Int,
    val income: Double
) : BaseViewData {
    override val layoutRes: Int
        get() = R.layout.layout_movie_statistic_item

    override fun areItemsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is StatisticItemViewData) false
        else newItem.id == id
    }

    override fun areContentsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is StatisticItemViewData) false
        else newItem.movieName == movieName
                && newItem.numberOfTickets == numberOfTickets
                && newItem.income == income
                && newItem.movieImage == movieImage
    }
}
