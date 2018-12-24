package com.sencent.qrcode

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.sencent.qrcodelib.IdentifyUtil
import com.sencent.qrcodelib.QRCodeEncoder
import kotlin.concurrent.thread

/**
 *  Create by Logan at 2018/12/21 0021
 *
 */
class QRCodeFragment :Fragment() {
    private lateinit var rootView: View
    private lateinit var qrcode: ImageView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_qrcode, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        qrcode = rootView.findViewById(R.id.qrcode)
        val content = "上面我们创建的TabLayout的下面的标识线是粉色的，这个颜色采用的是应用的Meterial Design主题中的强调类型颜色"
        val context =context?:return
        val size =IdentifyUtil.dp2px(context,250f)
        thread {
            val qrCode = QRCodeEncoder.syncEncodeQRCode(content, size)
            activity?.runOnUiThread {  qrcode.setImageBitmap(qrCode) }
        }

    }
}