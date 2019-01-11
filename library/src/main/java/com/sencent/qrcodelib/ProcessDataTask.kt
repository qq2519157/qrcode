package com.sencent.qrcodelib

import android.graphics.Bitmap
import android.hardware.Camera
import android.os.AsyncTask
import android.text.TextUtils
import java.lang.ref.WeakReference

/**
 *  Create by Logan at 2018/12/21 0021
 *
 */
class ProcessDataTask() : AsyncTask<Void, Void, ScanResult?>() {


    private var mCamera: Camera? = null
    private var mData: ByteArray? = null
    private var mIsPortrait: Boolean = false
    private var mPicturePath: String? = null
    private var mBitmap: Bitmap? = null
    private var mQRCodeViewRef: WeakReference<QRCodeView>? = null
    private var sLastStartTime: Long = 0

    constructor(camera: Camera, data: ByteArray?, qrCodeView: QRCodeView, isPortrait: Boolean):this() {
        mCamera = camera
        mData = data
        mQRCodeViewRef = WeakReference(qrCodeView)
        mIsPortrait = isPortrait
    }

    constructor(picturePath: String, qrCodeView: QRCodeView):this() {
        mPicturePath = picturePath
        mQRCodeViewRef = WeakReference(qrCodeView)
    }

    constructor(bitmap:Bitmap,qrCodeView: QRCodeView):this(){
        mBitmap = bitmap
        mQRCodeViewRef = WeakReference(qrCodeView)
    }

    fun perform(): ProcessDataTask {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        return this
    }

    fun cancelTask() {
        if (status != AsyncTask.Status.FINISHED) {
            cancel(true)
        }
    }

    override fun onCancelled() {
        super.onCancelled()
        mQRCodeViewRef?.clear()
        mBitmap = null
        mData = null
    }

    private fun processData(qrCodeView: QRCodeView): ScanResult? {
        if (mData == null) {
            return null
        }
        val camera =mCamera?: return null
        var width = 0
        var height = 0
        var data = mData
        try {
            val parameters = camera.parameters
            val size = parameters.previewSize
            width = size.width
            height = size.height

            if (mIsPortrait) {
                data = ByteArray(mData!!.size)
                for (y in 0 until height) {
                    for (x in 0 until width) {
                        data[x * height + height - y - 1] = mData!![x + y * width]
                    }
                }
                val tmp = width
                width = height
                height = tmp
            }

            return qrCodeView.processData(data, width, height, false)
        } catch (e1: Exception) {
            e1.printStackTrace()
            return try {
                if (width != 0 && height != 0) {
                    IdentifyUtil.d("识别失败重试")
                    qrCodeView.processData(data, width, height, true)
                } else {
                    null
                }
            } catch (e2: Exception) {
                e2.printStackTrace()
                null
            }

        }

    }

    override fun doInBackground(vararg params: Void?): ScanResult? {
        val qrCodeView = mQRCodeViewRef?.get() ?: return null

        when {
            mPicturePath != null -> return qrCodeView.processBitmapData(IdentifyUtil.getDecodeAbleBitmap(mPicturePath))
            mBitmap != null -> {
                val result = qrCodeView.processBitmapData(mBitmap)
                mBitmap = null
                return result
            }
            else -> {
                if (IdentifyUtil.isDebug()) {
                    IdentifyUtil.d("两次任务执行的时间间隔：" + (System.currentTimeMillis() - sLastStartTime))
                    sLastStartTime = System.currentTimeMillis()
                }
                val startTime = System.currentTimeMillis()

                val scanResult = processData(qrCodeView)

                if (IdentifyUtil.isDebug()) {
                    val time = System.currentTimeMillis() - startTime
                    if (scanResult != null && !TextUtils.isEmpty(scanResult.result)) {
                        IdentifyUtil.d("识别成功时间为：$time")
                    } else {
                        IdentifyUtil.e("识别失败时间为：$time")
                    }
                }

                return scanResult
            }
        }
    }

    override fun onPostExecute(result: ScanResult?) {
        val qrCodeView = mQRCodeViewRef?.get() ?: return

        if (mPicturePath != null || mBitmap != null) {
            mBitmap = null
            qrCodeView.onPostParseBitmapOrPicture(result)
        } else {
            qrCodeView.onPostParseData(result)
        }
    }

}