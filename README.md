# qrcode-view
[![Apache License 2.0][1]][2]
[![Release Version][5]][6]
[![API][3]][4]
[![PRs Welcome][7]][8]
 * ### 介绍
    这是一个基于ZXing的二维码扫描框架,kotlin代码编写,提供View作为扫描控件,适用于Activity和Fragment,样式可自定义.
***
 * ### 依赖方式
   #### Gradle:
    在你的module的build.gradle文件

    ```gradle
    implementation 'com.log1992:qrcodelib:1.0.1'
    ```
    ### Maven:
    ```maven
   <dependency>
     <groupId>com.log1992</groupId>
     <artifactId>qrcodelib</artifactId>
     <version>1.0.1</version>
     <type>pom</type>
   </dependency>
    ```
    ### Lvy
    ```lvy
    <dependency org='com.log1992' name='qrcodelib' rev='1.0.1'>
      <artifact name='qrcodelib' ext='pom' ></artifact>
    </dependency>
    ```
    ###### 如果Gradle出现compile失败的情况，可以在Project的build.gradle里面添加如下仓库地址：
    ```gradle
    allprojects {
    repositories {
        maven {url 'https://dl.bintray.com/qq2519157/maven'}
     }
    }
    ```
    ***
 * ### 引入的库：
    ```gradle
    compileOnly 'com.android.support:appcompat-v7:27.1.1'
    api 'com.google.zxing:core:3.3.3'
    ```
 * ### 使用方法
    ```xml
    <com.sencent.qrcodelib.ScanView
                android:layout_height="match_parent"
                android:layout_width="wrap_content"
                app:layout_constraintTop_toBottomOf="@id/tv_notice"
                android:id="@+id/zxingview"
                app:qrcv_animTime="1000"
                app:qrcv_barCodeTipText="将条码放入框内，即可自动扫描"
                app:qrcv_barcodeRectHeight="120dp"
                app:qrcv_borderColor="@color/green"
                app:qrcv_borderSize="1dp"
                app:qrcv_cornerColor="@color/green"
                app:qrcv_cornerDisplayType="center"
                app:qrcv_cornerLength="20dp"
                app:qrcv_cornerSize="3dp"
                app:qrcv_isAutoZoom="true"
                app:qrcv_isBarcode="false"
                app:qrcv_isOnlyDecodeScanBoxArea="false"
                app:qrcv_isScanLineReverse="true"
                app:qrcv_isShowDefaultGridScanLineDrawable="false"
                app:qrcv_isShowDefaultScanLineDrawable="true"
                app:qrcv_isShowLocationPoint="true"
                app:qrcv_isShowTipBackground="true"
                app:qrcv_isShowTipTextAsSingleLine="false"
                app:qrcv_isTipTextBelowRect="true"
                app:qrcv_maskColor="#60000000"
                app:qrcv_qrCodeTipText="@string/tips_scan_code"
                app:qrcv_rectWidth="200dp"
                app:qrcv_scanLineColor="@color/green"
                app:qrcv_scanLineMargin="0dp"
                app:qrcv_scanLineSize="0.5dp"
                app:qrcv_tipTextColor="@color/green"
                app:qrcv_tipTextSize="12sp"
                app:qrcv_toolbarHeight="56dp"
                app:qrcv_topOffset="65dp"
                app:qrcv_verticalBias="-1"/>
    ```
   ## 自定义属性说明
   
   属性名 | 说明 | 默认值
   :----------- | :----------- | :-----------
   qrcv_topOffset         | 扫描框距离 toolbar 底部的距离        | 90dp
   qrcv_cornerSize         | 扫描框边角线的宽度        | 3dp
   qrcv_cornerLength         | 扫描框边角线的长度        | 20dp
   qrcv_cornerColor         | 扫描框边角线的颜色        | @android:color/white
   qrcv_cornerDisplayType         | 扫描框边角线显示位置(相对于边框)，默认值为中间        | center
   qrcv_rectWidth         | 扫描框的宽度        | 200dp
   qrcv_barcodeRectHeight         | 条码扫样式描框的高度        | 140dp
   qrcv_maskColor         | 除去扫描框，其余部分阴影颜色        | #33FFFFFF
   qrcv_scanLineSize         | 扫描线的宽度        | 1dp
   qrcv_scanLineColor         | 扫描线的颜色「扫描线和默认的扫描线图片的颜色」        | @android:color/white
   qrcv_scanLineMargin         | 扫描线距离上下或者左右边框的间距        | 0dp
   qrcv_isShowDefaultScanLineDrawable         | 是否显示默认的图片扫描线「设置该属性后 qrcv_scanLineSize 将失效，可以通过 qrcv_scanLineColor 设置扫描线的颜色，避免让你公司的UI单独给你出特定颜色的扫描线图片」        | false
   qrcv_customScanLineDrawable         | 扫描线的图片资源「默认的扫描线图片样式不能满足你的需求时使用，设置该属性后 qrcv_isShowDefaultScanLineDrawable、qrcv_scanLineSize、qrcv_scanLineColor 将失效」        | null
   qrcv_borderSize         | 扫描边框的宽度        | 1dp
   qrcv_borderColor         | 扫描边框的颜色        | @android:color/white
   qrcv_animTime         | 扫描线从顶部移动到底部的动画时间「单位为毫秒」        | 1000
   qrcv_isCenterVertical（已废弃，如果要垂直居中用 qrcv_verticalBias="0.5"来代替）         | 扫描框是否垂直居中，该属性为true时会忽略 qrcv_topOffset 属性        | false
   qrcv_verticalBias         | 扫描框中心点在屏幕垂直方向的比例，当设置此值时，会忽略 qrcv_topOffset 属性        | -1
   qrcv_toolbarHeight         | Toolbar 的高度，通过该属性来修正由 Toolbar 导致扫描框在垂直方向上的偏差        | 0dp
   qrcv_isBarcode         | 扫描框的样式是否为扫条形码样式        | false
   qrcv_tipText         | 提示文案        | null
   qrcv_tipTextSize         | 提示文案字体大小        | 14sp
   qrcv_tipTextColor         | 提示文案颜色        | @android:color/white
   qrcv_isTipTextBelowRect         | 提示文案是否在扫描框的底部        | false
   qrcv_tipTextMargin         | 提示文案与扫描框之间的间距        | 20dp
   qrcv_isShowTipTextAsSingleLine         | 是否把提示文案作为单行显示        | false
   qrcv_isShowTipBackground         | 是否显示提示文案的背景        | false
   qrcv_tipBackgroundColor         | 提示文案的背景色        | #22000000
   qrcv_isScanLineReverse         | 扫描线是否来回移动        | true
   qrcv_isShowDefaultGridScanLineDrawable         | 是否显示默认的网格图片扫描线        | false
   qrcv_customGridScanLineDrawable         | 扫描线的网格图片资源        | nulll
   qrcv_isOnlyDecodeScanBoxArea         | 是否只识别扫描框中的码        | false
   qrcv_isShowLocationPoint         | 是否显示定位点        | false
   qrcv_isAutoZoom         | 码太小时是否自动缩放        | false

    ##### 独立Activity/Fragment中使用
    ```
    class TestActivity : AppCompatActivity(),QRCodeView.Delegate{
         override fun onScanQRCodeOpenCameraError() {

            }

         override fun onScanQRCodeSuccess(result: String?) {

            }

         override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {

            }

        override fun onResume() {
            captureView?.onResume()
            super.onResume()
            }

        override fun onPause() {
            captureView?.onPause()
            super.onPause()
            }

         override fun onDestroy() {
            captureView?.onDestroy()
            super.onDestroy()
            }
    }
    ```

    ##### viewpager嵌套Fragment使用
    ```
        class ScanFragment : Fragment(), ScanListener {
            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
              mZXingView?.setDelegate(this)
               mZXingView?.startCamera() // 打开后置摄像头开始预览，但是并未开始识别
                mZXingView?.startSpotAndShowRect() // 显示扫描框，并开始识别
                   }

            override fun onDestroy() {
                   mZXingView?.onDestroy() // 销毁二维码扫描控件
                   mZXingView?.stopSpot()
                   super.onDestroy()
               }

            override fun setUserVisibleHint(isVisibleToUser: Boolean) {
                 super.setUserVisibleHint(isVisibleToUser)
                    if (isVisibleToUser) {
                   // 打开后置摄像头开始预览，但是并未开始识别
                       mZXingView?.resumeCamera()
                     } else {
                      mZXingView?.pauseCamera()
                    }
                }

            override fun onScanQRCodeOpenCameraError() {
            
                        }
            
            override fun onScanQRCodeSuccess(result: String?) {
            
                        }
            
            override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {
            
                        }
        }
    ```
 * ### 注意事项
    * 必须实现QRCodeView.Delegate 接口,回调来处理扫描的结果
    * 需要自己处理相机权限,特别是6.0+动态权限处理
    
 * ### 感谢
    [bingoogolapple](https://github.com/bingoogolapple)的[BGAQRCode-Android](https://github.com/bingoogolapple/BGAQRCode-Android)

    [ZXing](https://github.com/zxing/zxing)

[1]:https://img.shields.io/:license-apache-blue.svg
[2]:https://www.apache.org/licenses/LICENSE-2.0.html
[3]:https://img.shields.io/badge/API-15%2B-red.svg?style=flat
[4]:https://android-arsenal.com/api?level=15
[5]:https://img.shields.io/badge/release-0.0.1-red.svg
[6]:https://github.com/qq2519157/qrcode-view/releases
[7]:https://img.shields.io/badge/PRs-welcome-brightgreen.svg
[8]:https://github.com/qq2519157/qrcode-view/pulls