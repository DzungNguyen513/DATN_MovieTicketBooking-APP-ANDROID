package com.example.baseappandroid.data.api

import com.example.baseappandroid.data.model.FCMResponse
import com.example.baseappandroid.data.model.request.FcmMessageRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AppApi {
    @POST("fcm/send")
    suspend fun sendFcm(@Body request: FcmMessageRequest): Response<FCMResponse>
}