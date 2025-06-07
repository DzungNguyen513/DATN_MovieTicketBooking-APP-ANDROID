package com.example.baseappandroid.utils

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.example.baseappandroid.R
import com.example.baseappandroid.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object GlobalData {
    val fireBaseAuth = FirebaseAuth.getInstance()
    val firebaseDB = FirebaseFirestore.getInstance()
    const val errorOccursTryAgain = "Có lỗi xảy ra, vui lòng thử lại sau!"
    val dividerBackgroundColor = Color.parseColor("#DBD9D9")
    var user: User? = null
}

enum class BillDetailScreenType(val value: String) {
    AFTER_PAYMENT("AFTER_PAYMENT"),
    HISTORY("HISTORY")
}

enum class AdminToAddEditMovieScreenType(val value: String) {
    ADD("add_movie"),
    EDIT("edit_movie")
}

enum class AdminToAddEditSnackScreenType() {
    ADD,
    EDIT
}