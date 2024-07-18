package com.small.dlopkit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.small.editorkit.RichEditor
import com.small.floatkit.common.DlopConfig
import com.small.floatkit.manager.PopDlopManager
import com.small.uikit.ui.view.UiTextView

class MainActivity : AppCompatActivity() {
    
    private val viewModel by lazy { ViewModelProvider(this)[MainViewModel::class.java] }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<UiTextView>(R.id.tvCeshi).setOnClickListener {

        }

        findViewById<Button>(R.id.btnUi).setOnClickListener {
        }

        findViewById<Button>(R.id.btnMulti).setOnClickListener {
//            startActivity(Intent(this,UiHelperActivity::class.java))
        }

        findViewById<Button>(R.id.btnMulti).setOnClickListener {
//            startActivity(Intent(this,MultiActivity::class.java))
        }

        PopDlopManager.init(this, DlopConfig().apply {
            http_dev = "http://www.baidu.com"
            http_main = "http://github.com"
        })

        findViewById<Button>(R.id.ceshi).setOnClickListener {
            Log.e("测试------->", PopDlopManager.getHttpUrl())
        }

        findViewById<Button>(R.id.btnVideo).setOnClickListener {
        }


        val play = findViewById<Button>(R.id.bt_play)
        play.setOnClickListener {
            Thread {
//                startVideo("http://cdn.ikaoyaner.com/xbky/course/video/2021-12-03/选择题押题1.mp4", findViewById<SurfaceView>(R.id.sfv_player).holder.surface)
//                videoPlay("rtmp://mobliestream.c3tv.com:554/live/goodtv.sdp", findViewById<SurfaceView>(R.id.sfv_player).holder.surface)
            }.start()
        }


        val diff = findViewById<Button>(R.id.bt_patch)
        diff.setOnClickListener {
            viewModel.fileDiff()
        }

        val update = findViewById<Button>(R.id.bt_update)
        update.setOnClickListener {
            viewModel.update()
        }

        findViewById<Button>(R.id.bt_bold).setOnClickListener {
            findViewById<RichEditor>(R.id.richEditor).setBold()
        }

        findViewById<Button>(R.id.btCar).setOnClickListener {

        }
    }

}