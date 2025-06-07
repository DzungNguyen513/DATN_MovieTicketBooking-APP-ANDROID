package com.example.baseappandroid.ui.admin.snack

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseappandroid.base.viewmodel.BaseViewModel
import com.example.baseappandroid.data.model.Result
import com.example.baseappandroid.data.model.SnackItemModel
import com.example.baseappandroid.data.model.SnackItemType
import com.example.baseappandroid.data.repository.SnackRepository
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SnackManagementViewModel @Inject constructor(
    private val snackRepository: SnackRepository
) : BaseViewModel() {

    var selectedSnackImage: Uri? = null
    private var _listOfSnacks = MutableLiveData<List<SnackItemModel>>()
    val listOfSnacks: LiveData<List<SnackItemModel>>
        get() = _listOfSnacks

    private var _addSnackResponse = MutableLiveData<String?>()
    val addSnackResponse: LiveData<String?>
        get() = _addSnackResponse

    private var _editSnackResponse = MutableLiveData<String?>()
    val editSnackResponse: LiveData<String?>
        get() = _editSnackResponse

    private var _editSnackAction = MutableLiveData<String?>()
    val editSnackAction: LiveData<String?>
        get() = _editSnackAction

    fun clearEditSnackAction() {
        viewModelScope.launch {
            _editSnackAction.value = null
        }
    }

    private var _deleteSnackAction = MutableLiveData<String?>()
    val deleteSnackAction: LiveData<String?>
        get() = _deleteSnackAction

    fun clearDeleteSnackAction() {
        viewModelScope.launch {
            _deleteSnackAction.value = null
        }
    }

    private var _detailSnackResponse = MutableLiveData<SnackItemModel>()
    val detailSnackResponse: LiveData<SnackItemModel>
        get() = _detailSnackResponse

    fun fetchSnacks() {
        viewModelScope.launch {
            when (val result = snackRepository.fetchSnacks()) {
                is Result.Success -> {
                    _listOfSnacks.value =
                        result.data.map { e ->
                            e.copy(
                                interact = this@SnackManagementViewModel,
                                chosenCount = 0,
                                type = SnackItemType.ADMIN
                            )
                        }
                }

                is Result.Error -> {
                    Log.d("checkError", result.errorMessage)
                }
            }
        }
    }

    fun addSnack(model: SnackItemModel) {
        viewModelScope.launch {
            val data = model.copy(
                image = uploadImage(selectedSnackImage!!)
            )
            when (val result = snackRepository.addSnack(data)) {
                is Result.Success -> {
                    _addSnackResponse.value = "Thành công"
                }

                is Result.Error -> {
                    _addSnackResponse.value = result.errorMessage
                    Log.d("checkError", result.errorMessage)
                }
            }
        }
    }

    fun editSnack(model: SnackItemModel) {
        viewModelScope.launch {
            var data = model
            if (selectedSnackImage != null) {
                data = model.copy(
                    image = uploadImage(selectedSnackImage!!)
                )
            }
            when (val result = snackRepository.updateSnack(data)) {
                is Result.Success -> {
                    _editSnackResponse.value = ""
                }

                is Result.Error -> {
                    _editSnackResponse.value = result.errorMessage
                    Log.d("checkError", result.errorMessage)
                }
            }
        }
    }

    fun deleteSnackConfirm(id: String) {
        viewModelScope.launch {
            when (val result = snackRepository.deleteSnack(id)) {
                is Result.Success -> {
                    fetchSnacks()
                }

                is Result.Error -> {
                    Log.d("checkError", result.errorMessage)
                }
            }
        }
    }

    fun fetchDetailSnack(id: String) {
        viewModelScope.launch {
            when (val result = snackRepository.fetchDetailSnack(id)) {
                is Result.Success -> {
                    _detailSnackResponse.value =
                        result.data.copy(
                            interact = this@SnackManagementViewModel,
                            type = SnackItemType.ADMIN
                        )
                }

                is Result.Error -> {
                    Log.d("checkError", result.errorMessage)
                }
            }
        }
    }

    private suspend fun uploadImage(imageUri: Uri): String =
        withContext(Dispatchers.IO) {
            val storage = FirebaseStorage.getInstance()
            var downloadUrls = ""

            val fileName = "${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference.child("images/$fileName")

            try {
                // Upload the file to Firebase Storage
                val uploadTask = storageRef.putFile(imageUri).await()
                // Retrieve the download URL
                downloadUrls = (storageRef.downloadUrl.await()).toString()
            } catch (e: Exception) {
                // Handle any errors
                e.printStackTrace()
            }
            return@withContext downloadUrls
        }

    override fun editSnack(id: String) {
        viewModelScope.launch {
            _editSnackAction.value = id
        }
    }

    override fun deleteSnack(id: String) {
        viewModelScope.launch {
            _deleteSnackAction.value = id
        }
    }
}