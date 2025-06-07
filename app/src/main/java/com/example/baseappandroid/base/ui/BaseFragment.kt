package com.example.baseappandroid.base.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<BINDING : ViewDataBinding> : Fragment() {
    private var _binding: BINDING? = null
    val binding: BINDING
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, getContentLayout(), container, false)
        return binding.root
    }

    abstract fun getContentLayout(): Int
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("CheckHere", "Yeah it is cleared")
    }

}