package com.example.baseappandroid.data.repository

import com.example.baseappandroid.data.model.BillItemModel
import com.example.baseappandroid.data.model.Result
import com.example.baseappandroid.data.model.TicketStatusType
import com.example.baseappandroid.data.model.view_data_item.MovieItemModel
import com.example.baseappandroid.data.model.view_data_item.SeatItemModel
import com.example.baseappandroid.di.IoDispatcher
import com.example.baseappandroid.utils.GlobalData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BillRepository @Inject constructor(
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend fun fetchDetailBill(id: String): Result<BillItemModel> =
        withContext(coroutineDispatcher) {
            try {
                val data = GlobalData.firebaseDB.collection("bill").document(id).get().await()
                val model = data.toObject(BillItemModel::class.java)
                if (model != null) {
                    return@withContext Result.Success(data = model)
                } else {
                    return@withContext Result.Error(errorMessage = "Không tìm thấy data!")
                }
            } catch (ex: Exception) {
                return@withContext Result.Error(errorMessage = ex.message.toString())
            }
        }

    suspend fun updateDetailBillStatus(id: String, status: Int, bill: BillItemModel?): Result<String> =
        withContext(coroutineDispatcher) {
            try {
                val hm = hashMapOf<String, Any>(
                    "status" to status
                )
                GlobalData.firebaseDB.collection("bill").document(id).update(hm).await()
                val ticket = bill ?: (GlobalData.firebaseDB.collection("bill").document(id).get().await()).toObject(
                    BillItemModel::class.java
                )
                if (status == TicketStatusType.CANCEL_TICKET.value) {
                    val listOfBooked = ticket?.seats.orEmpty()
                    val movieID = ticket?.movieId.toString().trim()
                    val date = ticket?.bookedDate.toString().trim()
                    val bookedTime = ticket?.bookedTime.toString().trim()
                    val modelSeat = (GlobalData.firebaseDB.collection("chair").document(date).collection(movieID)
                        .document(bookedTime)
                        .get().await()).toObject(SeatItemModel::class.java)
                    if (modelSeat != null) {
                        val list = modelSeat.bookedSeats?.filterNot { e -> e in listOfBooked }
                        val hmSeat = hashMapOf(
                            "availableSeats" to ((modelSeat.availableSeats)?.plus(listOfBooked.size) ?: 60),
                            "bookedSeats" to list
                        )
                        GlobalData.firebaseDB.collection("chair").document(date).collection(movieID)
                            .document(bookedTime).update(hmSeat)
                    }
                }
                return@withContext Result.Success(data = "Thành công")
            } catch (ex: Exception) {
                return@withContext Result.Error(errorMessage = ex.message.toString())
            }
        }

    suspend fun fetchListBills(userId: String): Result<List<BillItemModel>> =
        withContext(coroutineDispatcher) {
            try {
                val data = if (userId.isNotEmpty()) {
                    GlobalData.firebaseDB.collection("bill").whereEqualTo("userId", userId).get()
                        .await()
                } else {
                    GlobalData.firebaseDB.collection("bill").get().await()
                }
                val list = arrayListOf<BillItemModel>()
                for (i in data) {
                    val model = i.toObject(BillItemModel::class.java)
                    var movieImage = ""
                    if (model.movieId != null) {
                        val movie =
                            (GlobalData.firebaseDB.collection("movies").document(model.movieId)
                                .get().await()).toObject(
                                    MovieItemModel::class.java
                                )
                        if (movie != null) {
                            movieImage = movie.posterUrl.toString()
                        }
                    }
                    list.add(model.copy(movieImage = movieImage))
                }
                if (list.isEmpty()) {
                    return@withContext Result.Error(errorMessage = "Không tìm thấy data!")
                }
                return@withContext Result.Success(data = list.toList())
            } catch (ex: Exception) {
                return@withContext Result.Error(errorMessage = ex.message.toString())
            }
        }

    suspend fun fetchListBillsByPhone(phoneNumber: String): Result<List<BillItemModel>> =
        withContext(coroutineDispatcher) {
            try {
                val data = if (phoneNumber.isEmpty()) {
                    GlobalData.firebaseDB.collection("bill")
                        .whereEqualTo("status", 0).get().await()
                } else {
                    GlobalData.firebaseDB.collection("bill")
                        .whereGreaterThanOrEqualTo("userPhone", phoneNumber)
                        .whereLessThan("userPhone", phoneNumber + "\uf8ff").get().await()
                }
                val list = arrayListOf<BillItemModel>()
                for (i in data) {
                    val model = i.toObject(BillItemModel::class.java)
                    var movieImage = ""
                    if (model.movieId != null) {
                        val movie =
                            (GlobalData.firebaseDB.collection("movies").document(model.movieId)
                                .get().await()).toObject(
                                    MovieItemModel::class.java
                                )
                        if (movie != null) {
                            movieImage = movie.posterUrl.toString()
                        }
                    }
                    list.add(model.copy(movieImage = movieImage))
                }
                if (list.isEmpty()) {
                    return@withContext Result.Error(errorMessage = "Không tìm thấy data!")
                }
                return@withContext Result.Success(data = list.toList())
            } catch (ex: Exception) {
                return@withContext Result.Error(errorMessage = ex.message.toString())
            }
        }
}