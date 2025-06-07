package com.example.baseappandroid.data.repository

import com.example.baseappandroid.data.model.Result
import com.example.baseappandroid.data.model.view_data_item.MovieItemModel
import com.example.baseappandroid.data.model.view_data_item.toMap
import com.example.baseappandroid.di.IoDispatcher
import com.example.baseappandroid.utils.GlobalData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MovieRepository @Inject constructor(
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) {

    suspend fun fetchMoviesData(): Result<List<MovieItemModel>> =
        withContext(coroutineDispatcher) {
            try {
                val getData = GlobalData.firebaseDB.collection("movies").get().await()
                val list = arrayListOf<MovieItemModel>()
                for (i in getData) {
                    val model = i.toObject(MovieItemModel::class.java)
                    list.add(model)
                }
                return@withContext Result.Success(list)
            } catch (ex: Exception) {
                return@withContext Result.Error(errorMessage = ex.message.toString())
            }
        }

    suspend fun getDetailMovie(id: String): Result<MovieItemModel> =
        withContext(coroutineDispatcher) {
            try {
                val getData = GlobalData.firebaseDB.collection("movies").document(id).get().await()
                if (getData.data != null) {
                    val model = getData.toObject(MovieItemModel::class.java)
                    if (model != null) return@withContext Result.Success(data = model)
                }
                return@withContext Result.Error(errorMessage = "Can not find movie!")
            } catch (ex: Exception) {
                return@withContext Result.Error(errorMessage = ex.message.toString())
            }
        }

    suspend fun getAlLMoviesRealTime(
        callBack: (List<MovieItemModel>) -> Unit
    ) = withContext(coroutineDispatcher) {
        GlobalData.firebaseDB.collection("movies").addSnapshotListener { snapshots, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            if (snapshots != null) {
                val movies = snapshots.map { it.toObject(MovieItemModel::class.java) }
                callBack(movies)
            }

        }
    }

    suspend fun deleteMovie(id: String): Result<Boolean> = withContext(coroutineDispatcher) {
        try {
            GlobalData.firebaseDB.collection("movies").document(id).delete().await()
            return@withContext Result.Success(data = true)
        } catch (ex: Exception) {
            return@withContext Result.Error(errorMessage = ex.message.toString())
        }
    }

    suspend fun addMovie(
        movieItem: MovieItemModel,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) =
        withContext(coroutineDispatcher) {
            try {
                val id = GlobalData.firebaseDB.collection("movies").document().id
                val data = movieItem.copy(id = id)
                val hm = data.toMap()
                GlobalData.firebaseDB.collection("movies").document(id).set(hm).await()
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    onError(ex.message.toString())
                }
            }
        }

    suspend fun updateMovie(
        movieItem: MovieItemModel,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) =
        withContext(coroutineDispatcher) {
            try {
                val hm = movieItem.toMap()
                GlobalData.firebaseDB.collection("movies").document(movieItem.id.toString())
                    .update(hm).await()
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    onError(ex.message.toString())
                }
            }
        }
}