package com.example.baseappandroid.data.model

import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseViewData

data class SnackItemModel(
    val id: String?,
    val image: String?,
    val name: String?,
    val price: Double?,
    val description: String?,
    val chosenCount: Int = 0,
    val interact: SnackItemInteract? = null,
    val type: SnackItemType = SnackItemType.CLIENT
) : BaseViewData {

    constructor() : this(null, null, null, null, null, 0)

    override val layoutRes: Int
        get() = R.layout.layout_snack_item

    override fun areItemsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is SnackItemModel) false
        else newItem.id == id
    }

    override fun areContentsTheSame(newItem: BaseViewData): Boolean {
        return if (newItem !is SnackItemModel) false
        else newItem.image == image
                && newItem.name == name
                && newItem.price == price
                && newItem.description == description
                && newItem.chosenCount == chosenCount
                && newItem.type == type
    }

    fun toMapValue(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id.toString(),
            "image" to image.toString(),
            "name" to name.toString(),
            "price" to (price ?: 0.0),
            "description" to description.toString(),
            "name" to name.toString(),
        )
    }
}

enum class SnackItemType {
    ADMIN,
    CLIENT
}

interface SnackItemInteract {
    fun showSnackDetail(item: SnackItemModel)
    fun onPlus(id: String)
    fun onMinus(id: String)
    fun deleteSnack(id: String)
    fun editSnack(id: String)
}