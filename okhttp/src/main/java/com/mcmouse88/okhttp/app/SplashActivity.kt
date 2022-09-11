package com.mcmouse88.okhttp.app

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mcmouse88.okhttp.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Singletons.init(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }
}