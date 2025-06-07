package com.example.baseappandroid.data.model.view_data_item

data class SeatItemModel(
    val time: String?,
    val roomId: String?,
    val roomName: String?,
    val seats: Long?,
    val availableSeats: Long?,
    val bookedSeats: List<String>?
) {
    constructor() : this(null, null, null, null, null, null)
}
