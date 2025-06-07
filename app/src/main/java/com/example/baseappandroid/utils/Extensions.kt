package com.example.baseappandroid.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

object Extensions {

    fun String.convertListToString(): String {
        return this.replace("[", "").replace("]", "").replace(",", ", ")
    }

    fun Int.formatMoney(): String {
        val longValue = this.toLong()
        val formatter =
            NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter.applyPattern("#,###,###,###")
        val formattedString = formatter.format(longValue)
        return "${formattedString.replace(",", ".")}đ"
    }

    fun Double.formatMoney(): String {
        val symbols = DecimalFormatSymbols().apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        val formatter = DecimalFormat("#,###,###,###", symbols)
        return formatter.format(this) + " vnđ"
    }
}