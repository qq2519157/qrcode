package com.sencent.qrcode

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.sencent.qrcodelib.QRCodeView
import com.sencent.qrcodelib.ZXingView

/**
 *  Create by Logan at 2018/12/21 0021
 *
 */
class TestActivity : AppCompatActivity(), QRCodeView.Delegate {

    private var mZXingView: ZXingView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        mZXingView = findViewById(R.id.zxingview)
        mZXingView?.setDelegate(this)
    }

    override fun onStart() {
        super.onStart()
        mZXingView?.startCamera() // 打开后置摄像头开始预览，但是并未开始识别
        //        mZXingView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别
        mZXingView?.startSpotAndShowRect() // 显示扫描框，并开始识别
    }

    override fun onStop() {
        mZXingView?.stopCamera() // 关闭摄像头预览，并且隐藏扫描框
        super.onStop()
    }

    override fun onDestroy() {
        mZXingView?.onDestroy() // 销毁二维码扫描控件
        super.onDestroy()
    }

    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(200)
    }

    override fun onScanQRCodeSuccess(result: String?) {
        Log.i(TAG, "result:$result")
        Toast.makeText(this@TestActivity, "扫描结果为：$result", Toast.LENGTH_SHORT).show()
        vibrate()
        mZXingView?.startSpot() // 开始识别
    }

    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {
        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
        var tipText = mZXingView?.getScanBoxView()?.getTipText() ?: return
        val ambientBrightnessTip = "\n环境过暗，请打开闪光灯"
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                mZXingView?.getScanBoxView()?.setTipText(tipText + ambientBrightnessTip)
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip))
                mZXingView?.getScanBoxView()?.setTipText(tipText)
            }
        }
    }

    override fun onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错")
    }

    fun click(view: View) {
        when (view.id) {
            R.id.start -> {
                Toast.makeText(this@TestActivity, "开始扫码", Toast.LENGTH_SHORT).show()
                mZXingView?.startSpot()
            }
            else -> {
                Toast.makeText(this@TestActivity, "结束扫码", Toast.LENGTH_SHORT).show()
                mZXingView?.stopSpot()
            }
        }
    }

    companion object {
        private val TAG = TestActivity::class.java.simpleName
    }
}