package com.prplmnstr.drops.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.prplmnstr.drops.models.Date
import java.text.DateFormatSymbols
import java.util.Calendar

object Helper {
    @JvmStatic
    fun getDateInStringFormat(day: Int, month: Int, year: Int): String {
        val monthString = DateFormatSymbols().months[month - 1].substring(0, 3)
        return String.format("%d %s,%d", day, monthString, year)
    }

    @JvmStatic
    fun stringToBitmap(encodedString: String?): Bitmap {
        val decodedBytes = Base64.decode(encodedString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    @JvmStatic
    fun containsOnlyAlphabets(input: String): Boolean {
        // Regular expression to match alphabets
        val regex = "^[a-zA-Z]+$"

        // Check if the input matches the regular expression
        return input.matches(regex.toRegex())
    }

    @JvmStatic
    val todayDateObject: Date
        get() {
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH] + 1 // Month value is 0-based, so add 1
            val day = calendar[Calendar.DAY_OF_MONTH]
            val date = Date()
            val monthString = DateFormatSymbols().months[month - 1].substring(0, 3)
            val dateInStringFormat = String.format("%d %s,%d", day, monthString, year)
            date.day = day
            date.month = month
            date.year = year
            date.dateInStringFormat = dateInStringFormat
            return date
        }
}
