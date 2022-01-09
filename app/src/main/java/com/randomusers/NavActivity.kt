package com.randomusers

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.randomusers.databinding.ActivityMainBinding

class NavActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var mContext: Context
    lateinit var mActivity: AppCompatActivity
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this
        mActivity = this
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor =
            ContextCompat.getColor(this, R.color.white)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content) as NavHostFragment
        navController = navHostFragment.navController
        navController.navigate(R.id.nav_splash, Bundle())
        Handler(Looper.getMainLooper()).postDelayed({
            navController.navigate(R.id.action_nav_splash_to_nav_users_list, Bundle())
        }, 3000)
    }
}