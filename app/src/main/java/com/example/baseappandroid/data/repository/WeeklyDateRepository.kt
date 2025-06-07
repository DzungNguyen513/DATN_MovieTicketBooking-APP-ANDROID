package com.example.baseappandroid.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.baseappandroid.data.model.view_data_item.DateItemModel
import com.example.baseappandroid.di.DefaultDispatcher
import com.example.baseappandroid.utils.GlobalFunction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WeeklyDateRepository @Inject constructor(
    @DefaultDispatcher private val coroutineDispatcher: CoroutineDispatcher
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getWeeklyDates(): List<DateItemModel> = withContext(coroutineDispatcher) {
        val list = arrayListOf<DateItemModel>()
        val dates = GlobalFunction.getAllWeeklyDays()
        for (i in dates.indices) {
            if (i == 0) {
                list.add(
                    DateItemModel(
                        days = "H.Nay",
                        dates = dates[i].dayOfMonth.toString(),
                        chosen = true,
                        localDate = dates[i],
                        interact = null
                    )
                )
            } else {
                list.add(
                    DateItemModel(
                        days = GlobalFunction.mappingDayOfWeek(dates[i].dayOfWeek),
                        dates = dates[i].dayOfMonth.toString(),
                        chosen = false,
                        localDate = dates[i],
                        interact = null
                    )
                )
            }
        }
        return@withContext list
    }
}