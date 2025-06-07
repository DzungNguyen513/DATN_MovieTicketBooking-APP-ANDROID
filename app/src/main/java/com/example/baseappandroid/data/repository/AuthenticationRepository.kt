package com.example.baseappandroid.data.repository

import android.util.Log
import com.example.baseappandroid.data.model.Result
import com.example.baseappandroid.data.model.User
import com.example.baseappandroid.di.IoDispatcher
import com.example.baseappandroid.utils.GlobalData
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.log

class AuthenticationRepository @Inject constructor(
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) {

    suspend fun registerUser(user: User): Result<User> =
        withContext(coroutineDispatcher) {
            try {
                val create = GlobalData.fireBaseAuth.createUserWithEmailAndPassword(
                    user.email.toString(),
                    user.password.toString()
                ).await()
                if (create.user != null) {
                    saveUserToDB(user.copy(id = create.user?.uid))
                    return@withContext Result.Success(data = user.copy(id = create.user?.uid))
                } else {
                    return@withContext Result.Error(errorMessage = GlobalData.errorOccursTryAgain)
                }
            } catch (ex: Exception) {
                return@withContext Result.Error(errorMessage = ex.message.toString())
            }
        }

    private fun saveUserToDB(user: User) {
        val userMap = hashMapOf(
            "id" to user.id,
            "email" to user.email,
            "fullName" to user.fullName,
            "mobilePhone" to user.mobilePhone,
            "role" to user.role,
            "image" to user.image
        )

        GlobalData.firebaseDB.collection("users")
            .document(user.id.toString())
            .set(userMap)
            .addOnSuccessListener {
                Log.d("Register", "User info saved to Firestore")
            }
            .addOnFailureListener { e ->
                Log.w("Register", "Error saving user info", e)
            }
    }

    suspend fun loginEmail(user: User): Result<User> = withContext(coroutineDispatcher) {
        try {
            val login = GlobalData.fireBaseAuth.signInWithEmailAndPassword(
                user.email.toString(),
                user.password.toString()
            ).await()
            if (login.user != null) {
                val loginUser =
                    (GlobalData.firebaseDB.collection("users").document(login.user!!.uid).get()
                        .await()).toObject(User::class.java)
                if (loginUser != null) {
                    GlobalData.user = loginUser
                    return@withContext Result.Success(data = loginUser)
                } else {
                    return@withContext Result.Error(errorMessage = "Có lỗi xảy ra vui lòng thử lại!")
                }

            } else {
                return@withContext Result.Error(errorMessage = "Sai thông tin đăng nhập!")
            }
        } catch (ex: Exception) {
            return@withContext Result.Error(errorMessage = ex.message.toString())
        }
    }

    suspend fun checkIfPasswordIsCorrect(email: String, password: String): Result<Boolean> =
        withContext(coroutineDispatcher) {
            val credential = EmailAuthProvider.getCredential(email, password)
            try {
                FirebaseAuth.getInstance().currentUser?.reauthenticate(credential)?.await()
                return@withContext Result.Success(data = true)
            } catch (ex: Exception) {
                return@withContext Result.Error(errorMessage = ex.message.toString())
            }
        }

    suspend fun changePassword(email: String, password: String): Result<String> =
        withContext(coroutineDispatcher) {
            try {
                FirebaseAuth.getInstance().currentUser?.updatePassword(password)?.await()
                return@withContext Result.Success(data = "Đổi mật khẩu thành công!")
            } catch (ex: Exception) {
                return@withContext Result.Error(errorMessage = "Đổi mật khẩu thất bại, vui lòng thử lại!")
            }
        }

    suspend fun getUserInformation(id: String): Result<User> = withContext(coroutineDispatcher) {
        try {
            val loginUser =
                (GlobalData.firebaseDB.collection("users").document(id).get()
                    .await()).toObject(User::class.java)
            if (loginUser != null) {
                GlobalData.user = loginUser
                return@withContext Result.Success(data = loginUser)
            } else {
                return@withContext Result.Error(errorMessage = "")
            }
        } catch (e: Exception) {
            return@withContext Result.Error(errorMessage = "")
        }
    }

    suspend fun changeProfile(phone: String, selectedImage: String?, id: String): Result<String> =
        withContext(coroutineDispatcher) {
            try {
                val hm = if (selectedImage != null) {
                    hashMapOf<String, Any>(
                        "mobilePhone" to phone,
                        "image" to selectedImage
                    )
                } else {
                    hashMapOf<String, Any>(
                        "mobilePhone" to phone,
                    )
                }
                GlobalData.firebaseDB.collection("users").document(id).update(hm).await()
                return@withContext Result.Success(data = id)
            } catch (ex: Exception) {
                return@withContext Result.Error(errorMessage = "Đổi thông tin thất bại, vui lòng thử lại!")
            }
        }
}