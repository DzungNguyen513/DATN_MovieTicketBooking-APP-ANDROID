package com.example.baseappandroid.data.model.view_data_item

import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseViewData
import com.google.firebase.firestore.PropertyName

data class MovieItemModel(
    val id: String?,
    val title: String?,
    val images: List<String>?,
    val genre: List<String>?,
    @get:PropertyName("age_limit")
    @PropertyName("age_limit")
    val ageLimit: Int?,
    @get:PropertyName("poster_url")
    @PropertyName("poster_url")
    val posterUrl: String?,
    val description: String?,
    val rating: Double?,
    val duration: Long?,
    @get:PropertyName("release_date")
    @PropertyName("release_date")
    val releaseDate: String?,
    val interact: MovieModelItemInteract?,
    val movieType: MovieType?,
    val type: Int?,
) : BaseViewData {

    constructor() : this(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    )

    override val layoutRes: Int
        get() = when (movieType) {
            MovieType.newMovie -> R.layout.layout_movie_item
            MovieType.popular -> R.layout.layout_movie_popular_item
            MovieType.comingSoon -> R.layout.layout_movie_coming_soon_item
            MovieType.search -> R.layout.layout_search_movie_item
            MovieType.admin -> R.layout.layout_admin_movie_item
            else -> R.layout.layout_movie_item
        }

    override fun areItemsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is MovieItemModel) false
        else newItem.id == id
    }

    override fun areContentsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is MovieItemModel) false
        else newItem.title == title
                && newItem.images == images
                && newItem.description == description
                && newItem.movieType == movieType
                && newItem.genre == genre
                && newItem.ageLimit == ageLimit
                && newItem.posterUrl == posterUrl
                && newItem.rating == rating
                && newItem.duration == duration
                && newItem.releaseDate == releaseDate
                && newItem.type == type
    }
}

fun MovieItemModel.toMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "title" to title,
        "images" to images,
        "genre" to genre,
        "age_limit" to ageLimit,
        "poster_url" to posterUrl,
        "description" to description,
        "rating" to rating,
        "duration" to duration,
        "release_date" to releaseDate,
        "type" to type
    )
}

enum class MovieType(val num: Int) {
    popular(1),
    newMovie(3),
    comingSoon(2),
    search(0),
    admin(4),
}

interface MovieModelItemInteract {
    fun clickOnMovieAction(value: String?)
    fun deleteMovieAction(value: String?)
    fun editMovieAction(value: String?)
}