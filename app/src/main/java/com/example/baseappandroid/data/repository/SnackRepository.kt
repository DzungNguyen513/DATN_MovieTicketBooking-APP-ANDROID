package com.example.baseappandroid.data.repository

import com.example.baseappandroid.data.model.Result
import com.example.baseappandroid.data.model.SnackItemModel
import com.example.baseappandroid.di.IoDispatcher
import com.example.baseappandroid.utils.GlobalData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SnackRepository @Inject constructor(
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) {

    suspend fun fetchSnacks(): Result<List<SnackItemModel>> = withContext(coroutineDispatcher) {
        try {
            val data = GlobalData.firebaseDB.collection("snack").get().await()
            val list = arrayListOf<SnackItemModel>()
            for (i in data.documents) {
                val model = i.toObject(SnackItemModel::class.java)
                if (model != null) {
                    list.add(model)
                }
            }
            return@withContext Result.Success(data = list)
        } catch (ex: Exception) {
            return@withContext Result.Error(errorMessage = ex.message.toString())
        }
    }

    suspend fun fetchDetailSnack(id: String): Result<SnackItemModel> =
        withContext(coroutineDispatcher) {
            try {
                val modelDB = GlobalData.firebaseDB.collection("snack").document(id).get().await()
                if (modelDB.data == null) {
                    return@withContext Result.Error(errorMessage = "Không có dữ liệu!")
                } else {
                    val data = modelDB.toObject(SnackItemModel::class.java)
                    if (data != null) {
                        return@withContext Result.Success(data = data)
                    } else {
                        return@withContext Result.Error(errorMessage = "Không có dữ liệu!")
                    }
                }
            } catch (ex: Exception) {
                return@withContext Result.Error(errorMessage = ex.message.toString())
            }
        }

    suspend fun addSnack(model: SnackItemModel): Result<String> = withContext(coroutineDispatcher) {
        try {
            val db = GlobalData.firebaseDB.collection("snack")
            val id = db.document().id
            val saveModel = (model.copy(id = id)).toMapValue()
            db.document(id).set(saveModel).await()
            return@withContext Result.Success(data = "")
        } catch (ex: Exception) {
            return@withContext Result.Error(errorMessage = ex.message.toString())
        }
    }

    suspend fun updateSnack(model: SnackItemModel): Result<String> =
        withContext(coroutineDispatcher) {
            try {
                val db = GlobalData.firebaseDB.collection("snack").document(model.id.toString())
                val saveModel = model.toMapValue()
                db.update(saveModel).await()
                return@withContext Result.Success(data = "")
            } catch (ex: Exception) {
                return@withContext Result.Error(errorMessage = ex.message.toString())
            }
        }

    suspend fun deleteSnack(id: String): Result<String> = withContext(coroutineDispatcher) {
        try {
            GlobalData.firebaseDB.collection("snack").document(id).delete().await()
            return@withContext Result.Success(data = "")
        } catch (ex: Exception) {
            return@withContext Result.Error(errorMessage = ex.message.toString())
        }
    }

}