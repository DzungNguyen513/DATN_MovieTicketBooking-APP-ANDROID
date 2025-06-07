package com.example.baseappandroid.data.repository

import com.example.baseappandroid.data.model.BillItemModel
import com.example.baseappandroid.data.model.BookTicketModel
import com.example.baseappandroid.data.model.Result
import com.example.baseappandroid.data.model.arg.NavigateToBookTicketModel
import com.example.baseappandroid.data.model.view_data_item.SeatItemModel
import com.example.baseappandroid.di.IoDispatcher
import com.example.baseappandroid.utils.GlobalData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SeatRepository @Inject constructor(
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend fun updateSeatsAndBillAfterSuccessfulPayment(
        model: BookTicketModel,
        onResult: (String?) -> Unit
    ) =
        withContext(coroutineDispatcher) {
            val seatDB = GlobalData.firebaseDB.collection("chair").document(model.bookedDate)
                .collection(model.movieId).document(model.bookedTime)
            val billDB = GlobalData.firebaseDB.collection("bill").document()
            val data = seatDB.get().await()
            if (data != null) {
                val modelData = data.toObject(SeatItemModel::class.java)
                if (modelData != null) {
                    GlobalData.firebaseDB.runTransaction { transaction ->
                        val bookedSeatsList = arrayListOf<String>()
                        modelData.bookedSeats.orEmpty().let { bookedSeatsList.addAll(it) }
                        bookedSeatsList.addAll(model.seats)
                        val hm = hashMapOf(
                            "availableSeats" to ((modelData.availableSeats
                                ?: 0) - model.seats.size),
                            "bookedSeats" to bookedSeatsList.toList()
                        )
                        transaction.update(seatDB, hm)

                        val billHM = BillItemModel(
                            id = billDB.id,
                            movieName = model.movieName,
                            movieId = model.movieId,
                            roomName = model.roomName,
                            seats = model.seats,
                            bookedDate = model.bookedDate,
                            bookedTime = model.bookedTime,
                            createdAt = System.currentTimeMillis().toString(),
                            userId = GlobalData.user?.id.toString(),
                            userPhone = GlobalData.user?.mobilePhone.toString(),
                            userName = GlobalData.user?.fullName.toString(),
                            status = 0
                            )
                        transaction.set(billDB, billHM)
                        null
                    }.addOnSuccessListener {
                        onResult(billDB.id)
                    }.addOnFailureListener {
                        onResult(null)
                    }
                }
            }
        }

    suspend fun getSeatsInformation(model: NavigateToBookTicketModel): Result<SeatItemModel> =
        withContext(coroutineDispatcher) {
            try {
                val data =
                    GlobalData.firebaseDB.collection("chair").document(model.bookDate.trim())
                        .collection(model.movieId).document(model.bookTime.trim()).get().await()
                if (data != null) {
                    val modelData = data.toObject(SeatItemModel::class.java)
                    return@withContext Result.Success(data = modelData ?: SeatItemModel())
                }
                return@withContext Result.Error(errorMessage = "Error")
            } catch (ex: Exception) {
                return@withContext Result.Error(errorMessage = ex.message.toString())
            }
        }

}