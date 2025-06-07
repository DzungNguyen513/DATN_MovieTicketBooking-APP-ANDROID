package com.example.baseappandroid.base.ui

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

class OneByOnePagerSnapHelper : PagerSnapHelper() {
    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager,
        velocityX: Int,
        velocityY: Int
    ): Int {
        if (layoutManager !is LinearLayoutManager) {
            return RecyclerView.NO_POSITION
        }
        val currentPosition = layoutManager.findFirstVisibleItemPosition()
        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION
        }
        // Nếu lướt sang bên phải, chuyển sang item tiếp theo, ngược lại chuyển về item trước đó
        return when {
            velocityX > 0 -> currentPosition + 1
            velocityX < 0 -> currentPosition - 1
            else -> currentPosition
        }.coerceIn(0, layoutManager.itemCount - 1)
    }
}
