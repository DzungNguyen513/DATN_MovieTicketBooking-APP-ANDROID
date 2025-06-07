package com.example.baseappandroid.data.model.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FcmMessageRequest(
    val topic: String,
    val title: String,
    val message: String
)
