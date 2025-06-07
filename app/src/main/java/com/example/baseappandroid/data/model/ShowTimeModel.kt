package com.example.baseappandroid.data.model

data class ShowTimeModel(
    val times: List<ShowTimeItemModel>?
) {
    constructor() : this(null)
}

data class ShowTimeItemModel(
    val time: String?,
) {
    constructor() : this( null)
}
