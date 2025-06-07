package com.example.baseappandroid.data.repository

import com.example.baseappandroid.data.model.MovieShowTime
import com.example.baseappandroid.data.model.Result
import com.example.baseappandroid.data.model.RoomItemModel
import com.example.baseappandroid.data.model.view_data_item.WeeklyMovieItemModel
import com.example.baseappandroid.di.IoDispatcher
import com.example.baseappandroid.utils.GlobalData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoomRepository @Inject constructor(
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) {

    suspend fun fetchAllRooms(): Result<List<RoomItemModel>> =
        withContext(coroutineDispatcher) {
            try {
                val data = GlobalData.firebaseDB.collection("room").get().await()
                val list = arrayListOf<RoomItemModel>()
                if (data != null) {
                    for (i in data.documents) {
                        val model = i.toObject(RoomItemModel::class.java)
                        if (model != null) list.add(model)
                    }
                    return@withContext Result.Success(data = list)
                } else {
                    return@withContext Result.Error(errorMessage = "Không tìm thấy data!")
                }
            } catch (ex: Exception) {
                return@withContext Result.Error(errorMessage = ex.message.toString())
            }
        }

    suspend fun addRoom(room: RoomItemModel): Result<String> = withContext(coroutineDispatcher) {
        try {
            val id = GlobalData.firebaseDB.collection("room").document().id
            val hm = hashMapOf<String, Any>(
                "name" to room.name.toString(),
                "seats" to (room.seats ?: 0),
                "roomId" to id
            )
            GlobalData.firebaseDB.collection("room").document(id).set(hm)
            return@withContext Result.Success(data = id)
        } catch (ex: Exception) {
            return@withContext Result.Error(errorMessage = ex.message.toString())
        }
    }

    suspend fun updateRoom(room: RoomItemModel): Result<String> = withContext(coroutineDispatcher) {
        try {
            val hm = hashMapOf<String, Any>(
                "name" to room.name.toString(),
                "seats" to (room.seats ?: 0),
                "roomId" to room.roomId.toString()
            )
            GlobalData.firebaseDB.collection("room").document(room.roomId.toString()).update(hm)
            return@withContext Result.Success(data = room.roomId.toString())
        } catch (ex: Exception) {
            return@withContext Result.Error(errorMessage = ex.message.toString())
        }
    }

    suspend fun deleteRoom(room: String): Result<String> = withContext(coroutineDispatcher) {
        try {
            val roomDB = GlobalData.firebaseDB.collection("room")
            val movieShowTimeDB = GlobalData.firebaseDB.collection("movie_showtime")
            val showTimeDB = GlobalData.firebaseDB.collection("showtime")
            roomDB.document(room).delete().await()
            val list = arrayListOf<MovieShowTime>()
            val data = movieShowTimeDB.whereEqualTo("roomId", room).get().await()
            for (i in data.documents) {
                val model = i.toObject(MovieShowTime::class.java)
                if (model != null) {
                    list.add(model)
                }
            }
            for (i in list) {
                movieShowTimeDB.document(i.id.toString()).delete().await()
                val showTimeData =
                    (showTimeDB.document("${i.movieId}_${i.date}").get().await()).toObject(
                        WeeklyMovieItemModel::class.java
                    )
                if (showTimeData != null) {
                    val times = arrayListOf<String>()
                    times.addAll(showTimeData.times.orEmpty())
                    if (times.contains(i.startTime)) times.remove(i.startTime)
                    val updateShowTimeModel = showTimeData.copy(times = times)
                    showTimeDB.document("${i.movieId}_${i.date}")
                        .update(updateShowTimeModel.convertToMap()).await()
                }
            }
            return@withContext Result.Success(data = room)
        } catch (ex: Exception) {
            return@withContext Result.Error(errorMessage = ex.message.toString())
        }
    }
}