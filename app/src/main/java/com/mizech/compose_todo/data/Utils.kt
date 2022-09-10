package com.mizech.compose_todo.data

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    fun createDateTimeStr(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy, HH:mm")
        val oDate = Date(timestamp)
        return sdf.format(oDate)
    }

    public val maxTitleLength = 150
    public val maxNoteLength = 400
}