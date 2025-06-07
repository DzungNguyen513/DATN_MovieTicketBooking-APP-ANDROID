package com.example.baseappandroid.base.recyclerview

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager

class HorizontalScrollingGridLayoutManager(
    context: Context,
    spanColumn: Int
) : GridLayoutManager(context, spanColumn) {

    override fun canScrollHorizontally(): Boolean {
        return true
    }

    override fun canScrollVertically(): Boolean {
        return false
    }
}