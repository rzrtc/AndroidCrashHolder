package com.rz.crashreport

import android.os.Build

fun serialNo(): String {
    var serial = ""
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {//9.0+
            serial = Build.getSerial()
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {//8.0+
            serial = Build.SERIAL;
        } else {//8.0-
            val c = Class.forName("android.os.SystemProperties")
            val get = c.getMethod("get", String::class.java)
            get.invoke(c, "ro.serialno") as String
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return serial
}