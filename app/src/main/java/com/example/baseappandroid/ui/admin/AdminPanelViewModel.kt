package com.example.baseappandroid.ui.admin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseappandroid.base.viewmodel.BaseViewModel
import com.example.baseappandroid.data.model.view_data_item.AdminFeature
import com.example.baseappandroid.data.model.view_data_item.AdminFeatureModel
import com.example.baseappandroid.data.model.view_data_item.INCOME_MANAGEMENT_ICON
import com.example.baseappandroid.data.model.view_data_item.MOVIE_MANAGEMENT_ICON
import com.example.baseappandroid.data.model.view_data_item.ROOM_MANAGEMENT_ICON
import com.example.baseappandroid.data.model.view_data_item.SHOW_TIME_MANAGEMENT_ICON
import com.example.baseappandroid.data.model.view_data_item.SNACK_MANAGEMENT_ICON
import com.example.baseappandroid.data.model.view_data_item.TICKET_MANAGEMENT_ICON
import com.example.baseappandroid.utils.GlobalData
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AdminPanelViewModel : BaseViewModel() {
    private var _listOfFeatures = MutableLiveData<List<AdminFeatureModel>>()
    val listOfFeatures: LiveData<List<AdminFeatureModel>>
        get() = _listOfFeatures

    private var _actionFeature = MutableLiveData<AdminFeature?>()
    val actionFeature: LiveData<AdminFeature?>
        get() = _actionFeature

    fun clearActionFeature() {
        _actionFeature.value = null
    }

    init {
        getFeatures()
    }

    private fun getFeatures() {
        viewModelScope.launch {
            val list = arrayListOf<AdminFeatureModel>()
            list.add(
                AdminFeatureModel(
                    id = 1,
                    feature = AdminFeature.MOVIE_MANAGEMENT,
                    interact = this@AdminPanelViewModel,
                    image = MOVIE_MANAGEMENT_ICON
                )
            )
            list.add(
                AdminFeatureModel(
                    id = 2,
                    feature = AdminFeature.SHOW_TIME_MANAGEMENT,
                    interact = this@AdminPanelViewModel,
                    image = SHOW_TIME_MANAGEMENT_ICON
                )
            )
            list.add(
                AdminFeatureModel(
                    id = 3,
                    feature = AdminFeature.ROOM_MANAGEMENT,
                    interact = this@AdminPanelViewModel,
                    image = ROOM_MANAGEMENT_ICON
                )
            )
            list.add(
                AdminFeatureModel(
                    id = 4,
                    feature = AdminFeature.TICKET_MANAGEMENT,
                    interact = this@AdminPanelViewModel,
                    image = TICKET_MANAGEMENT_ICON
                )
            )
            list.add(
                AdminFeatureModel(
                    id = 5,
                    feature = AdminFeature.INCOME_MANAGEMENT,
                    interact = this@AdminPanelViewModel,
                    image = INCOME_MANAGEMENT_ICON
                )
            )
            list.add(
                AdminFeatureModel(
                    id = 6,
                    feature = AdminFeature.SNACK_MANAGEMENT,
                    interact = this@AdminPanelViewModel,
                    image = SNACK_MANAGEMENT_ICON
                )
            )
            _listOfFeatures.value = list
        }
    }

    override fun onFeatureClick(feature: AdminFeature) {
        _actionFeature.value = feature
    }
}