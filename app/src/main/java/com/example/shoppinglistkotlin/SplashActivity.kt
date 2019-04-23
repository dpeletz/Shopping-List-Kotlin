package com.example.shoppinglistkotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Handler

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val secondsDelayed = 3
        startActivityOnDelay(secondsDelayed)
    }

    private fun startActivityOnDelay(secondsDelayed: Int) {
        Handler().postDelayed(Runnable {
            startActivity(Intent(this@SplashActivity, ScrollingActivity::class.java))
            finish()
        }, secondsDelayed.toLong() * 1000)
    }
}