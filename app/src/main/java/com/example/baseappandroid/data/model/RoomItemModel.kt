package com.example.baseappandroid.data.model

import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseViewData

data class RoomItemModel(
    val roomId: String?,
    val name: String?,
    val seats: Long?,
    val interact: RoomItemInteract? = null
) : BaseViewData {
    constructor() : this(null, null, null)

    override val layoutRes: Int
        get() = R.layout.layout_room_item

    override fun areItemsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is RoomItemModel) false
        else newItem.roomId == roomId
    }

    override fun areContentsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is RoomItemModel) false
        else newItem.name == name && newItem.seats == seats
    }
}

interface RoomItemInteract {
    fun onRoomItemClick(id: RoomItemModel)
}