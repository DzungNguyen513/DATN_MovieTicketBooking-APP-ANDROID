package com.example.baseappandroid.data.model.view_data_item

import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseViewData
import com.example.baseappandroid.data.model.arg.NavigateToBookTicketModel


data class WeeklyMovieItemModel(
    val id: String?,
    val movieId: String?,
    val date: String?,
    val times: List<String>?,
    val timesModel: List<WeeklyShowTimeItemModel>?,
    val interact: WeeklyMovieItemInteract?,
    val movie: MovieItemModel?,
    val movieInteractType: MovieInteractType? = MovieInteractType.CLIENT
) : BaseViewData {

    constructor() : this(null, null, null, null, null, null, null)

    override val layoutRes: Int
        get() = R.layout.layout_weekly_movie_item

    override fun areItemsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is WeeklyMovieItemModel) false
        else newItem.id == id
    }

    override fun areContentsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is WeeklyMovieItemModel) false
        else newItem.movieId == movieId
                && newItem.date == date
                && newItem.times == times
    }

    fun convertToMap(): HashMap<String, Any?> {
        return hashMapOf(
            "id" to id,
            "movieId" to movieId,
            "date" to date,
            "times" to times
        )
    }

}

interface WeeklyMovieItemInteract {
    fun chooseMovie(movieArg: NavigateToBookTicketModel)
    fun deleteMovieShowTimeAction(id: String)
    fun editMovieShowTimeAction(time: String, id: String)
}

data class WeeklyShowTimeItemModel(
    val id: Int,
    val time: String,
    val weeklyItemId: String,
    val interact: WeeklyMovieShowTimeItemInteract
) : BaseViewData {
    override val layoutRes: Int
        get() = R.layout.layout_weekly_movie_show_time_item

    override fun areItemsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is WeeklyShowTimeItemModel) false
        else newItem.id == id
    }

    override fun areContentsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is WeeklyShowTimeItemModel) false
        else newItem.time == time
    }
}

interface WeeklyMovieShowTimeItemInteract {
    fun chooseTime(time: WeeklyShowTimeItemModel)
}

enum class MovieInteractType {
    ADMIN,
    CLIENT
}