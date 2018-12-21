package com.sencent.qrcodelib

import android.content.Context
import android.graphics.Point
import android.hardware.Camera
import android.view.Surface
import android.view.WindowManager

/**
 *  Create by Logan at 2018/12/21 0021
 *
 */
class CameraConfigurationManager(private val mContext: Context) {
    private var mCameraResolution: Point? = null
    private var mPreviewResolution: Point? = null

    fun initFromCameraParameters(camera: Camera) {
        val screenResolution = IdentifyUtil.getScreenResolution(mContext)
        val screenResolutionForCamera = Point()
        screenResolutionForCamera.x = screenResolution.x
        screenResolutionForCamera.y = screenResolution.y

        if (IdentifyUtil.isPortrait(mContext)) {
            screenResolutionForCamera.x = screenResolution.y
            screenResolutionForCamera.y = screenResolution.x
        }

        mPreviewResolution = getPreviewResolution(camera.parameters, screenResolutionForCamera)

        mCameraResolution = if (IdentifyUtil.isPortrait(mContext)) {
            Point(mPreviewResolution!!.y, mPreviewResolution!!.x)
        } else {
            mPreviewResolution
        }
    }

    private fun autoFocusAble(camera: Camera): Boolean {
        val supportedFocusModes = camera.parameters.supportedFocusModes
        val focusMode = findSettableValue(supportedFocusModes, Camera.Parameters.FOCUS_MODE_AUTO)
        return focusMode != null
    }

    fun getCameraResolution(): Point? {
        return mCameraResolution
    }

    fun setDesiredCameraParameters(camera: Camera) {
        val previewResolution=mPreviewResolution?:return
        val parameters = camera.parameters
        parameters.setPreviewSize(previewResolution.x, previewResolution.y)

        // https://github.com/googlesamples/android-vision/blob/master/visionSamples/barcode-reader/app/src/main/java/com/google/android/gms/samples/vision/barcodereader/ui/camera/CameraSource.java
        val previewFpsRange = selectPreviewFpsRange(camera, 60.0f)
        if (previewFpsRange != null) {
            parameters.setPreviewFpsRange(
                previewFpsRange!![Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
                previewFpsRange!![Camera.Parameters.PREVIEW_FPS_MAX_INDEX]
            )
        }

        camera.setDisplayOrientation(getDisplayOrientation())
        camera.parameters = parameters
    }

    /**
     * Selects the most suitable preview frames per second range, given the desired frames per
     * second.
     *
     * @param camera            the camera to select a frames per second range from
     * @param desiredPreviewFps the desired frames per second for the camera preview frames
     * @return the selected preview frames per second range
     */
    private fun selectPreviewFpsRange(camera: Camera, desiredPreviewFps: Float): IntArray? {
        // The camera API uses integers scaled by a factor of 1000 instead of floating-point frame
        // rates.
        val desiredPreviewFpsScaled = (desiredPreviewFps * 1000.0f).toInt()

        // The method for selecting the best range is to minimize the sum of the differences between
        // the desired value and the upper and lower bounds of the range.  This may select a range
        // that the desired value is outside of, but this is often preferred.  For example, if the
        // desired frame rate is 29.97, the range (30, 30) is probably more desirable than the
        // range (15, 30).
        var selectedFpsRange: IntArray? = null
        var minDiff = Integer.MAX_VALUE
        val previewFpsRangeList = camera.parameters.supportedPreviewFpsRange
        for (range in previewFpsRangeList) {
            val deltaMin = desiredPreviewFpsScaled - range[Camera.Parameters.PREVIEW_FPS_MIN_INDEX]
            val deltaMax = desiredPreviewFpsScaled - range[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]
            val diff = Math.abs(deltaMin) + Math.abs(deltaMax)
            if (diff < minDiff) {
                selectedFpsRange = range
                minDiff = diff
            }
        }
        return selectedFpsRange
    }

    fun openFlashlight(camera: Camera) {
        doSetTorch(camera, true)
    }

    fun closeFlashlight(camera: Camera) {
        doSetTorch(camera, false)
    }

    private fun doSetTorch(camera: Camera, newSetting: Boolean) {
        val parameters = camera.parameters
        val flashMode: String?
        /** 是否支持闪光灯  */
        flashMode = if (newSetting) {
            findSettableValue(
                parameters.supportedFlashModes,
                Camera.Parameters.FLASH_MODE_TORCH,
                Camera.Parameters.FLASH_MODE_ON
            )
        } else {
            findSettableValue(parameters.supportedFlashModes, Camera.Parameters.FLASH_MODE_OFF)
        }
        if (flashMode != null) {
            parameters.flashMode = flashMode
        }
        camera.parameters = parameters
    }

    private fun findSettableValue(supportedValues: Collection<String>?, vararg desiredValues: String): String? {
        var result: String? = null
        if (supportedValues != null) {
            for (desiredValue in desiredValues) {
                if (supportedValues.contains(desiredValue)) {
                    result = desiredValue
                    break
                }
            }
        }
        return result
    }

    private fun getDisplayOrientation(): Int {
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info)
        val wm = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager ?: return 0
        val display = wm.defaultDisplay

        val rotation = display.rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }
        var result: Int
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360
        } else {
            result = (info.orientation - degrees + 360) % 360
        }
        return result
    }

    private fun getPreviewResolution(parameters: Camera.Parameters, screenResolution: Point): Point {
        var previewResolution = findBestPreviewSizeValue(parameters.supportedPreviewSizes, screenResolution)
        if (previewResolution == null) {
            previewResolution = Point(screenResolution.x shr 3 shl 3, screenResolution.y shr 3 shl 3)
        }
        return previewResolution
    }

    private fun findBestPreviewSizeValue(supportSizeList: List<Camera.Size>, screenResolution: Point): Point? {
        var bestX = 0
        var bestY = 0
        var diff = Integer.MAX_VALUE
        for (previewSize in supportSizeList) {

            val newX = previewSize.width
            val newY = previewSize.height

            val newDiff = Math.abs(newX - screenResolution.x) + Math.abs(newY - screenResolution.y)
            if (newDiff == 0) {
                bestX = newX
                bestY = newY
                break
            } else if (newDiff < diff) {
                bestX = newX
                bestY = newY
                diff = newDiff
            }

        }

        return if (bestX > 0 && bestY > 0) {
            Point(bestX, bestY)
        } else null
    }

}