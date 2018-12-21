package com.sencent.qrcode

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.view.View
import com.sencent.qrcodelib.IdentifyUtil

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        IdentifyUtil.setDebug(true)
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_PERMISSION_CAMERA
                )
            }
        }
    }

    fun scan(view: View) {
        val optionsCompat = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.act_in, R.anim.act_out)
        when (view.id) {
            R.id.btn1 -> {
                ActivityCompat.startActivity(
                    this@MainActivity,
                    Intent(this@MainActivity, TestActivity::class.java),
                    optionsCompat.toBundle()
                )
            }
            else -> {
                ActivityCompat.startActivity(
                    this@MainActivity,
                    Intent(this@MainActivity, TestFragmentActivity::class.java),
                    optionsCompat.toBundle()
                )
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // 未获得Camera权限
                AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("请在系统设置中为App开启摄像头权限后重试")
                    .setPositiveButton("确定") { dialog, which -> this.finish() }
                    .show()
            }
        }
    }

    companion object {
        private val REQUEST_PERMISSION_CAMERA = 1000
    }
}
