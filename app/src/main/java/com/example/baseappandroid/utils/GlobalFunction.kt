package com.example.baseappandroid.utils

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.IOException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import kotlin.math.min

object GlobalFunction {

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT)
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun LocalDate.convertLocalDateToString(): String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return formatter.format(this)
    }

    fun addMinutesForTime(time: String, duration: Int, date: String): Calendar? {
        try {
            val calendar = Calendar.getInstance()
            val parts = time.split(":")
            val hour = parts[0].toInt()
            val minutes = parts[1].toInt()
            val partsDate = date.split("-")
            val day = partsDate[0].toInt()
            val month = partsDate[1].toInt()
            val year = partsDate[2].toInt()
            calendar.apply {
                clear()
                set(year, month - 1, day, hour, minutes)
            }
            calendar.add(Calendar.MINUTE, duration)
            return calendar
        } catch (_: Exception) {
            return null
        }
    }

    fun minusMinutesForTime(time: String, duration: Int, date: String): Calendar? {
        try {
            val calendar = Calendar.getInstance()
            val parts = time.split(":")
            val hour = parts[0].toInt()
            val minutes = parts[1].toInt()
            val partsDate = date.split("-")
            val day = partsDate[0].toInt()
            val month = partsDate[1].toInt()
            val year = partsDate[2].toInt()
            calendar.apply {
                clear()
                set(year, month, day, hour, minutes)
            }
            calendar.add(Calendar.MINUTE, -duration)
            return calendar
        } catch (_: Exception) {
            return null
        }
    }

    fun convertTimeAndDateIntoDateTime(time: String?, date: String?): Calendar? {
        try {
            val calendar = Calendar.getInstance()
            if (time == null || date == null) {
                return calendar
            }
            val parts = time.split(":")
            val hour = parts[0].toInt()
            val minutes = parts[1].toInt()

            val partsDate = date.split("-")
            val day = partsDate[0].toInt()
            val month = partsDate[1].toInt()
            val year = partsDate[2].toInt()

            calendar.apply {
                clear()
                set(year, month, day, hour, minutes)
            }
            return calendar
        } catch (_: Exception) {
            return null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllWeeklyDays(): List<LocalDate> {
        val listOfDates = arrayListOf<LocalDate>()
        val currentDate = LocalDate.now()
        for (i in 0..7) {
            listOfDates.add(currentDate.plusDays(i.toLong()))
        }
        return listOfDates
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun mappingDayOfWeek(day: DayOfWeek): String {
        return when (day) {
            DayOfWeek.MONDAY -> "Thứ 2"
            DayOfWeek.TUESDAY -> "Thứ 3"
            DayOfWeek.WEDNESDAY -> "Thứ 4"
            DayOfWeek.THURSDAY -> "Thứ 5"
            DayOfWeek.FRIDAY -> "Thứ 6"
            DayOfWeek.SATURDAY -> "Thứ 7"
            DayOfWeek.SUNDAY -> "C.Nhật"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertSystemMillisecondsIntoFormat(systemMilliseconds: Long): String {
        val localDate: LocalDateTime = Instant.ofEpochMilli(systemMilliseconds)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        val weekDay = mappingDayOfWeek(localDate.dayOfWeek)
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        return "${timeFormatter.format(localDate)}, $weekDay ${dateFormatter.format(localDate)}"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatString(currentFormat: String, targetFormat: String, input: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern(currentFormat, Locale.getDefault())
        val outputFormatter = DateTimeFormatter.ofPattern(targetFormat, Locale.getDefault())
        val date = LocalDate.parse(input, inputFormatter)
        return date.format(outputFormatter)
    }

    fun showDatePickerDialog(context: Context, setDate: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate =
                    String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                setDate(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    fun showTimePickerDialog(context: Context, setTime: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val formatted = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                setTime(formatted)
            },
            currentHour,
            currentMinute,
            android.text.format.DateFormat.is24HourFormat(context) // respects 24h setting
        )
        timePickerDialog.setTitle("Chọn thời gian chiếu")
        timePickerDialog.show()
    }

    fun showDeleteConfirmationDialog(
        title: String,
        message: String,
        id: String,
        context: Context,
        onDeleteAction: (Any?) -> Unit
    ) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(title)
        dialog.setMessage(message)
        dialog.setPositiveButton(
            "Xoá"
        ) { p0, _ ->
            onDeleteAction(id)
            p0.dismiss()
        }

        dialog.setNegativeButton(
            "Huỷ"
        ) { p0, _ -> p0.dismiss() }

        val alertDialog = dialog.create()
        alertDialog.show()
    }

}