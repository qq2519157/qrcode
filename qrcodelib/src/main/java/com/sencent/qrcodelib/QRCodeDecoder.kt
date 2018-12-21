package com.sencent.qrcodelib

import android.graphics.Bitmap
import com.google.zxing.*
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import java.util.*

/**
 *  Create by Logan at 2018/12/21 0021
 *  解析二维码图片。一维条码、二维码各种类型简介 https://blog.csdn.net/xdg_blog/article/details/52932707
 */
object QRCodeDecoder {
    val allFormatList = arrayListOf(
        BarcodeFormat.AZTEC,
        BarcodeFormat.CODABAR,
        BarcodeFormat.CODE_39,
        BarcodeFormat.CODE_93,
        BarcodeFormat.CODE_128,
        BarcodeFormat.DATA_MATRIX,
        BarcodeFormat.EAN_8,
        BarcodeFormat.EAN_13,
        BarcodeFormat.ITF,
        BarcodeFormat.MAXICODE,
        BarcodeFormat.PDF_417,
        BarcodeFormat.QR_CODE,
        BarcodeFormat.RSS_14,
        BarcodeFormat.RSS_EXPANDED,
        BarcodeFormat.UPC_A,
        BarcodeFormat.UPC_E,
        BarcodeFormat.UPC_EAN_EXTENSION
    )

    val ALL_HINT_MAP = hashMapOf(
        // 可能的编码格式
        Pair(DecodeHintType.POSSIBLE_FORMATS, allFormatList),
        // 花更多的时间用于寻找图上的编码，优化准确性，但不优化速度
        Pair(DecodeHintType.TRY_HARDER, java.lang.Boolean.TRUE),
        // 复杂模式，开启 PURE_BARCODE 模式（带图片 LOGO 的解码方案）
//        Pair(DecodeHintType.PURE_BARCODE, java.lang.Boolean.TRUE)
        // 编码字符集
        Pair(DecodeHintType.CHARACTER_SET, "utf-8")
    )


    val oneDimenFormatList = arrayListOf(
        BarcodeFormat.CODABAR,
        BarcodeFormat.CODE_39,
        BarcodeFormat.CODE_93,
        BarcodeFormat.CODE_128,
        BarcodeFormat.EAN_8,
        BarcodeFormat.EAN_13,
        BarcodeFormat.ITF,
        BarcodeFormat.PDF_417,
        BarcodeFormat.RSS_14,
        BarcodeFormat.RSS_EXPANDED,
        BarcodeFormat.UPC_A,
        BarcodeFormat.UPC_E,
        BarcodeFormat.UPC_EAN_EXTENSION
    )

    val ONE_DIMENSION_HINT_MAP = hashMapOf(
        Pair(DecodeHintType.POSSIBLE_FORMATS, oneDimenFormatList),
        Pair(DecodeHintType.TRY_HARDER, java.lang.Boolean.TRUE),
        Pair(DecodeHintType.CHARACTER_SET, "utf-8")
    )

    val twoDimenFormatList = arrayListOf(
        BarcodeFormat.AZTEC,
        BarcodeFormat.DATA_MATRIX,
        BarcodeFormat.MAXICODE,
        BarcodeFormat.QR_CODE
    )

    val TWO_DIMENSION_HINT_MAP = hashMapOf(
        Pair(DecodeHintType.POSSIBLE_FORMATS, twoDimenFormatList),
        Pair(DecodeHintType.TRY_HARDER, java.lang.Boolean.TRUE),
        Pair(DecodeHintType.CHARACTER_SET, "utf-8")
    )

    val QR_CODE_HINT_MAP = hashMapOf(
        Pair(DecodeHintType.POSSIBLE_FORMATS, listOf(BarcodeFormat.QR_CODE)),
        Pair(DecodeHintType.TRY_HARDER, java.lang.Boolean.TRUE),
        Pair(DecodeHintType.CHARACTER_SET, "utf-8")
    )

    val CODE_128_HINT_MAP = hashMapOf(
        Pair(DecodeHintType.POSSIBLE_FORMATS, listOf(BarcodeFormat.CODE_128)),
        Pair(DecodeHintType.TRY_HARDER, java.lang.Boolean.TRUE),
        Pair(DecodeHintType.CHARACTER_SET, "utf-8")
    )

    val EAN_13_HINT_MAP = hashMapOf(
        Pair(DecodeHintType.POSSIBLE_FORMATS, listOf(BarcodeFormat.EAN_13)),
        Pair(DecodeHintType.TRY_HARDER, java.lang.Boolean.TRUE),
        Pair(DecodeHintType.CHARACTER_SET, "utf-8")
    )

    val highFrequencyFormatList = arrayListOf(
        BarcodeFormat.QR_CODE,
        BarcodeFormat.UPC_A,
        BarcodeFormat.EAN_13,
        BarcodeFormat.CODE_128
    )

    val HIGH_FREQUENCY_HINT_MAP = hashMapOf(
        Pair(DecodeHintType.POSSIBLE_FORMATS, highFrequencyFormatList),
        Pair(DecodeHintType.TRY_HARDER, java.lang.Boolean.TRUE),
        Pair(DecodeHintType.CHARACTER_SET, "utf-8")
    )

    /**
     * 同步解析本地图片二维码。该方法是耗时操作，请在子线程中调用。
     *
     * @param picturePath 要解析的二维码图片本地路径
     * @return 返回二维码图片里的内容 或 null
     */
    fun syncDecodeQRCode(picturePath: String): String? {
        return syncDecodeQRCode(IdentifyUtil.getDecodeAbleBitmap(picturePath))
    }

    /**
     * 同步解析bitmap二维码。该方法是耗时操作，请在子线程中调用。
     *
     * @param bitmap 要解析的二维码图片
     * @return 返回二维码图片里的内容 或 null
     */
    fun syncDecodeQRCode(bitmap: Bitmap?): String? {
        bitmap ?: return null
        var result: Result
        var source: RGBLuminanceSource? = null
        try {
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            source = RGBLuminanceSource(width, height, pixels)
            result = MultiFormatReader().decode(BinaryBitmap(HybridBinarizer(source)), ALL_HINT_MAP)
            return result.text
        } catch (e: Exception) {
            e.printStackTrace()
            if (source != null) {
                try {
                    result = MultiFormatReader().decode(BinaryBitmap(GlobalHistogramBinarizer(source)), ALL_HINT_MAP)
                    return result.text
                } catch (e2: Throwable) {
                    e2.printStackTrace()
                }

            }
            return null
        }

    }
}