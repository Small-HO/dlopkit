package com.small.dlopkit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.widget.Button
import com.small.floatkit.common.DlopConfig
import com.small.floatkit.manager.PopDlopManager
import com.small.videokit.FFmpegNativeUtils.videoPlay

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


        val play = findViewById<Button>(R.id.bt_play)
        play.setOnClickListener {
            Thread {
//                videoPlay("http://cdn.ikaoyaner.com/xbky/course/video/2021-12-03/选择题押题1.mp4", findViewById<SurfaceView>(R.id.sfv_player).holder.surface)
                videoPlay("rtmp://mobliestream.c3tv.com:554/live/goodtv.sdp", findViewById<SurfaceView>(R.id.sfv_player).holder.surface)
            }.start()
        }
    }
}