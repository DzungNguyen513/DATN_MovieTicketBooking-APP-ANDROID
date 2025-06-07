package com.example.baseappandroid.ui.register

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baseappandroid.data.model.Result
import com.example.baseappandroid.data.model.User
import com.example.baseappandroid.data.repository.AuthenticationRepository
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {
    var selectedImage: Uri? = null
    private var _registerUserResponse: MutableLiveData<String?> = MutableLiveData()
    val registerUserResponse: LiveData<String?>
        get() = _registerUserResponse

    fun registerUser(user: User) {
        viewModelScope.launch {
            var data = user
            if (selectedImage != null) {
                data = user.copy(image = uploadImage(selectedImage!!))
            }
            when (val result = authenticationRepository.registerUser(data)) {
                is Result.Success -> _registerUserResponse.value = null
                is Result.Error -> _registerUserResponse.value = result.errorMessage
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
                if (downloadUrls.isEmpty()) {
                    //default image
                    downloadUrls =
                        "https://firebasestorage.googleapis.com/v0/b/my-demo-project-dfedb.appspot.com/o/istockphoto-1427966302-612x612.jpg?alt=media&token=f5add684-04b7-49d0-9c62-ac2fe4de0682"
                }
            } catch (e: Exception) {
                // Handle any errors
                e.printStackTrace()
            }
            return@withContext downloadUrls
        }
}