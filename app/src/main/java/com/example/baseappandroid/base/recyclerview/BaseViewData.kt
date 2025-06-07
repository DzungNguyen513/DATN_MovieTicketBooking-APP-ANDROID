package com.example.baseappandroid.base.recyclerview

import androidx.recyclerview.widget.DiffUtil

interface BaseViewData {
    val layoutRes: Int

    fun areItemsTheSame(newItem: BaseViewData): Boolean

    fun areContentsTheSame(newItem: BaseViewData): Boolean

    object HomeDiffUtil : DiffUtil.ItemCallback<BaseViewData>() {
        override fun areItemsTheSame(oldItem: BaseViewData, newItem: BaseViewData): Boolean {
            return oldItem.areItemsTheSame(newItem)
        }

        override fun areContentsTheSame(oldItem: BaseViewData, newItem: BaseViewData): Boolean {
            return oldItem.areContentsTheSame(newItem)
        }
    }
}