package com.example.baseappandroid.data.repository

import com.example.baseappandroid.data.model.MovieShowTime
import com.example.baseappandroid.data.model.Result
import com.example.baseappandroid.data.model.RoomItemModel
import com.example.baseappandroid.data.model.ShowTimeItemModel
import com.example.baseappandroid.data.model.ShowTimeModel
import com.example.baseappandroid.data.model.view_data_item.MovieItemModel
import com.example.baseappandroid.data.model.view_data_item.SeatItemModel
import com.example.baseappandroid.data.model.view_data_item.WeeklyMovieItemModel
import com.example.baseappandroid.di.IoDispatcher
import com.example.baseappandroid.utils.GlobalData
import com.example.baseappandroid.utils.GlobalFunction
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class ShowTimeRepository @Inject constructor(
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend fun addWeeklyMovie(
        data: WeeklyMovieItemModel,
        seatItemModel: SeatItemModel,
        chosenShowTime: String,
        movieDuration: Int
    ): Result<String> =
        withContext(coroutineDispatcher) {
            try {
                val movieDB = GlobalData.firebaseDB.collection("showtime")
                    .document("${data.movieId}_${data.date}")
                val moviesShowTimes =
                    (movieDB.get()
                        .await()).toObject(WeeklyMovieItemModel::class.java)?.times.orEmpty()
                val list = arrayListOf<String>()
                list.addAll(moviesShowTimes)
                list.add(chosenShowTime)
                val hm = hashMapOf<String, Any>(
                    "id" to "${data.movieId}_${data.date}".trim(),
                    "movieId" to data.movieId.toString().trim(),
                    "date" to data.date.toString().trim(),
                    "times" to list
                )
                val room =
                    (GlobalData.firebaseDB.collection("room")
                        .document(seatItemModel.roomId.toString().trim())
                        .get()
                        .await()).toObject(RoomItemModel::class.java)

                movieDB.set(hm, SetOptions.merge()).addOnSuccessListener {
                    val chairDB =
                        GlobalData.firebaseDB.collection("chair").document(data.date.toString().trim())
                            .collection(data.movieId.toString().trim())

                    val hashMapData = hashMapOf<String, Any?>(
                        "time" to chosenShowTime.trim(),
                        "seats" to ((room?.seats) ?: 60),
                        "roomId" to seatItemModel.roomId?.trim(),
                        "roomName" to ((room?.name?.trim()) ?: "P"),
                        "availableSeats" to room?.seats,
                        "bookedSeats" to seatItemModel.availableSeats
                    )
                    chairDB.document(chosenShowTime.trim()).set(hashMapData).addOnSuccessListener {
                        addMovieShowTimeRef(
                            chosenShowTime,
                            data.date.toString(),
                            seatItemModel.roomId.toString(),
                            movieDuration,
                            data.movieId.toString().trim()
                        )
                    }
                }
                return@withContext Result.Success(data = "Thêm thành công!")
            } catch (ex: Exception) {
                return@withContext Result.Error(errorMessage = ex.message.toString())
            }
        }

    suspend fun getWeeklyMovieData(date: String): Result<List<WeeklyMovieItemModel>> =
        withContext(coroutineDispatcher) {
            try {
                val data =
                    GlobalData.firebaseDB.collection("showtime").whereEqualTo("date", date).get()
                        .await()
                val movieDB = GlobalData.firebaseDB.collection("movies")
                val list = arrayListOf<WeeklyMovieItemModel>()
                for (i in data.documents) {
                    val model = i.toObject(WeeklyMovieItemModel::class.java)
                    val movie =
                        (movieDB.document(model?.movieId.toString()).get().await()).toObject(
                            MovieItemModel::class.java
                        )
                    if (model != null) {
                        list.add(model.copy(movie = movie))
                    }
                }
                return@withContext Result.Success(data = list)
            } catch (ex: Exception) {
                return@withContext Result.Error(errorMessage = ex.message.toString())
            }
        }

    suspend fun deleteWeeklyMovieData(id: String): Result<String> =
        withContext(coroutineDispatcher) {
            try {
                GlobalData.firebaseDB.collection("showtime").document(id).delete().await()
                val movieId = id.split("_")[0]
                val date = id.split("_")[1]
                if (movieId.isNotEmpty() && date.isNotEmpty()) {
                    val data = GlobalData.firebaseDB.collection("movie_showtime").get().await()
                    for (i in data.documents) {
                        val model = i.toObject(MovieShowTime::class.java)
                        if (model != null) {
                            if (date == model.date && model.movieId == movieId) {
                                GlobalData.firebaseDB.collection("movie_showtime").document(model.id.toString().trim())
                                    .delete().await()
                            }
                        }
                    }
                }
                return@withContext Result.Success(data = "")
            } catch (ex: Exception) {
                return@withContext Result.Error(errorMessage = ex.message.toString())
            }
        }

    suspend fun updateWeeklyMovieData(id: String, times: List<String>, chosenTime: String): Result<String> =
        withContext(coroutineDispatcher) {
            try {
                val hm = hashMapOf<String, Any>(
                    "times" to times
                )
                val date = id.split("_")[1]
                val movieId = id.split("_")[0]
                GlobalData.firebaseDB.collection("showtime").document(id).update(hm).await()
                if (date.isNotEmpty() && movieId.isNotEmpty()) {
                    val room =
                        (GlobalData.firebaseDB.collection("chair").document(date).collection(movieId)
                            .document(chosenTime.trim()).get().await()).toObject(
                                SeatItemModel::class.java
                            )
                    if (room != null) {
                        deleteMovieShowTimeRef(chosenTime, date, room.roomId.toString().trim(), movieId)
                    }
                }

                return@withContext Result.Success(data = "")
            } catch (ex: Exception) {
                return@withContext Result.Error(errorMessage = ex.message.toString())
            }
        }

    suspend fun getDetailMovieTimeData(
        movieId: String,
        date: String
    ): Result<WeeklyMovieItemModel> =
        withContext(coroutineDispatcher) {
            try {
                var data =
                    (GlobalData.firebaseDB.collection("showtime")
                        .document("${movieId}_${date}")
                        .get().await()).toObject(WeeklyMovieItemModel::class.java)
                val movie =
                    (GlobalData.firebaseDB.collection("movies").document(data?.movieId.toString())
                        .get().await()).toObject(
                            MovieItemModel::class.java
                        )
                if (movie != null && data != null) {
                    data = data.copy(movie = movie)
                }
                if (data != null) {
                    return@withContext Result.Success(data = data)
                } else {
                    return@withContext Result.Error(errorMessage = "Không tìm thấy dữ liệu!")
                }
            } catch (ex: Exception) {
                return@withContext Result.Error(errorMessage = ex.message.toString())
            }
        }

    suspend fun checkIfExist(
        time: String,
        date: String,
        roomId: String,
        duration: Int
    ): Result<Boolean> = withContext(coroutineDispatcher) {
        try {
            var isExist = false
            val data = GlobalData.firebaseDB.collection("movie_showtime").get().await()
            for (i in data.documents) {
                val model = i.toObject(MovieShowTime::class.java)
                if (model != null) {
                    if (roomId.trim() == model.roomId) {
                        val endTime =
                            GlobalFunction.convertTimeAndDateIntoDateTime(
                                model.endTime,
                                model.endDate
                            )
                        val beforeTime = GlobalFunction.minusMinutesForTime(
                            model.startTime.toString(),
                            duration,
                            model.date.toString()
                        )
                        val chosenTime = GlobalFunction.convertTimeAndDateIntoDateTime(time, date)
                        if (chosenTime?.before(endTime) == true && chosenTime.after(beforeTime)) {
                            isExist = true
                            break
                        }
                    }
                }
            }
            return@withContext Result.Success(data = isExist)
        } catch (ex: Exception) {
            return@withContext Result.Error(errorMessage = ex.message.toString())
        }
    }

    private suspend fun deleteMovieShowTimeRef(
        time: String,
        date: String,
        roomId: String,
        movieId: String
    ) = withContext(coroutineDispatcher) {
        try {
            var ref: MovieShowTime? = null
            val data = GlobalData.firebaseDB.collection("movie_showtime").get().await()
            for (i in data.documents) {
                val model = i.toObject(MovieShowTime::class.java)
                if (model != null) {
                    if (roomId.trim() == model.roomId.toString().trim()
                        && time.trim() == model.startTime.toString().trim()
                        && date.trim() == model.date.toString().trim()
                        && model.movieId.toString().trim() == movieId.trim()
                    ) {
                        ref = model
                        break
                    }
                }
            }
            if (ref != null) GlobalData.firebaseDB.collection("movie_showtime")
                .document(ref.id.toString()).delete().await()
        } catch (_: Exception) {
        }
    }

    private fun addMovieShowTimeRef(
        time: String,
        date: String,
        roomId: String,
        duration: Int,
        movieId: String
    ) {
        try {
            val data = GlobalData.firebaseDB.collection("movie_showtime")
            val id = data.document().id
            val end = GlobalFunction.addMinutesForTime(time, duration, date)
            val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val endTime = end?.time?.let { timeFormatter.format(it) }
            val endDate = end?.time?.let { dateFormatter.format(it) }
            val model = MovieShowTime(
                id = id.trim(),
                startTime = time.trim(),
                endTime = endTime?.trim(),
                roomId = roomId.trim(),
                date = date.trim(),
                endDate = endDate?.trim(),
                movieId = movieId.trim()
            )
            data.document(id).set(model.convertToMap())
        } catch (ex: Exception) {
        }
    }
}