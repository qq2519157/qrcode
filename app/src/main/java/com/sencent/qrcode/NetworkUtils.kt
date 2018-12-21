package com.sencent.qrcode

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException

/**
 * Created by liyuan on 16/10/14.
 */
object NetworkUtils {
    /**
     * Current network is eHRPD
     */
    val NETWORK_TYPE_EHRPD = 14
    /**
     * Network type is unknown
     */
    val NETWORK_TYPE_UNKNOWN = 0
    /**
     * Current network is GPRS
     */
    val NETWORK_TYPE_GPRS = 1
    /**
     * Current network is EDGE
     */
    val NETWORK_TYPE_EDGE = 2
    /**
     * Current network is UMTS
     */
    val NETWORK_TYPE_UMTS = 3
    /**
     * Current network is CDMA: Either IS95A or IS95B
     */
    val NETWORK_TYPE_CDMA = 4
    /**
     * Current network is EVDO revision 0
     */
    val NETWORK_TYPE_EVDO_0 = 5
    /**
     * Current network is EVDO revision A
     */
    val NETWORK_TYPE_EVDO_A = 6
    /**
     * Current network is 1xRTT
     */
    val NETWORK_TYPE_1xRTT = 7
    /**
     * Current network is HSDPA
     */
    val NETWORK_TYPE_HSDPA = 8
    /**
     * Current network is HSUPA
     */
    val NETWORK_TYPE_HSUPA = 9
    /**
     * Current network is HSPA
     */
    val NETWORK_TYPE_HSPA = 10
    /**
     * Current network is iDen
     */
    val NETWORK_TYPE_IDEN = 11
    /**
     * Current network is EVDO revision B
     */
    val NETWORK_TYPE_EVDO_B = 12
    /**
     * Current network is LTE
     */
    val NETWORK_TYPE_LTE = 13
    /**
     * Current network is HSPA+
     */
    val NETWORK_TYPE_HSPAP = 15
    private val NETWORK_TYPE_UNAVAILABLE = -1
    // private static final int NETWORK_TYPE_MOBILE = -100;
    private val NETWORK_TYPE_WIFI = -101
    private val NETWORK_CLASS_WIFI = -101
    private val NETWORK_CLASS_UNAVAILABLE = -1
    /**
     * Unknown network class.
     */
    private val NETWORK_CLASS_UNKNOWN = 0
    /**
     * Class of broadly defined "2G" networks.
     */
    private val NETWORK_CLASS_2_G = 1
    /**
     * Class of broadly defined "3G" networks.
     */
    private val NETWORK_CLASS_3_G = 2
    /**
     * Class of broadly defined "4G" networks.
     */
    private val NETWORK_CLASS_4_G = 3

    //判断wifi是否可用
    fun isWifiAvailable(context: Context): Boolean {
        val connectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected && networkInfo
            .type == ConnectivityManager.TYPE_WIFI
    }

    //判断网络是否可用
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isAvailable
    }

    /**
     * 获取网络类型
     *
     * @return
     */
    fun getCurrentNetworkType(context: Context): String {
        val networkClass = getNetworkClass(context)
        var type = "unknown"
        when (networkClass) {
            NETWORK_CLASS_UNAVAILABLE -> type = "none"
            NETWORK_CLASS_WIFI -> type = "wifi"
            NETWORK_CLASS_2_G -> type = "2G"
            NETWORK_CLASS_3_G -> type = "3G"
            NETWORK_CLASS_4_G -> type = "4G"
            NETWORK_CLASS_UNKNOWN -> type = "unknown"
        }
        return type
    }

    private fun getNetworkClass(context: Context): Int {
        var networkType = NETWORK_TYPE_UNKNOWN
        try {
            val network = (context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
            if (network != null && network.isAvailable && network.isConnected) {
                val type = network.type
                if (type == ConnectivityManager.TYPE_WIFI) {
                    networkType = NETWORK_TYPE_WIFI
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    val telephonyManager = context.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    networkType = telephonyManager.networkType
                }
            } else {
                networkType = NETWORK_TYPE_UNAVAILABLE
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return getNetworkClassByType(networkType)
    }

    private fun getNetworkClassByType(networkType: Int): Int {
        when (networkType) {
            NETWORK_TYPE_UNAVAILABLE -> return NETWORK_CLASS_UNAVAILABLE
            NETWORK_TYPE_WIFI -> return NETWORK_CLASS_WIFI
            NETWORK_TYPE_GPRS, NETWORK_TYPE_EDGE, NETWORK_TYPE_CDMA, NETWORK_TYPE_1xRTT, NETWORK_TYPE_IDEN -> return NETWORK_CLASS_2_G
            NETWORK_TYPE_UMTS, NETWORK_TYPE_EVDO_0, NETWORK_TYPE_EVDO_A, NETWORK_TYPE_HSDPA, NETWORK_TYPE_HSUPA, NETWORK_TYPE_HSPA, NETWORK_TYPE_EVDO_B, NETWORK_TYPE_EHRPD, NETWORK_TYPE_HSPAP -> return NETWORK_CLASS_3_G
            NETWORK_TYPE_LTE -> return NETWORK_CLASS_4_G
            else -> return NETWORK_CLASS_UNKNOWN
        }
    }

    fun getIPAddress(context: Context): String? {
        val info = (context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        if (info != null && info.isConnected) {
            if (info.type == ConnectivityManager.TYPE_MOBILE) { //当前使用2G/3G/4G网络
                try {
                    val en = NetworkInterface.getNetworkInterfaces()
                    while (en.hasMoreElements()) {
                        val intf = en.nextElement()
                        val enumIpAddr = intf.inetAddresses
                        while (enumIpAddr.hasMoreElements()) {
                            val inetAddress = enumIpAddr.nextElement()
                            if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                                return inetAddress.getHostAddress()
                            }
                        }
                    }
                } catch (e: SocketException) {
                    e.printStackTrace()
                }
            } else if (info.type == ConnectivityManager.TYPE_WIFI) { //当前使用无线网络
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                return intIP2StringIP(wifiInfo.ipAddress)
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return ""
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    private fun intIP2StringIP(ip: Int): String {
        return (ip and 0xFF).toString() + "." +
                (ip shr 8 and 0xFF) + "." +
                (ip shr 16 and 0xFF) + "." +
                (ip shr 24 and 0xFF)
    }
}