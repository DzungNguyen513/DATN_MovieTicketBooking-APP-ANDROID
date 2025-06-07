package com.example.baseappandroid.data.model

import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseViewData

data class BillItemModel(
    val id: String?,
    val movieName: String?,
    val movieId: String?,
    val roomName: String?,
    val seats: List<String>?,
    val bookedDate: String?,
    val bookedTime: String?,
    val createdAt: String?,
    val userId: String?,
    val userPhone: String?,
    val userName: String?,
    val movieImage: String? = null,
    val interact: BillItemInteract? = null,
    val status: Int?
) : BaseViewData {
    constructor() : this(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    )

    fun toHashMapData(): HashMap<String, Any?> {
        return hashMapOf(
            "id" to id,
            "movieName" to movieName,
            "movieId" to movieId,
            "roomName" to roomName,
            "seats" to seats,
            "bookedDate" to bookedDate,
            "bookedTime" to bookedTime,
            "createdAt" to createdAt,
            "userId" to userId,
            "userPhone" to userPhone,
            "userName" to userName
        )
    }

    override val layoutRes: Int
        get() = R.layout.layout_movie_history_item

    override fun areItemsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is BillItemModel) false
        else newItem.id == id
    }

    override fun areContentsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is BillItemModel) false
        else newItem.movieImage == movieImage
                && newItem.movieName == movieName
                && newItem.bookedDate == bookedDate
                && newItem.bookedTime == bookedTime
    }
}

interface BillItemInteract {
    fun onBillClick(id: String)
}

enum class TicketStatusType(val value: Int) {
    NOT_CONFIRM(0),
    CONFIRM(1),
    CANCEL_TICKET(2)
}
