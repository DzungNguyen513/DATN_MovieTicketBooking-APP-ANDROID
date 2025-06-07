package com.example.baseappandroid.ui.login

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baseappandroid.data.model.Result
import com.example.baseappandroid.data.model.User
import com.example.baseappandroid.data.model.UserRole
import com.example.baseappandroid.data.repository.AuthenticationRepository
import com.example.baseappandroid.utils.GlobalData
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
) : ViewModel() {
    var isHidePassword = true

    private var _loginResponse = MutableLiveData<User?>()
    val loginResponse: LiveData<User?>
        get() = _loginResponse

    private var _errorResponse = MutableLiveData<String?>()
    val errorResponse: LiveData<String?>
        get() = _errorResponse
    fun checkAutoLogin() {
        viewModelScope.launch {
            val data = sharedPreferences.getString("remember_user", null)
            if (data != null) {
                val user =
                    gson.fromJson(data, User::class.java)
                if (user.id != null && user.role == UserRole.USER.value) {
                    GlobalData.user = user
                    _loginResponse.value = user
                }
            }

        }
    }

    fun login(user: User) {
        viewModelScope.launch {
            when (val result = authenticationRepository.loginEmail(user)) {
                is Result.Success -> {
                    _loginResponse.value = result.data
                    if (result.data.role == UserRole.USER.value) {
                        sharedPreferences.edit()
                            .putString("remember_user", gson.toJson(result.data))
                            .apply()
                    }
                }

                is Result.Error -> {
                    _errorResponse.value = result.errorMessage
                }
            }
        }
    }
}