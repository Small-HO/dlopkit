package com.small.dlopkit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.small.floatkit.common.DlopConfig
import com.small.floatkit.manager.PopDlopManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PopDlopManager.init(this, DlopConfig().apply {
            http_dev = "http://www.baidu.com"
            http_main = "http://github.com"
        })

        findViewById<Button>(R.id.ceshi).setOnClickListener {
            Log.e("测试------->", PopDlopManager.getHttpUrl())
        }

    }
}