package com.example.baseappandroid.data.model

data class MovieShowTime(
    val id: String?,
    val startTime: String?,
    val endTime: String?,
    val date: String?,
    val roomId: String?,
    val movieId: String?,
    val endDate: String?

) {
    constructor() : this(null, null, null, null, null, null, null)

    fun convertToMap(): HashMap<String, Any?> {
        return hashMapOf(
            "id" to id,
            "startTime" to startTime,
            "endTime" to endTime,
            "roomId" to roomId,
            "date" to date,
            "endDate" to endDate,
            "movieId" to movieId,
        )
    }
}
