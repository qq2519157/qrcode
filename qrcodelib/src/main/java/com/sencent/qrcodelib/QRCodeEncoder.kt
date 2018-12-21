package com.sencent.qrcodelib

import android.graphics.*
import android.text.TextUtils
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.HashMap

/**
 *  Create by Logan at 2018/12/21 0021
 *
 */
object QRCodeEncoder {

    val HINTS = hashMapOf(
        Pair(EncodeHintType.CHARACTER_SET, "utf-8"),
        Pair(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H),
        Pair(EncodeHintType.MARGIN, 0)
    )

    /**
     * 同步创建黑色前景色、白色背景色的二维码图片。该方法是耗时操作，请在子线程中调用。
     *
     * @param content 要生成的二维码图片内容
     * @param size    图片宽高，单位为px
     */
    fun syncEncodeQRCode(content: String, size: Int): Bitmap? {
        return syncEncodeQRCode(content, size, Color.BLACK, Color.WHITE, null)
    }

    /**
     * 同步创建指定前景色、白色背景色的二维码图片。该方法是耗时操作，请在子线程中调用。
     *
     * @param content         要生成的二维码图片内容
     * @param size            图片宽高，单位为px
     * @param foregroundColor 二维码图片的前景色
     */
    fun syncEncodeQRCode(content: String, size: Int, foregroundColor: Int): Bitmap? {
        return syncEncodeQRCode(content, size, foregroundColor, Color.WHITE, null)
    }

    /**
     * 同步创建指定前景色、白色背景色、带logo的二维码图片。该方法是耗时操作，请在子线程中调用。
     *
     * @param content         要生成的二维码图片内容
     * @param size            图片宽高，单位为px
     * @param foregroundColor 二维码图片的前景色
     * @param logo            二维码图片的logo
     */
    fun syncEncodeQRCode(content: String, size: Int, foregroundColor: Int, logo: Bitmap): Bitmap? {
        return syncEncodeQRCode(content, size, foregroundColor, Color.WHITE, logo)
    }

    /**
     * 同步创建指定前景色、指定背景色、带logo的二维码图片。该方法是耗时操作，请在子线程中调用。
     *
     * @param content         要生成的二维码图片内容
     * @param size            图片宽高，单位为px
     * @param foregroundColor 二维码图片的前景色
     * @param backgroundColor 二维码图片的背景色
     * @param logo            二维码图片的logo
     */
    fun syncEncodeQRCode(
        content: String,
        size: Int,
        foregroundColor: Int,
        backgroundColor: Int,
        logo: Bitmap?
    ): Bitmap? {
        try {
            val matrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size, HINTS)
            val pixels = IntArray(size * size)
            for (y in 0 until size) {
                for (x in 0 until size) {
                    if (matrix.get(x, y)) {
                        pixels[y * size + x] = foregroundColor
                    } else {
                        pixels[y * size + x] = backgroundColor
                    }
                }
            }
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size)
            return addLogoToQRCode(bitmap, logo)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 添加logo到二维码图片上
     */
    private fun addLogoToQRCode(src: Bitmap?, logo: Bitmap?): Bitmap? {
        if (src == null || logo == null) {
            return src
        }

        val srcWidth = src.width
        val srcHeight = src.height
        val logoWidth = logo.width
        val logoHeight = logo.height

        val scaleFactor = srcWidth * 1.0f / 5f / logoWidth.toFloat()
        var bitmap: Bitmap? = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888)
        try {
            val canvas = Canvas(bitmap!!)
            canvas.drawBitmap(src, 0f, 0f, null)
            canvas.scale(scaleFactor, scaleFactor, (srcWidth / 2).toFloat(), (srcHeight / 2).toFloat())
            canvas.drawBitmap(
                logo,
                ((srcWidth - logoWidth) / 2).toFloat(),
                ((srcHeight - logoHeight) / 2).toFloat(),
                null
            )
            canvas.save()
            canvas.restore()
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap = null
        }

        return bitmap
    }

    /**
     * 获取圆角+边框
     */
    fun getRoundBitmapByShader(bitmap: Bitmap?, offset: Int, radius: Float, boarder: Float): Bitmap? {
        if (bitmap == null) {
            return null
        }
        val width = bitmap.width
        val height = bitmap.height
        val outWidth = width + offset
        val outHeight = height + offset
        val widthScale = outWidth * 1f / width
        val heightScale = outHeight * 1f / height
        val matrix = Matrix()
        matrix.setScale(widthScale, heightScale)
        //创建输出的bitmap
        val desBitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888)
        //创建canvas并传入desBitmap，这样绘制的内容都会在desBitmap上
        val canvas = Canvas(desBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        //创建着色器
        val bitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = bitmapShader
        //创建矩形区域并且预留出border
        val rect = RectF(boarder, boarder, outWidth - boarder, outHeight - boarder)
        //把传入的bitmap绘制到圆角矩形区域内
        canvas.drawRoundRect(rect, radius, radius, paint);
        if (boarder > 0) {
            //绘制boarder
            val boarderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            boarderPaint.color = Color.WHITE
            boarderPaint.style = Paint.Style.STROKE
            boarderPaint.strokeWidth = boarder
            canvas.drawRoundRect(rect, radius, radius, boarderPaint)
        }
        return desBitmap
    }

    /**
     * 同步创建条形码图片
     *
     * @param content  要生成条形码包含的内容
     * @param width    条形码的宽度，单位px
     * @param height   条形码的高度，单位px
     * @param textSize 字体大小，单位px，如果等于0则不在底部绘制文字
     * @return 返回生成条形的位图
     */
    fun syncEncodeBarcode(content: String, width: Int, height: Int, textSize: Int): Bitmap? {
        if (TextUtils.isEmpty(content)) {
            return null
        }
        val hints = HashMap<EncodeHintType, Any>()
        hints[EncodeHintType.CHARACTER_SET] = "utf-8"
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        hints[EncodeHintType.MARGIN] = 0

        try {
            val bitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.CODE_128, width, height, hints)
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = -0x1000000
                    } else {
                        pixels[y * width + x] = -0x1
                    }
                }
            }
            var bitmap: Bitmap? = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap!!.setPixels(pixels, 0, width, 0, 0, width, height)
            if (textSize > 0) {
                bitmap = showContent(bitmap, content, textSize)
            }
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * 显示条形的内容
     *
     * @param barcodeBitmap 已生成的条形码的位图
     * @param content       条形码包含的内容
     * @param textSize      字体大小，单位px
     * @return 返回生成的新条形码位图
     */
    private fun showContent(barcodeBitmap: Bitmap?, content: String, textSize: Int): Bitmap? {
        if (TextUtils.isEmpty(content) || null == barcodeBitmap) {
            return null
        }
        val paint = Paint()
        paint.color = Color.BLACK
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.textSize = textSize.toFloat()
        paint.textAlign = Paint.Align.CENTER
        val textWidth = paint.measureText(content).toInt()
        val fm = paint.fontMetrics
        val textHeight = (fm.bottom - fm.top).toInt()
        val scaleRateX = barcodeBitmap.width * 1.0f / textWidth
        if (scaleRateX < 1) {
            paint.textScaleX = scaleRateX
        }
        val baseLine = barcodeBitmap.height + textHeight
        val bitmap =
            Bitmap.createBitmap(barcodeBitmap.width, barcodeBitmap.height + 2 * textHeight, Bitmap.Config.ARGB_4444)
        val canvas = Canvas()
        canvas.drawColor(Color.WHITE)
        canvas.setBitmap(bitmap)
        canvas.drawBitmap(barcodeBitmap, 0f, 0f, null)
        canvas.drawText(content, (barcodeBitmap.width / 2).toFloat(), baseLine.toFloat(), paint)
        canvas.save()
        canvas.restore()
        return bitmap
    }
}