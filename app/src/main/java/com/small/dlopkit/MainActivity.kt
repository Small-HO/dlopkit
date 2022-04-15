package com.small.dlopkit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.small.floatkit.manager.PopDlopManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PopDlopManager.init(this)
    }
}