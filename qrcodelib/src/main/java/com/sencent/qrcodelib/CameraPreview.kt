package com.sencent.qrcodelib

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Rect
import android.hardware.Camera
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View

/**
 *  Create by Logan at 2018/12/21 0021
 *
 */
class CameraPreview(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    private var mCamera: Camera? = null
    private var mPreviewing = true
    private var mSurfaceCreated = false
    private var mIsTouchFocusing = false
    private var mOldDist = 1f
    private var mCameraConfigurationManager: CameraConfigurationManager? = null
    private var mDelegate: Delegate? = null


    fun setCamera(camera: Camera?) {
        mCamera = camera
        if (mCamera != null) {
            mCameraConfigurationManager = CameraConfigurationManager(context)
            mCameraConfigurationManager!!.initFromCameraParameters(mCamera!!)

            holder.addCallback(this)
            if (mPreviewing) {
                requestLayout()
            } else {
                showCameraPreview()
            }
        }
    }

    internal fun setDelegate(delegate: Delegate) {
        mDelegate = delegate
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        mSurfaceCreated = true
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (surfaceHolder.surface == null) {
            return
        }
        stopCameraPreview()
        showCameraPreview()
    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
        mSurfaceCreated = false
        stopCameraPreview()
    }

    fun reactNativeShowCameraPreview() {
        if (holder == null || holder.surface == null) {
            return
        }

        stopCameraPreview()
        showCameraPreview()
    }

    private fun showCameraPreview() {
        val camera = mCamera ?: return
        try {
            mPreviewing = true
            val surfaceHolder = holder
            surfaceHolder.setKeepScreenOn(true)
            camera.setPreviewDisplay(surfaceHolder)

            mCameraConfigurationManager?.setDesiredCameraParameters(camera)
            camera.startPreview()
            if (mDelegate != null) {
                mDelegate?.onStartPreview()
            }
            startContinuousAutoFocus()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopCameraPreview() {
        val camera = mCamera ?: return
        try {
            mPreviewing = false
            camera.cancelAutoFocus()
            camera.setOneShotPreviewCallback(null)
            camera.stopPreview()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun openFlashlight() {
        val camera = mCamera ?: return
        if (flashLightAvailable()) {
            mCameraConfigurationManager?.openFlashlight(camera)
        }
    }

    fun closeFlashlight() {
        val camera = mCamera ?: return
        if (flashLightAvailable()) {
            mCameraConfigurationManager?.closeFlashlight(camera)
        }
    }

    private fun flashLightAvailable(): Boolean {
        return isPreviewing() && context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    internal fun onScanBoxRectChanged(scanRect: Rect?) {
        var scanRect = scanRect
        if (mCamera == null || scanRect == null || scanRect.left <= 0 || scanRect.top <= 0) {
            return
        }
        var centerX = scanRect.centerX()
        var centerY = scanRect.centerY()
        var rectHalfWidth = scanRect.width() / 2
        var rectHalfHeight = scanRect.height() / 2

        IdentifyUtil.printRect("转换前", scanRect)

        if (IdentifyUtil.isPortrait(context)) {
            var temp = centerX
            centerX = centerY
            centerY = temp

            temp = rectHalfWidth
            rectHalfWidth = rectHalfHeight
            rectHalfHeight = temp
        }
        scanRect = Rect(
            centerX - rectHalfWidth,
            centerY - rectHalfHeight,
            centerX + rectHalfWidth,
            centerY + rectHalfHeight
        )
        IdentifyUtil.printRect("转换后", scanRect)

        IdentifyUtil.d("扫码框发生变化触发对焦测光")
        handleFocusMetering(
            scanRect.centerX().toFloat(),
            scanRect.centerY().toFloat(),
            scanRect.width(),
            scanRect.height()
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isPreviewing()) {
            return super.onTouchEvent(event)
        }

        if (event.pointerCount == 1 && event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
            if (mIsTouchFocusing) {
                return true
            }
            mIsTouchFocusing = true
            IdentifyUtil.d("手指触摸触发对焦测光")
            var centerX = event.x
            var centerY = event.y
            if (IdentifyUtil.isPortrait(context)) {
                val temp = centerX
                centerX = centerY
                centerY = temp
            }
            val focusSize = IdentifyUtil.dp2px(context, 120f)
            handleFocusMetering(centerX, centerY, focusSize, focusSize)
        }

        if (event.pointerCount == 2) {
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_POINTER_DOWN -> mOldDist = IdentifyUtil.calculateFingerSpacing(event)
                MotionEvent.ACTION_MOVE -> {
                    val newDist = IdentifyUtil.calculateFingerSpacing(event)
                    if (newDist > mOldDist) {
                        handleZoom(true, mCamera ?: return true)
                    } else if (newDist < mOldDist) {
                        handleZoom(false, mCamera ?: return true)
                    }
                }
            }
        }
        return true
    }

    private fun handleZoom(isZoomIn: Boolean, camera: Camera) {
        val params = camera.parameters
        if (params.isZoomSupported) {
            var zoom = params.zoom
            if (isZoomIn && zoom < params.maxZoom) {
                IdentifyUtil.d("放大")
                zoom++
            } else if (!isZoomIn && zoom > 0) {
                IdentifyUtil.d("缩小")
                zoom--
            } else {
                IdentifyUtil.d("既不放大也不缩小")
            }
            params.zoom = zoom
            camera.parameters = params
        } else {
            IdentifyUtil.d("不支持缩放")
        }
    }

    private fun handleFocusMetering(
        originFocusCenterX: Float, originFocusCenterY: Float,
        originFocusWidth: Int, originFocusHeight: Int
    ) {
        val camera = mCamera ?: return
        try {
            var isNeedUpdate = false
            val focusMeteringParameters = camera.parameters
            val size = focusMeteringParameters.previewSize
            if (focusMeteringParameters.maxNumFocusAreas > 0) {
                IdentifyUtil.d("支持设置对焦区域")
                isNeedUpdate = true
                val focusRect = IdentifyUtil.calculateFocusMeteringArea(
                    1f,
                    originFocusCenterX, originFocusCenterY,
                    originFocusWidth, originFocusHeight,
                    size.width, size.height
                )
                IdentifyUtil.printRect("对焦区域", focusRect)
                focusMeteringParameters.focusAreas = listOf(Camera.Area(focusRect, 1000))
                focusMeteringParameters.focusMode = Camera.Parameters.FOCUS_MODE_MACRO
            } else {
                IdentifyUtil.d("不支持设置对焦区域")
            }

            if (focusMeteringParameters.maxNumMeteringAreas > 0) {
                IdentifyUtil.d("支持设置测光区域")
                isNeedUpdate = true
                val meteringRect = IdentifyUtil.calculateFocusMeteringArea(
                    1.5f,
                    originFocusCenterX, originFocusCenterY,
                    originFocusWidth, originFocusHeight,
                    size.width, size.height
                )
                IdentifyUtil.printRect("测光区域", meteringRect)
                focusMeteringParameters.meteringAreas = listOf(Camera.Area(meteringRect, 1000))
            } else {
                IdentifyUtil.d("不支持设置测光区域")
            }

            if (isNeedUpdate) {
                camera.cancelAutoFocus()
                camera.parameters = focusMeteringParameters
                camera.autoFocus { success, _ ->
                    if (success) {
                        IdentifyUtil.d("对焦测光成功")
                    } else {
                        IdentifyUtil.e("对焦测光失败")
                    }
                    startContinuousAutoFocus()
                }
            } else {
                mIsTouchFocusing = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            IdentifyUtil.e("对焦测光失败：" + e.message)
            startContinuousAutoFocus()
        }

    }

    /**
     * 连续对焦
     */
    private fun startContinuousAutoFocus() {
        mIsTouchFocusing = false
        val camera = mCamera ?: return
        try {
            val parameters = camera.parameters
            // 连续对焦
            parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
            camera.parameters = parameters
            // 要实现连续的自动对焦，这一句必须加上
            camera.cancelAutoFocus()
        } catch (e: Exception) {
            IdentifyUtil.e("连续对焦失败")
        }

    }

    fun isPreviewing(): Boolean {
        return mCamera != null && mPreviewing && mSurfaceCreated
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        val cameraConfigurationManager = mCameraConfigurationManager ?: return
        var width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        var height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        if (cameraConfigurationManager.getCameraResolution() != null) {
            val cameraResolution = cameraConfigurationManager.getCameraResolution() ?: return
            // 取出来的cameraResolution高宽值与屏幕的高宽顺序是相反的
            val cameraPreviewWidth = cameraResolution.x
            val cameraPreviewHeight = cameraResolution.y
            if (width * 1f / height < cameraPreviewWidth * 1f / cameraPreviewHeight) {
                val ratio = cameraPreviewHeight * 1f / cameraPreviewWidth
                width = (height / ratio + 0.5f).toInt()
            } else {
                val ratio = cameraPreviewWidth * 1f / cameraPreviewHeight
                height = (width / ratio + 0.5f).toInt()
            }
        }
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
    }

    interface Delegate {
        fun onStartPreview()
    }
}