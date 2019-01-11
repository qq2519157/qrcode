package com.sencent.qrcodelib

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View

/**
 *  Create by Logan at 2018/12/21 0021
 *
 */
class ScanBoxView(context: Context):View(context) {
    private var mMoveStepDistance: Int = IdentifyUtil.dp2px(context, 2f)
    private var mAnimDelayTime: Int = 0

    private var mFramingRect: Rect? = null
    private var mScanLineTop: Float = 0.toFloat()
    private var mScanLineLeft: Float = 0.toFloat()
    private val mPaint = Paint()
    private val mTipPaint=TextPaint()

    private var mMaskColor: Int = Color.parseColor("#60000000")
    private var mCornerColor: Int =  Color.WHITE
    private var mCornerLength: Int = IdentifyUtil.dp2px(context, 20f)
    private var mCornerSize: Int = IdentifyUtil.dp2px(context, 3f)
    private var mRectWidth: Int = IdentifyUtil.dp2px(context, 200f)
    private var mRectHeight: Int = 0
    private var mBarcodeRectHeight: Int = IdentifyUtil.dp2px(context, 140f)
    private var mTopOffset: Int = IdentifyUtil.dp2px(context, 90f)
    private var mScanLineSize: Int = IdentifyUtil.dp2px(context, 1f)
    private var mScanLineColor: Int = Color.WHITE
    private var mScanLineMargin: Int = 0
    private var mIsShowDefaultScanLineDrawable: Boolean = false
    private var mCustomScanLineDrawable: Drawable? = null
    private var mScanLineBitmap: Bitmap? = null
    private var mBorderSize: Int = IdentifyUtil.dp2px(context, 1f)
    private var mBorderColor: Int = Color.WHITE
    private var mAnimTime: Int = 1000
    private var mVerticalBias: Float = -1f
    private var mCornerDisplayType: Int = 1
    private var mToolbarHeight: Int = 0
    private var mIsBarcode: Boolean = false
    private var mQRCodeTipText: String? = null
    private var mBarCodeTipText: String? = null
    private var mTipText: String? = null
    private var mTipTextSize: Int = IdentifyUtil.sp2px(context, 14f)
    private var mTipTextColor: Int = Color.WHITE
    private var mIsTipTextBelowRect: Boolean = false
    private var mTipTextMargin: Int = IdentifyUtil.dp2px(context, 20f)
    private var mIsShowTipTextAsSingleLine: Boolean = false
    private var mTipBackgroundColor: Int = Color.parseColor("#22000000")
    private var mIsShowTipBackground: Boolean = false
    private var mIsScanLineReverse: Boolean = false
    private var mIsShowDefaultGridScanLineDrawable: Boolean = false
    private var mCustomGridScanLineDrawable: Drawable? = null
    private var mGridScanLineBitmap: Bitmap? = null
    private var mGridScanLineBottom: Float = 0.toFloat()
    private var mGridScanLineRight: Float = 0.toFloat()

    private var mOriginQRCodeScanLineBitmap: Bitmap? = null
    private var mOriginBarCodeScanLineBitmap: Bitmap? = null
    private var mOriginQRCodeGridScanLineBitmap: Bitmap? = null
    private var mOriginBarCodeGridScanLineBitmap: Bitmap? = null


    private var mHalfCornerSize: Float = 0.toFloat()
    private var mTipTextSl: StaticLayout? = null
    private var mTipBackgroundRadius: Int = IdentifyUtil.dp2px(context, 4f)

    private var mIsOnlyDecodeScanBoxArea: Boolean = false
    private var mIsShowLocationPoint: Boolean = false
    private var mIsAutoZoom: Boolean = false

    private var mQRCodeView: QRCodeView? = null

    init {
        mPaint.isAntiAlias = true
        mTipPaint.isAntiAlias = true
    }

     fun init(qrCodeView: QRCodeView, attrs: AttributeSet?) {
        mQRCodeView = qrCodeView
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.QRCodeView)
        val count = typedArray.indexCount
        for (i in 0 until count) {
            initCustomAttr(typedArray.getIndex(i), typedArray)
        }
        typedArray.recycle()

        afterInitCustomAttrs()
    }

    private fun initCustomAttr(attr: Int, typedArray: TypedArray) {
        when (attr) {
            R.styleable.QRCodeView_qrcv_topOffset -> mTopOffset = typedArray.getDimensionPixelSize(attr, mTopOffset)
            R.styleable.QRCodeView_qrcv_cornerSize -> mCornerSize = typedArray.getDimensionPixelSize(attr, mCornerSize)
            R.styleable.QRCodeView_qrcv_cornerLength -> mCornerLength = typedArray.getDimensionPixelSize(attr, mCornerLength)
            R.styleable.QRCodeView_qrcv_scanLineSize -> mScanLineSize = typedArray.getDimensionPixelSize(attr, mScanLineSize)
            R.styleable.QRCodeView_qrcv_rectWidth -> mRectWidth = typedArray.getDimensionPixelSize(attr, mRectWidth)
            R.styleable.QRCodeView_qrcv_maskColor -> mMaskColor = typedArray.getColor(attr, mMaskColor)
            R.styleable.QRCodeView_qrcv_cornerColor -> mCornerColor = typedArray.getColor(attr, mCornerColor)
            R.styleable.QRCodeView_qrcv_scanLineColor -> mScanLineColor = typedArray.getColor(attr, mScanLineColor)
            R.styleable.QRCodeView_qrcv_scanLineMargin -> mScanLineMargin = typedArray.getDimensionPixelSize(attr, mScanLineMargin)
            R.styleable.QRCodeView_qrcv_isShowDefaultScanLineDrawable -> mIsShowDefaultScanLineDrawable = typedArray.getBoolean(attr, mIsShowDefaultScanLineDrawable)
            R.styleable.QRCodeView_qrcv_customScanLineDrawable -> mCustomScanLineDrawable = typedArray.getDrawable(attr)
            R.styleable.QRCodeView_qrcv_borderSize -> mBorderSize = typedArray.getDimensionPixelSize(attr, mBorderSize)
            R.styleable.QRCodeView_qrcv_borderColor -> mBorderColor = typedArray.getColor(attr, mBorderColor)
            R.styleable.QRCodeView_qrcv_animTime -> mAnimTime = typedArray.getInteger(attr, mAnimTime)
            R.styleable.QRCodeView_qrcv_verticalBias -> mVerticalBias = typedArray.getFloat(attr, mVerticalBias)
            R.styleable.QRCodeView_qrcv_cornerDisplayType -> mCornerDisplayType = typedArray.getInteger(attr, mCornerDisplayType)
            R.styleable.QRCodeView_qrcv_toolbarHeight -> mToolbarHeight = typedArray.getDimensionPixelSize(attr, mToolbarHeight)
            R.styleable.QRCodeView_qrcv_barcodeRectHeight -> mBarcodeRectHeight = typedArray.getDimensionPixelSize(attr, mBarcodeRectHeight)
            R.styleable.QRCodeView_qrcv_isBarcode -> mIsBarcode = typedArray.getBoolean(attr, mIsBarcode)
            R.styleable.QRCodeView_qrcv_barCodeTipText -> mBarCodeTipText = typedArray.getString(attr)
            R.styleable.QRCodeView_qrcv_qrCodeTipText -> mQRCodeTipText = typedArray.getString(attr)
            R.styleable.QRCodeView_qrcv_tipTextSize -> mTipTextSize = typedArray.getDimensionPixelSize(attr, mTipTextSize)
            R.styleable.QRCodeView_qrcv_tipTextColor -> mTipTextColor = typedArray.getColor(attr, mTipTextColor)
            R.styleable.QRCodeView_qrcv_isTipTextBelowRect -> mIsTipTextBelowRect = typedArray.getBoolean(attr, mIsTipTextBelowRect)
            R.styleable.QRCodeView_qrcv_tipTextMargin -> mTipTextMargin = typedArray.getDimensionPixelSize(attr, mTipTextMargin)
            R.styleable.QRCodeView_qrcv_isShowTipTextAsSingleLine -> mIsShowTipTextAsSingleLine = typedArray.getBoolean(attr, mIsShowTipTextAsSingleLine)
            R.styleable.QRCodeView_qrcv_isShowTipBackground -> mIsShowTipBackground = typedArray.getBoolean(attr, mIsShowTipBackground)
            R.styleable.QRCodeView_qrcv_tipBackgroundColor -> mTipBackgroundColor = typedArray.getColor(attr, mTipBackgroundColor)
            R.styleable.QRCodeView_qrcv_isScanLineReverse -> mIsScanLineReverse = typedArray.getBoolean(attr, mIsScanLineReverse)
            R.styleable.QRCodeView_qrcv_isShowDefaultGridScanLineDrawable -> mIsShowDefaultGridScanLineDrawable = typedArray.getBoolean(attr, mIsShowDefaultGridScanLineDrawable)
            R.styleable.QRCodeView_qrcv_customGridScanLineDrawable -> mCustomGridScanLineDrawable = typedArray.getDrawable(attr)
            R.styleable.QRCodeView_qrcv_isOnlyDecodeScanBoxArea -> mIsOnlyDecodeScanBoxArea = typedArray.getBoolean(attr, mIsOnlyDecodeScanBoxArea)
            R.styleable.QRCodeView_qrcv_isShowLocationPoint -> mIsShowLocationPoint = typedArray.getBoolean(attr, mIsShowLocationPoint)
            R.styleable.QRCodeView_qrcv_isAutoZoom -> mIsAutoZoom = typedArray.getBoolean(attr, mIsAutoZoom)
        }
    }

    private fun afterInitCustomAttrs() {
        if (mCustomGridScanLineDrawable != null) {
            mOriginQRCodeGridScanLineBitmap = (mCustomGridScanLineDrawable as BitmapDrawable).bitmap
        }
        if (mOriginQRCodeGridScanLineBitmap == null) {
            mOriginQRCodeGridScanLineBitmap =
                    BitmapFactory.decodeResource(resources, R.drawable.qrcode_default_grid_scan_line)
            mOriginQRCodeGridScanLineBitmap =
                    IdentifyUtil.makeTintBitmap(mOriginQRCodeGridScanLineBitmap, mScanLineColor)
        }
        mOriginBarCodeGridScanLineBitmap = IdentifyUtil.adjustPhotoRotation(mOriginQRCodeGridScanLineBitmap, 90)
        mOriginBarCodeGridScanLineBitmap = IdentifyUtil.adjustPhotoRotation(mOriginBarCodeGridScanLineBitmap, 90)
        mOriginBarCodeGridScanLineBitmap = IdentifyUtil.adjustPhotoRotation(mOriginBarCodeGridScanLineBitmap, 90)


        if (mCustomScanLineDrawable != null) {
            mOriginQRCodeScanLineBitmap = (mCustomScanLineDrawable as BitmapDrawable).bitmap
        }
        if (mOriginQRCodeScanLineBitmap == null) {
            mOriginQRCodeScanLineBitmap = BitmapFactory.decodeResource(resources, R.drawable.qrcode_default_scan_line)
            mOriginQRCodeScanLineBitmap = IdentifyUtil.makeTintBitmap(mOriginQRCodeScanLineBitmap, mScanLineColor)
        }
        mOriginBarCodeScanLineBitmap = IdentifyUtil.adjustPhotoRotation(mOriginQRCodeScanLineBitmap, 90)

        mTopOffset += mToolbarHeight
        mHalfCornerSize = 1.0f * mCornerSize / 2

        mTipPaint.textSize = mTipTextSize.toFloat()
        mTipPaint.color = mTipTextColor

        setIsBarcode(mIsBarcode)
    }

    public override fun onDraw(canvas: Canvas) {
        if (mFramingRect == null) {
            return
        }

        // 画遮罩层
        drawMask(canvas)

        // 画边框线
        drawBorderLine(canvas)

        // 画四个直角的线
        drawCornerLine(canvas)

        // 画扫描线
        drawScanLine(canvas)

        // 画提示文本
        drawTipText(canvas)

        // 移动扫描线的位置
        moveScanLine()
    }

    /**
     * 画遮罩层
     */
    private fun drawMask(canvas: Canvas) {
        val width = canvas.width
        val height = canvas.height
        val frameRect =mFramingRect?:return
        if (mMaskColor != Color.TRANSPARENT) {
            mPaint.style = Paint.Style.FILL
            mPaint.color = mMaskColor
            canvas.drawRect(0f, 0f, width.toFloat(), frameRect.top.toFloat(), mPaint)
            canvas.drawRect(
                0f,
                frameRect.top.toFloat(),
                frameRect.left.toFloat(),
                (frameRect.bottom + 1).toFloat(),
                mPaint
            )
            canvas.drawRect(
                (frameRect.right + 1).toFloat(),
                frameRect.top.toFloat(),
                width.toFloat(),
                (frameRect.bottom + 1).toFloat(),
                mPaint
            )
            canvas.drawRect(0f, (frameRect.bottom + 1).toFloat(), width.toFloat(), height.toFloat(), mPaint)
        }
    }

    /**
     * 画边框线
     */
    private fun drawBorderLine(canvas: Canvas) {
        val frameRect =mFramingRect?:return
        if (mBorderSize > 0) {
            mPaint.style = Paint.Style.STROKE
            mPaint.color = mBorderColor
            mPaint.strokeWidth = mBorderSize.toFloat()
            canvas.drawRect(frameRect, mPaint)
        }
    }

    /**
     * 画四个直角的线
     */
    private fun drawCornerLine(canvas: Canvas) {
        val frameRect=mFramingRect?:return
        if (mHalfCornerSize > 0) {
            mPaint.style = Paint.Style.STROKE
            mPaint.color = mCornerColor
            mPaint.strokeWidth = mCornerSize.toFloat()
            if (mCornerDisplayType == 1) {
                canvas.drawLine(
                    frameRect.left - mHalfCornerSize,
                    frameRect.top.toFloat(),
                    frameRect.left - mHalfCornerSize + mCornerLength,
                    frameRect.top.toFloat(),
                    mPaint
                )
                canvas.drawLine(
                    frameRect.left.toFloat(),
                    frameRect.top - mHalfCornerSize,
                    frameRect.left.toFloat(),
                    frameRect.top - mHalfCornerSize + mCornerLength,
                    mPaint
                )
                canvas.drawLine(
                    frameRect.right + mHalfCornerSize,
                    frameRect.top.toFloat(),
                    frameRect.right + mHalfCornerSize - mCornerLength,
                    frameRect.top.toFloat(),
                    mPaint
                )
                canvas.drawLine(
                    frameRect.right.toFloat(),
                    frameRect.top - mHalfCornerSize,
                    frameRect.right.toFloat(),
                    frameRect.top - mHalfCornerSize + mCornerLength,
                    mPaint
                )

                canvas.drawLine(
                    frameRect.left - mHalfCornerSize,
                    frameRect.bottom.toFloat(),
                    frameRect.left - mHalfCornerSize + mCornerLength,
                    frameRect.bottom.toFloat(),
                    mPaint
                )
                canvas.drawLine(
                    frameRect.left.toFloat(), frameRect.bottom + mHalfCornerSize, frameRect.left.toFloat(),
                    frameRect.bottom + mHalfCornerSize - mCornerLength, mPaint
                )
                canvas.drawLine(
                    frameRect.right + mHalfCornerSize,
                    frameRect.bottom.toFloat(),
                    frameRect.right + mHalfCornerSize - mCornerLength,
                    frameRect.bottom.toFloat(),
                    mPaint
                )
                canvas.drawLine(
                    frameRect.right.toFloat(), frameRect.bottom + mHalfCornerSize, frameRect.right.toFloat(),
                    frameRect.bottom + mHalfCornerSize - mCornerLength, mPaint
                )
            } else if (mCornerDisplayType == 2) {
                canvas.drawLine(
                    frameRect.left.toFloat(),
                    frameRect.top + mHalfCornerSize,
                    (frameRect.left + mCornerLength).toFloat(),
                    frameRect.top + mHalfCornerSize,
                    mPaint
                )
                canvas.drawLine(
                    frameRect.left + mHalfCornerSize,
                    frameRect.top.toFloat(),
                    frameRect.left + mHalfCornerSize,
                    (frameRect.top + mCornerLength).toFloat(),
                    mPaint
                )
                canvas.drawLine(
                    frameRect.right.toFloat(),
                    frameRect.top + mHalfCornerSize,
                    (frameRect.right - mCornerLength).toFloat(),
                    frameRect.top + mHalfCornerSize,
                    mPaint
                )
                canvas.drawLine(
                    frameRect.right - mHalfCornerSize,
                    frameRect.top.toFloat(),
                    frameRect.right - mHalfCornerSize,
                    (frameRect.top + mCornerLength).toFloat(),
                    mPaint
                )

                canvas.drawLine(
                    frameRect.left.toFloat(),
                    frameRect.bottom - mHalfCornerSize,
                    (frameRect.left + mCornerLength).toFloat(),
                    frameRect.bottom - mHalfCornerSize,
                    mPaint
                )
                canvas.drawLine(
                    frameRect.left + mHalfCornerSize,
                    frameRect.bottom.toFloat(),
                    frameRect.left + mHalfCornerSize,
                    (frameRect.bottom - mCornerLength).toFloat(),
                    mPaint
                )
                canvas.drawLine(
                    frameRect.right.toFloat(),
                    frameRect.bottom - mHalfCornerSize,
                    (frameRect.right - mCornerLength).toFloat(),
                    frameRect.bottom - mHalfCornerSize,
                    mPaint
                )
                canvas.drawLine(
                    frameRect.right - mHalfCornerSize,
                    frameRect.bottom.toFloat(),
                    frameRect.right - mHalfCornerSize,
                    (frameRect.bottom - mCornerLength).toFloat(),
                    mPaint
                )
            }
        }
    }

    /**
     * 画扫描线
     */
    private fun drawScanLine(canvas: Canvas) {
        val frameRect=mFramingRect?:return
        if (mIsBarcode) {
            when {
                mGridScanLineBitmap != null -> {
                    val gridScanLineBitmap=mGridScanLineBitmap?:return
                    val dstGridRectF = RectF(
                        frameRect.left.toFloat() + mHalfCornerSize + 0.5f,
                        frameRect.top.toFloat() + mHalfCornerSize + mScanLineMargin.toFloat(),
                        mGridScanLineRight,
                        frameRect.bottom.toFloat() - mHalfCornerSize - mScanLineMargin.toFloat()
                    )

                    val srcGridRect = Rect(
                        (gridScanLineBitmap.width - dstGridRectF.width()).toInt(), 0, gridScanLineBitmap.width,
                        gridScanLineBitmap.height
                    )

                    if (srcGridRect.left < 0) {
                        srcGridRect.left = 0
                        dstGridRectF.left = dstGridRectF.right - srcGridRect.width()
                    }

                    canvas.drawBitmap(gridScanLineBitmap, srcGridRect, dstGridRectF, mPaint)
                }
                mScanLineBitmap != null -> {
                    val scanBitmap=mScanLineBitmap?:return
                    val lineRect = RectF(
                        mScanLineLeft,
                        frameRect.top.toFloat() + mHalfCornerSize + mScanLineMargin.toFloat(),
                        mScanLineLeft + scanBitmap.getWidth(),
                        frameRect.bottom.toFloat() - mHalfCornerSize - mScanLineMargin.toFloat()
                    )
                    canvas.drawBitmap(scanBitmap, null, lineRect, mPaint)
                }
                else -> {
                    mPaint.style = Paint.Style.FILL
                    mPaint.color = mScanLineColor
                    canvas.drawRect(
                        mScanLineLeft,
                        frameRect.top.toFloat() + mHalfCornerSize + mScanLineMargin.toFloat(),
                        mScanLineLeft + mScanLineSize,
                        frameRect.bottom.toFloat() - mHalfCornerSize - mScanLineMargin.toFloat(),
                        mPaint
                    )
                }
            }
        } else {
            when {
                mGridScanLineBitmap != null -> {
                    val gridScanLineBitmap=mGridScanLineBitmap?:return
                    val dstGridRectF = RectF(
                        frameRect.left.toFloat() + mHalfCornerSize + mScanLineMargin.toFloat(),
                        frameRect.top.toFloat() + mHalfCornerSize + 0.5f,
                        frameRect.right.toFloat() - mHalfCornerSize - mScanLineMargin.toFloat(),
                        mGridScanLineBottom
                    )

                    val srcRect = Rect(
                        0,
                        (gridScanLineBitmap.height - dstGridRectF.height()).toInt(),
                        gridScanLineBitmap.width,
                        gridScanLineBitmap.height
                    )

                    if (srcRect.top < 0) {
                        srcRect.top = 0
                        dstGridRectF.top = dstGridRectF.bottom - srcRect.height()
                    }

                    canvas.drawBitmap(gridScanLineBitmap, srcRect, dstGridRectF, mPaint)
                }
                mScanLineBitmap != null -> {
                    val scanBitmap=mScanLineBitmap?:return
                    val lineRect = RectF(
                        frameRect.left.toFloat() + mHalfCornerSize + mScanLineMargin.toFloat(),
                        mScanLineTop,
                        frameRect.right.toFloat() - mHalfCornerSize - mScanLineMargin.toFloat(),
                        mScanLineTop + scanBitmap.getHeight()
                    )
                    canvas.drawBitmap(scanBitmap, null, lineRect, mPaint)
                }
                else -> {
                    mPaint.style = Paint.Style.FILL
                    mPaint.color = mScanLineColor
                    canvas.drawRect(
                        frameRect.left.toFloat() + mHalfCornerSize + mScanLineMargin.toFloat(),
                        mScanLineTop,
                        frameRect.right.toFloat() - mHalfCornerSize - mScanLineMargin.toFloat(),
                        mScanLineTop + mScanLineSize,
                        mPaint
                    )
                }
            }
        }
    }

    /**
     * 画提示文本
     */
    private fun drawTipText(canvas: Canvas) {
        val frameRect=mFramingRect?:return
        val tipTextSl =mTipTextSl?:return
        if (TextUtils.isEmpty(mTipText)) {
            return
        }

        if (mIsTipTextBelowRect) {
            if (mIsShowTipBackground) {
                mPaint.color = mTipBackgroundColor
                mPaint.style = Paint.Style.FILL
                if (mIsShowTipTextAsSingleLine) {
                    val tipRect = Rect()
                    mTipPaint.getTextBounds(mTipText, 0, mTipText!!.length, tipRect)
                    val left = ((canvas.width - tipRect.width()) / 2 - mTipBackgroundRadius).toFloat()
                    canvas.drawRoundRect(
                        RectF(
                            left,
                            (frameRect.bottom + mTipTextMargin - mTipBackgroundRadius).toFloat(),
                            left + tipRect.width().toFloat() + (2 * mTipBackgroundRadius).toFloat(),
                            (frameRect.bottom + mTipTextMargin + tipTextSl.height + mTipBackgroundRadius).toFloat()
                        ), mTipBackgroundRadius.toFloat(),
                        mTipBackgroundRadius.toFloat(), mPaint
                    )
                } else {
                    canvas.drawRoundRect(
                        RectF(
                            frameRect.left.toFloat(),
                            (frameRect.bottom + mTipTextMargin - mTipBackgroundRadius).toFloat(),
                            frameRect.right.toFloat(),
                            (frameRect.bottom + mTipTextMargin + tipTextSl.height + mTipBackgroundRadius).toFloat()
                        ), mTipBackgroundRadius.toFloat(),
                        mTipBackgroundRadius.toFloat(),
                        mPaint
                    )
                }
            }

            canvas.save()
            if (mIsShowTipTextAsSingleLine) {
                canvas.translate(0f, (frameRect.bottom + mTipTextMargin).toFloat())
            } else {
                canvas.translate(
                    (frameRect.left + mTipBackgroundRadius).toFloat(),
                    (frameRect.bottom + mTipTextMargin).toFloat()
                )
            }
            tipTextSl.draw(canvas)
            canvas.restore()
        } else {
            if (mIsShowTipBackground) {
                mPaint.color = mTipBackgroundColor
                mPaint.style = Paint.Style.FILL

                if (mIsShowTipTextAsSingleLine) {
                    val tipRect = Rect()
                    mTipPaint.getTextBounds(mTipText, 0, mTipText!!.length, tipRect)
                    val left = ((canvas.width - tipRect.width()) / 2 - mTipBackgroundRadius).toFloat()
                    canvas.drawRoundRect(
                        RectF(
                            left,
                            (frameRect.top - mTipTextMargin - tipTextSl.height - mTipBackgroundRadius).toFloat(),
                            left + tipRect.width().toFloat() + (2 * mTipBackgroundRadius).toFloat(),
                            (frameRect.top - mTipTextMargin + mTipBackgroundRadius).toFloat()
                        ),
                        mTipBackgroundRadius.toFloat(),
                        mTipBackgroundRadius.toFloat(), mPaint
                    )
                } else {
                    canvas.drawRoundRect(
                        RectF(
                            frameRect.left.toFloat(),
                            (frameRect.top - mTipTextMargin - tipTextSl.height - mTipBackgroundRadius).toFloat(),
                            frameRect.right.toFloat(),
                            (frameRect.top - mTipTextMargin + mTipBackgroundRadius).toFloat()
                        ), mTipBackgroundRadius.toFloat(), mTipBackgroundRadius.toFloat(), mPaint
                    )
                }
            }

            canvas.save()
            if (mIsShowTipTextAsSingleLine) {
                canvas.translate(0f, (frameRect.top - mTipTextMargin - tipTextSl.height).toFloat())
            } else {
                canvas.translate(
                    (frameRect.left + mTipBackgroundRadius).toFloat(),
                    (frameRect.top - mTipTextMargin - tipTextSl.height).toFloat()
                )
            }
            tipTextSl.draw(canvas)
            canvas.restore()
        }
    }

    /**
     * 移动扫描线的位置
     */
    private fun moveScanLine() {
        val frameRect=mFramingRect?:return
        if (mIsBarcode) {
            if (mGridScanLineBitmap == null) {
                // 处理非网格扫描图片的情况
                mScanLineLeft += mMoveStepDistance.toFloat()
                var scanLineSize = mScanLineSize
                if (mScanLineBitmap != null) {
                    scanLineSize = mScanLineBitmap!!.width
                }

                if (mIsScanLineReverse) {
                    if (mScanLineLeft + scanLineSize > frameRect.right - mHalfCornerSize || mScanLineLeft < frameRect.left + mHalfCornerSize) {
                        mMoveStepDistance = -mMoveStepDistance
                    }
                } else {
                    if (mScanLineLeft + scanLineSize > frameRect.right - mHalfCornerSize) {
                        mScanLineLeft = frameRect.left.toFloat() + mHalfCornerSize + 0.5f
                    }
                }
            } else {
                // 处理网格扫描图片的情况
                mGridScanLineRight += mMoveStepDistance.toFloat()
                if (mGridScanLineRight > frameRect.right - mHalfCornerSize) {
                    mGridScanLineRight = frameRect.left.toFloat() + mHalfCornerSize + 0.5f
                }
            }
        } else {
            if (mGridScanLineBitmap == null) {
                // 处理非网格扫描图片的情况
                mScanLineTop += mMoveStepDistance.toFloat()
                var scanLineSize = mScanLineSize
                if (mScanLineBitmap != null) {
                    scanLineSize = mScanLineBitmap!!.height
                }

                if (mIsScanLineReverse) {
                    if (mScanLineTop + scanLineSize > frameRect.bottom - mHalfCornerSize || mScanLineTop < frameRect.top + mHalfCornerSize) {
                        mMoveStepDistance = -mMoveStepDistance
                    }
                } else {
                    if (mScanLineTop + scanLineSize > frameRect.bottom - mHalfCornerSize) {
                        mScanLineTop = frameRect.top.toFloat() + mHalfCornerSize + 0.5f
                    }
                }
            } else {
                // 处理网格扫描图片的情况
                mGridScanLineBottom += mMoveStepDistance.toFloat()
                if (mGridScanLineBottom > frameRect.bottom - mHalfCornerSize) {
                    mGridScanLineBottom = frameRect.top.toFloat() + mHalfCornerSize + 0.5f
                }
            }

        }
        postInvalidateDelayed(
            mAnimDelayTime.toLong(),
            frameRect.left,
            frameRect.top,
            frameRect.right,
            frameRect.bottom
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calFramingRect()
    }

    private fun calFramingRect() {
        val leftOffset = (width - mRectWidth) / 2
        mFramingRect = Rect(leftOffset, mTopOffset, leftOffset + mRectWidth, mTopOffset + mRectHeight)

        if (mIsBarcode) {
            mScanLineLeft = mFramingRect!!.left.toFloat() + mHalfCornerSize + 0.5f
            mGridScanLineRight = mScanLineLeft
        } else {
            mScanLineTop = mFramingRect!!.top.toFloat() + mHalfCornerSize + 0.5f
            mGridScanLineBottom = mScanLineTop
        }

        if (mQRCodeView != null && isOnlyDecodeScanBoxArea()) {
            mQRCodeView!!.onScanBoxRectChanged(Rect(mFramingRect))
        }
    }

    fun getScanBoxAreaRect(previewHeight: Int): Rect? {
        if (mIsOnlyDecodeScanBoxArea && visibility == View.VISIBLE) {
            val rect = Rect(mFramingRect)
            val ratio = 1.0f * previewHeight / measuredHeight

            val centerX = rect.exactCenterX()
            val centerY = rect.exactCenterY()

            val halfWidth = rect.width() / 2f
            val halfHeight = rect.height() / 2f
            val newHalfWidth = halfWidth * ratio
            val newHalfHeight = halfHeight * ratio

            rect.left = (centerX - newHalfWidth).toInt()
            rect.right = (centerX + newHalfWidth).toInt()
            rect.top = (centerY - newHalfHeight).toInt()
            rect.bottom = (centerY + newHalfHeight).toInt()
            return rect
        } else {
            return null
        }
    }

    fun setIsBarcode(isBarcode: Boolean) {
        mIsBarcode = isBarcode
        refreshScanBox()
    }

    private fun refreshScanBox() {
        if (mCustomGridScanLineDrawable != null || mIsShowDefaultGridScanLineDrawable) {
            if (mIsBarcode) {
                mGridScanLineBitmap = mOriginBarCodeGridScanLineBitmap
            } else {
                mGridScanLineBitmap = mOriginQRCodeGridScanLineBitmap
            }
        } else if (mCustomScanLineDrawable != null || mIsShowDefaultScanLineDrawable) {
            if (mIsBarcode) {
                mScanLineBitmap = mOriginBarCodeScanLineBitmap
            } else {
                mScanLineBitmap = mOriginQRCodeScanLineBitmap
            }
        }

        if (mIsBarcode) {
            mTipText = mBarCodeTipText
            mRectHeight = mBarcodeRectHeight
            mAnimDelayTime = (1.0f * mAnimTime.toFloat() * mMoveStepDistance.toFloat() / mRectWidth).toInt()
        } else {
            mTipText = mQRCodeTipText
            mRectHeight = mRectWidth
            mAnimDelayTime = (1.0f * mAnimTime.toFloat() * mMoveStepDistance.toFloat() / mRectHeight).toInt()
        }

        if (!TextUtils.isEmpty(mTipText)) {
            if (mIsShowTipTextAsSingleLine) {
                mTipTextSl = StaticLayout(
                    mTipText,
                    mTipPaint,
                    IdentifyUtil.getScreenResolution(context).x,
                    Layout.Alignment.ALIGN_CENTER,
                    1.0f,
                    0f,
                    true
                )
            } else {
                mTipTextSl = StaticLayout(
                    mTipText,
                    mTipPaint,
                    mRectWidth - 2 * mTipBackgroundRadius,
                    Layout.Alignment.ALIGN_CENTER,
                    1.0f,
                    0f,
                    true
                )
            }
        }

        if (mVerticalBias != -1f) {
            val screenHeight = IdentifyUtil.getScreenResolution(context).y - IdentifyUtil.getStatusBarHeight(context)
            if (mToolbarHeight == 0) {
                mTopOffset = (screenHeight * mVerticalBias - mRectHeight / 2).toInt()
            } else {
                mTopOffset = mToolbarHeight +
                        ((screenHeight - mToolbarHeight) * mVerticalBias - mRectHeight / 2).toInt()
            }
        }

        calFramingRect()

        postInvalidate()
    }

    fun getIsBarcode(): Boolean {
        return mIsBarcode
    }

    fun getMaskColor(): Int {
        return mMaskColor
    }

    fun setMaskColor(maskColor: Int) {
        mMaskColor = maskColor
        refreshScanBox()
    }

    fun getCornerColor(): Int {
        return mCornerColor
    }

    fun setCornerColor(cornerColor: Int) {
        mCornerColor = cornerColor
        refreshScanBox()
    }

    fun getCornerLength(): Int {
        return mCornerLength
    }

    fun setCornerLength(cornerLength: Int) {
        mCornerLength = cornerLength
        refreshScanBox()
    }

    fun getCornerSize(): Int {
        return mCornerSize
    }

    fun setCornerSize(cornerSize: Int) {
        mCornerSize = cornerSize
        refreshScanBox()
    }

    fun getRectWidth(): Int {
        return mRectWidth
    }

    fun setRectWidth(rectWidth: Int) {
        mRectWidth = rectWidth
        refreshScanBox()
    }

    fun getRectHeight(): Int {
        return mRectHeight
    }

    fun setRectHeight(rectHeight: Int) {
        mRectHeight = rectHeight
        refreshScanBox()
    }

    fun getBarcodeRectHeight(): Int {
        return mBarcodeRectHeight
    }

    fun setBarcodeRectHeight(barcodeRectHeight: Int) {
        mBarcodeRectHeight = barcodeRectHeight
        refreshScanBox()
    }

    fun getTopOffset(): Int {
        return mTopOffset
    }

    fun setTopOffset(topOffset: Int) {
        mTopOffset = topOffset
        refreshScanBox()
    }

    fun getScanLineSize(): Int {
        return mScanLineSize
    }

    fun setScanLineSize(scanLineSize: Int) {
        mScanLineSize = scanLineSize
        refreshScanBox()
    }

    fun getScanLineColor(): Int {
        return mScanLineColor
    }

    fun setScanLineColor(scanLineColor: Int) {
        mScanLineColor = scanLineColor
        refreshScanBox()
    }

    fun getScanLineMargin(): Int {
        return mScanLineMargin
    }

    fun setScanLineMargin(scanLineMargin: Int) {
        mScanLineMargin = scanLineMargin
        refreshScanBox()
    }

    fun isShowDefaultScanLineDrawable(): Boolean {
        return mIsShowDefaultScanLineDrawable
    }

    fun setShowDefaultScanLineDrawable(showDefaultScanLineDrawable: Boolean) {
        mIsShowDefaultScanLineDrawable = showDefaultScanLineDrawable
        refreshScanBox()
    }

    fun getCustomScanLineDrawable(): Drawable? {
        return mCustomScanLineDrawable
    }

    fun setCustomScanLineDrawable(customScanLineDrawable: Drawable) {
        mCustomScanLineDrawable = customScanLineDrawable
        refreshScanBox()
    }

    fun getScanLineBitmap(): Bitmap? {
        return mScanLineBitmap
    }

    fun setScanLineBitmap(scanLineBitmap: Bitmap) {
        mScanLineBitmap = scanLineBitmap
        refreshScanBox()
    }

    fun getBorderSize(): Int {
        return mBorderSize
    }

    fun setBorderSize(borderSize: Int) {
        mBorderSize = borderSize
        refreshScanBox()
    }

    fun getBorderColor(): Int {
        return mBorderColor
    }

    fun setBorderColor(borderColor: Int) {
        mBorderColor = borderColor
        refreshScanBox()
    }

    fun getAnimTime(): Int {
        return mAnimTime
    }

    fun setAnimTime(animTime: Int) {
        mAnimTime = animTime
        refreshScanBox()
    }

    fun getVerticalBias(): Float {
        return mVerticalBias
    }

    fun setVerticalBias(verticalBias: Float) {
        mVerticalBias = verticalBias
        refreshScanBox()
    }

    fun getToolbarHeight(): Int {
        return mToolbarHeight
    }

    fun setToolbarHeight(toolbarHeight: Int) {
        mToolbarHeight = toolbarHeight
        refreshScanBox()
    }

    fun getQRCodeTipText(): String? {
        return mQRCodeTipText
    }

    fun setQRCodeTipText(qrCodeTipText: String) {
        mQRCodeTipText = qrCodeTipText
        refreshScanBox()
    }

    fun getBarCodeTipText(): String? {
        return mBarCodeTipText
    }

    fun setBarCodeTipText(barCodeTipText: String) {
        mBarCodeTipText = barCodeTipText
        refreshScanBox()
    }

    fun getTipText(): String? {
        return mTipText
    }

    fun setTipText(tipText: String) {
        if (mIsBarcode) {
            mBarCodeTipText = tipText
        } else {
            mQRCodeTipText = tipText
        }
        refreshScanBox()
    }

    fun getTipTextColor(): Int {
        return mTipTextColor
    }

    fun setTipTextColor(tipTextColor: Int) {
        mTipTextColor = tipTextColor
        mTipPaint.color = mTipTextColor
        refreshScanBox()
    }

    fun getTipTextSize(): Int {
        return mTipTextSize
    }

    fun setTipTextSize(tipTextSize: Int) {
        mTipTextSize = tipTextSize
        mTipPaint.textSize = mTipTextSize.toFloat()
        refreshScanBox()
    }

    fun isTipTextBelowRect(): Boolean {
        return mIsTipTextBelowRect
    }

    fun setTipTextBelowRect(tipTextBelowRect: Boolean) {
        mIsTipTextBelowRect = tipTextBelowRect
        refreshScanBox()
    }

    fun getTipTextMargin(): Int {
        return mTipTextMargin
    }

    fun setTipTextMargin(tipTextMargin: Int) {
        mTipTextMargin = tipTextMargin
        refreshScanBox()
    }

    fun isShowTipTextAsSingleLine(): Boolean {
        return mIsShowTipTextAsSingleLine
    }

    fun setShowTipTextAsSingleLine(showTipTextAsSingleLine: Boolean) {
        mIsShowTipTextAsSingleLine = showTipTextAsSingleLine
        refreshScanBox()
    }

    fun isShowTipBackground(): Boolean {
        return mIsShowTipBackground
    }

    fun setShowTipBackground(showTipBackground: Boolean) {
        mIsShowTipBackground = showTipBackground
        refreshScanBox()
    }

    fun getTipBackgroundColor(): Int {
        return mTipBackgroundColor
    }

    fun setTipBackgroundColor(tipBackgroundColor: Int) {
        mTipBackgroundColor = tipBackgroundColor
        refreshScanBox()
    }

    fun isScanLineReverse(): Boolean {
        return mIsScanLineReverse
    }

    fun setScanLineReverse(scanLineReverse: Boolean) {
        mIsScanLineReverse = scanLineReverse
        refreshScanBox()
    }

    fun isShowDefaultGridScanLineDrawable(): Boolean {
        return mIsShowDefaultGridScanLineDrawable
    }

    fun setShowDefaultGridScanLineDrawable(showDefaultGridScanLineDrawable: Boolean) {
        mIsShowDefaultGridScanLineDrawable = showDefaultGridScanLineDrawable
        refreshScanBox()
    }

    fun getHalfCornerSize(): Float {
        return mHalfCornerSize
    }

    fun setHalfCornerSize(halfCornerSize: Float) {
        mHalfCornerSize = halfCornerSize
        refreshScanBox()
    }

    fun getTipTextSl(): StaticLayout? {
        return mTipTextSl
    }

    fun setTipTextSl(tipTextSl: StaticLayout) {
        mTipTextSl = tipTextSl
        refreshScanBox()
    }

    fun getTipBackgroundRadius(): Int {
        return mTipBackgroundRadius
    }

    fun setTipBackgroundRadius(tipBackgroundRadius: Int) {
        mTipBackgroundRadius = tipBackgroundRadius
        refreshScanBox()
    }

    fun isOnlyDecodeScanBoxArea(): Boolean {
        return mIsOnlyDecodeScanBoxArea
    }

    fun setOnlyDecodeScanBoxArea(onlyDecodeScanBoxArea: Boolean) {
        mIsOnlyDecodeScanBoxArea = onlyDecodeScanBoxArea
        calFramingRect()
    }

    fun isShowLocationPoint(): Boolean {
        return mIsShowLocationPoint
    }

    fun setShowLocationPoint(showLocationPoint: Boolean) {
        mIsShowLocationPoint = showLocationPoint
    }

    fun isAutoZoom(): Boolean {
        return mIsAutoZoom
    }

    fun setAutoZoom(autoZoom: Boolean) {
        mIsAutoZoom = autoZoom
    }
}