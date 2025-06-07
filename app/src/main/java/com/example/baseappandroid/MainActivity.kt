package com.example.baseappandroid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.baseappandroid.data.api.AppInfo
import com.example.baseappandroid.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPaySDK


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_all) as NavHostFragment
        navController = navHostFragment.findNavController()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        ZaloPaySDK.init(AppInfo.APP_ID, Environment.SANDBOX)
        ZaloPaySDK.getInstance().onResult(intent)
    }
}