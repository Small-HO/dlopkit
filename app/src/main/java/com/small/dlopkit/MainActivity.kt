package com.small.dlopkit

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.small.camerakit.Matisse
import com.small.camerakit.MimeType
import com.small.camerakit.engine.impl.GlideEngine
import com.small.camerakit.internal.entity.CaptureStrategy
import com.small.editorkit.RichEditor
import com.small.floatkit.common.DlopConfig
import com.small.floatkit.manager.PopDlopManager
import com.small.videokit.FFmpegNativeUtils.startVideo
import com.small.videokit.FFmpegNativeUtils.videoPlay

class MainActivity : AppCompatActivity() {
    
    private val viewModel by lazy { ViewModelProvider(this)[MainViewModel::class.java] }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnUi).setOnClickListener {
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
            startActivity(Intent(this, VideoActivity::class.java))
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
            XXPermissions.with(this)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .permission(Manifest.permission.CAMERA)
                .request { _, allGranted ->
                    if (allGranted) {
                        Matisse.from(this)
                            .choose(MimeType.ofImage())           // 图片类型
                            .countable(true)           // true:选中后显示数字;false:选中后显示对号
                            .maxSelectable(1)      // 可选的最大数
                            .capture(false)              // 选择照片时，是否显示拍照
                            .captureStrategy(CaptureStrategy(true, "com.xbkaoyan.ikaoyaner.provider")) //参数1 true表示拍照存储在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
                            .imageEngine(GlideEngine()) //图片加载引擎
                            .forResult(2) //
                    }
                }
        }
    }

}