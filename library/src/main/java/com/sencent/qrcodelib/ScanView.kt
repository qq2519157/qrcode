package com.sencent.qrcodelib

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 *  Create by Logan at 2018/12/21 0021
 *
 */
class ScanView(context: Context,attrs: AttributeSet?) : ZXingView(context,attrs) {

    override fun startCamera() {
        super.startCamera()
        if (childCount == 0) {
            addView(mCameraPreview)
            addView(mScanBoxView)
            showScanRect()
        }else{
            if (mCameraPreview.visibility== View.GONE) {
                mCameraPreview.visibility=View.VISIBLE
            }
            if (mScanBoxView?.visibility== View.GONE) {
                mScanBoxView?.visibility=View.VISIBLE
            }
        }
    }

    override fun stopCamera() {
        super.stopCamera()
        if (mCameraPreview.visibility== View.VISIBLE) {
            mCameraPreview.visibility=View.GONE
        }
        if (mScanBoxView?.visibility== View.VISIBLE) {
            mScanBoxView?.visibility=View.GONE
        }
//        removeAllViews()
    }

}