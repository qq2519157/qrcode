package com.sencent.qrcodelib

import android.graphics.PointF

/**
 *  Create by Logan at 2018/12/21 0021
 *
 */
class ScanResult() {
     var result: String?=null
     var resultPoints: Array<PointF>?=null

    constructor(result: String?) : this() {
        this.result=result
    }

    constructor(resultPoints: Array<PointF>?) : this() {
        this.resultPoints=resultPoints
    }

    constructor(result: String?, resultPoints: Array<PointF>?) : this() {
        this.result=result
        this.resultPoints=resultPoints
    }
}