package com.example.baseappandroid.ui.admin.movie

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseappandroid.base.viewmodel.BaseViewModel
import com.example.baseappandroid.data.model.Result
import com.example.baseappandroid.data.model.request.FcmMessageRequest
import com.example.baseappandroid.data.model.view_data_item.MovieImageItemModel
import com.example.baseappandroid.data.model.view_data_item.MovieItemModel
import com.example.baseappandroid.data.model.view_data_item.MovieType
import com.example.baseappandroid.data.repository.FCMRepository
import com.example.baseappandroid.data.repository.MovieRepository
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MovieManagementViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val fcmRepository: FCMRepository
) : BaseViewModel() {

    // movie images
    val listOfImages = arrayListOf<MovieImageItemModel>()

    private var _movieList = MutableLiveData<List<MovieItemModel>>()
    val movieList: LiveData<List<MovieItemModel>>
        get() = _movieList

    private var _movieDetailResponse = MutableLiveData<MovieItemModel>()
    val movieDetailResponse: LiveData<MovieItemModel>
        get() = _movieDetailResponse

    private var _deleteMovieAction = MutableLiveData<String?>()
    val deleteMovieAction: LiveData<String?>
        get() = _deleteMovieAction

    private var _editMovieAction = MutableLiveData<String?>()
    val editMovieAction: LiveData<String?>
        get() = _editMovieAction

    private var _deleteMovieResponse = MutableLiveData<String?>()
    val deleteMovieResponse: LiveData<String?>
        get() = _deleteMovieResponse

    private var _addMovieResponse = MutableLiveData<String?>()
    val addMovieResponse: LiveData<String?>
        get() = _addMovieResponse

    private var _updateMovieResponse = MutableLiveData<String?>()
    val updateMovieResponse: LiveData<String?>
        get() = _updateMovieResponse

    fun clearDeleteFeature() {
        _deleteMovieAction.value = null
    }

    fun clearEditFeature() {
        _editMovieAction.value = null
    }

    init {
        fetchMoviesData()
    }

    fun fetchMoviesData() {
        viewModelScope.launch {
            when (val result = movieRepository.fetchMoviesData()) {
                is Result.Success -> {
                    _movieList.value = result.data.map { e ->
                        e.copy(
                            movieType = MovieType.admin,
                            interact = this@MovieManagementViewModel
                        )
                    }
                }

                is Result.Error -> {
                    // handle error here
                }
            }
        }
    }

    fun deleteMovie(id: String) {
        viewModelScope.launch {
            when (movieRepository.deleteMovie(id)) {
                is Result.Success -> {
                    _deleteMovieResponse.value = "Xoá thành công"
                    fetchMoviesData()
                }

                is Result.Error -> {
                    _deleteMovieResponse.value = "Có lỗi xảy ra, vui lòng thử lại!"
                }
            }
        }
    }

    fun addNewMovie(movieItemModel: MovieItemModel) {
        viewModelScope.launch {
            val listOfUploadedImages =
                uploadMultipleImages(listOfImages.map { e -> e.uri!! }.toList())
            val movie = movieItemModel.copy(
                posterUrl = listOfUploadedImages.firstOrNull(),
                images = listOfUploadedImages
            )
            movieRepository.addMovie(movie, onSuccess = {
                _addMovieResponse.value = null
            }, onError = {
                _addMovieResponse.value = it
            })
        }
    }

    fun sendFCM() {
        viewModelScope.launch(Dispatchers.IO) {
            fcmRepository.sendFcm(
                FcmMessageRequest(
                    "NEW_MOVIES",
                    "Phim mới",
                    "Vừa mới có mặt tại rạp, hãy đặt vé ngay!"
                )
            )
        }
    }

    fun updateMovie(movieItemModel: MovieItemModel) {
        viewModelScope.launch {
            val movie = movieItemModel.copy(
                id = _movieDetailResponse.value?.id,
                posterUrl = _movieDetailResponse.value?.posterUrl,
                images = _movieDetailResponse.value?.images
            )
            movieRepository.updateMovie(movie, onSuccess = {
                _updateMovieResponse.value = null
            }, onError = {
                _updateMovieResponse.value = it
            })
        }
    }

    private suspend fun uploadMultipleImages(imageUris: List<Uri>): List<String> =
        withContext(Dispatchers.IO) {
            val storage = FirebaseStorage.getInstance()
            val downloadUrls = mutableListOf<String>()

            for (uri in imageUris) {
                val fileName = "${UUID.randomUUID()}.jpg"
                val storageRef = storage.reference.child("images/$fileName")

                try {
                    // Upload the file to Firebase Storage
                    val uploadTask = storageRef.putFile(uri).await()
                    // Retrieve the download URL
                    val downloadUrl = storageRef.downloadUrl.await()
                    downloadUrls.add(downloadUrl.toString())
                } catch (e: Exception) {
                    // Handle any errors
                    e.printStackTrace()
                }
            }
            return@withContext downloadUrls
        }

    fun getDetailMovie(id: String) {
        viewModelScope.launch {
            when (val result = movieRepository.getDetailMovie(id)) {
                is Result.Success -> {
                    _movieDetailResponse.value = result.data
                }

                is Result.Error -> {
                    // handle error here
                }
            }
        }
    }

    override fun deleteMovieAction(value: String?) {
        _deleteMovieAction.value = value
    }

    override fun editMovieAction(value: String?) {
        _editMovieAction.value = value
    }
}