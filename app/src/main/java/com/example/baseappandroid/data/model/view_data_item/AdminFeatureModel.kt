package com.example.baseappandroid.data.model.view_data_item

import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseViewData

data class AdminFeatureModel(
    val id: Int,
    val feature: AdminFeature,
    val interact: AdminFeatureInteract,
    val image: Int,
) : BaseViewData {
    override val layoutRes: Int
        get() = R.layout.layout_admin_feature_item

    override fun areItemsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is AdminFeatureModel) false
        else newItem.id == id
    }

    override fun areContentsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is AdminFeatureModel) false
        else newItem.feature == feature && newItem.image == image
    }
}

enum class AdminFeature(val value: String) {
    MOVIE_MANAGEMENT("Quản lý phim"),
    SHOW_TIME_MANAGEMENT("Quản lý suất chiếu"),
    ROOM_MANAGEMENT("Quản lý phòng"),
    TICKET_MANAGEMENT("Quản lý vé"),
    INCOME_MANAGEMENT("Quản lý doanh thu"),
    SNACK_MANAGEMENT("Quản lý đồ ăn - uống"),
}

const val MOVIE_MANAGEMENT_ICON = R.drawable.ic_movie
const val SHOW_TIME_MANAGEMENT_ICON = R.drawable.ic_movie_scheduling
const val ROOM_MANAGEMENT_ICON = R.drawable.ic_room
const val TICKET_MANAGEMENT_ICON = R.drawable.ic_ticket
const val INCOME_MANAGEMENT_ICON = R.drawable.ic_statistics
const val SNACK_MANAGEMENT_ICON = R.drawable.ic_snack

public interface AdminFeatureInteract {
    fun onFeatureClick(feature: AdminFeature)
}