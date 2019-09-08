package com.ronnnnn.zeroconfsample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ronnnnn.zeroconfsample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            hostButton.setOnClickListener {
                startActivity(Intent(this@MainActivity, HostActivity::class.java))
            }
            guestButton.setOnClickListener {
                startActivity(Intent(this@MainActivity, GuestActivity::class.java))
            }
        }
    }
}