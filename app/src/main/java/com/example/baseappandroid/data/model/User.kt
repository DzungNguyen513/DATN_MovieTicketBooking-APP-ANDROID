package com.example.baseappandroid.data.model

data class User(
    val id: String? = null,
    val fullName: String? = null,
    val mobilePhone: String? = null,
    val email: String? = null,
    var password: String? = null,
    val role: String? = null,
    val image: String? = null
)

enum class UserRole(val value: String) {
    USER("user"),
    ADMIN("admin")
}