package com.example.baseappandroid.base.recyclerview

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class VerticalScrollingLinearLayoutManager(
    context: Context
) : LinearLayoutManager(context) {
    override fun canScrollHorizontally(): Boolean {
        return false
    }

    override fun canScrollVertically(): Boolean {
        return false
    }
}