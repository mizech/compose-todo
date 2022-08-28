package com.mizech.compose_todo

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class Utils {
    companion object {
        @SuppressLint("SimpleDateFormat")
        fun createDateTimeStr(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd.MM.yyyy, HH:mm")
            val oDate = Date(timestamp)
            return sdf.format(oDate)
        }
    }
}