package com.example.baseappandroid.data.repository

import com.example.baseappandroid.data.api.AppApi
import com.example.baseappandroid.data.model.request.FcmMessageRequest
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FCMRepository @Inject constructor(
    private val api: AppApi,
) {

    suspend fun sendFcm(request: FcmMessageRequest) = withContext(NonCancellable) {
        api.sendFcm(request)
    }
}