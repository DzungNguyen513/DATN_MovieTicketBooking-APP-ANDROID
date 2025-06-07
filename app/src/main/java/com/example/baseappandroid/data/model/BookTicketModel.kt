package com.example.baseappandroid.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookTicketModel(
    val id: String,
    val movieName: String,
    val movieId: String,
    val seats: List<String>,
    val totalMoney: String,
    val bookedDate: String,
    val bookedTime: String,
    val roomId: String,
    val roomName: String,
) : Parcelable