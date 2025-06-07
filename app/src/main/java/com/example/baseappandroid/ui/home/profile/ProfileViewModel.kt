package com.example.baseappandroid.ui.home.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseappandroid.base.viewmodel.BaseViewModel
import com.example.baseappandroid.data.model.Result
import com.example.baseappandroid.data.model.User
import com.example.baseappandroid.data.repository.AuthenticationRepository
import com.example.travelappadmin.common.Event
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : BaseViewModel() {
    var selectedImage: Uri? = null
    private var _checkPasswordResponse = MutableLiveData<Boolean>()
    val checkPasswordResponse: LiveData<Boolean>
        get() = _checkPasswordResponse

    private var _changePasswordResponse = MutableLiveData<String>()
    val changePasswordResponse: LiveData<String>
        get() = _changePasswordResponse

    private var _changeInfoResponse = MutableLiveData<String>()
    val changeInfoResponse: LiveData<String>
        get() = _changeInfoResponse

    private var _getInfoResponse = MutableLiveData<Event<User>>()
    val getInfoResponse: LiveData<Event<User>>
        get() = _getInfoResponse

    fun checkPassword(email: String, password: String) {
        viewModelScope.launch {
            val check = authenticationRepository.checkIfPasswordIsCorrect(email, password)
            _checkPasswordResponse.value = check is Result.Success
        }
    }

    fun changeInformation(phoneNumber: String, id: String) {
        viewModelScope.launch {
            viewModelScope.launch {
                var image: String? = null
                if (selectedImage != null) {
                    image = uploadImage(selectedImage!!)
                    if (image.isEmpty()) image = null
                }
                when (val result = authenticationRepository.changeProfile(phoneNumber, image, id)) {
                    is Result.Success -> {
                        _changeInfoResponse.value = result.data
                        when (val result1 =
                            authenticationRepository.getUserInformation(result.data)) {
                            is Result.Success -> {
                                _getInfoResponse.value = Event(result1.data)
                            }

                            is Result.Error -> {

                            }
                        }
                    }

                    is Result.Error -> {
                        _changeInfoResponse.value = result.errorMessage
                    }
                }
            }
        }
    }


    fun changePassword(email: String, password: String) {
        viewModelScope.launch {
            when (val result = authenticationRepository.changePassword(email, password)) {
                is Result.Success -> {
                    _changePasswordResponse.value = result.data
                }

                is Result.Error -> {
                    _changePasswordResponse.value = result.errorMessage
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
}