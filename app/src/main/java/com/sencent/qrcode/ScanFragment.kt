package com.sencent.qrcode

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.sencent.qrcodelib.QRCodeView
import com.sencent.qrcodelib.ScanView
import com.sencent.qrcodelib.ZXingView

/**
 *  Create by Logan at 2018/12/21 0021
 *
 */
class ScanFragment : Fragment(), QRCodeView.Delegate {


    private var mRootView: View? = null
    private var mZXingView: ScanView? = null
    private var mFlash: ImageView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(R.layout.fragmnet_scan, container, false)
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mZXingView=mRootView?.findViewById(R.id.zxingview)
        mFlash=mRootView?.findViewById(R.id.flash)
        mZXingView?.setDelegate(this)
        mZXingView?.startCamera() // 打开后置摄像头开始预览，但是并未开始识别
        //        mZXingView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别
        mZXingView?.startSpotAndShowRect() // 显示扫描框，并开始识别
        mFlash?.setOnClickListener {
            if (it.isSelected) {
                mFlash?.setImageResource(R.drawable.flash_off)
                mZXingView?.closeFlashlight()
                mFlash?.isSelected=false
            }else{
                mFlash?.setImageResource(R.drawable.flash_on)
                mZXingView?.openFlashlight()
                mFlash?.isSelected=true
            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            // 打开后置摄像头开始预览，但是并未开始识别
            mZXingView?.resumeCamera()
            Log.i(TAG, "onResume")
        } else {
           mZXingView?.pauseCamera()
            Log.i(TAG, "onPause")
        }
    }


    override fun onDestroy() {
        mZXingView?.onDestroy() // 销毁二维码扫描控件
        mZXingView?.stopSpot()
        super.onDestroy()
    }

    override fun onScanQRCodeSuccess(result: String?) {
        Log.i(TAG, "result:$result")
        Toast.makeText(activity!!, "扫描结果为：$result", Toast.LENGTH_SHORT).show()
        vibrate()

        mZXingView?.startSpot() // 开始识别
    }

    private fun vibrate() {
        val vibrator = activity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(200)
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

    companion object {
        private val TAG = ScanFragment::class.java.simpleName
    }

}