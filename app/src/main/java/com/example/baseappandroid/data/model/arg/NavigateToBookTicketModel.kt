package com.example.baseappandroid.data.model.arg

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NavigateToBookTicketModel(
    val movieId: String,
    val movieName: String,
    val bookDate: String,
    val bookTime: String,
) : Parcelable
