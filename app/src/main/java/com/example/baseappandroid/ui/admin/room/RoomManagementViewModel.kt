package com.example.baseappandroid.ui.admin.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baseappandroid.base.viewmodel.BaseViewModel
import com.example.baseappandroid.data.model.Result
import com.example.baseappandroid.data.model.RoomItemModel
import com.example.baseappandroid.data.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomManagementViewModel @Inject constructor(
    private val roomRepository: RoomRepository
) : BaseViewModel() {
    var shouldCallFetchData = false
    private var _listOfRooms = MutableLiveData<List<RoomItemModel>>()
    val listOfRooms: LiveData<List<RoomItemModel>>
        get() = _listOfRooms

    private var _addRoomResponse = MutableLiveData<Boolean?>()
    val addRoomResponse: LiveData<Boolean?>
        get() = _addRoomResponse

    private var _editRoomResponse = MutableLiveData<Boolean?>()
    val editRoomResponse: LiveData<Boolean?>
        get() = _editRoomResponse

    private var _onRoomClickAction = MutableLiveData<RoomItemModel?>()
    val onRoomClickAction: LiveData<RoomItemModel?>
        get() = _onRoomClickAction

    fun clearCLickRoomAction() {
        _onRoomClickAction.value = null
    }

    init {
        fetchRooms()
    }

     fun fetchRooms() {
        viewModelScope.launch {
            when (val result = roomRepository.fetchAllRooms()) {
                is Result.Success -> {
                    _listOfRooms.value = result.data.map { e -> e.copy(interact = this@RoomManagementViewModel) }
                }

                is Result.Error -> {
                    // handle error here
                }
            }
        }
    }

    fun addRoom(room: RoomItemModel) {
        viewModelScope.launch {
            when (roomRepository.addRoom(room)) {
                is Result.Success -> {
                    fetchRooms()
                    _addRoomResponse.value = true
                }

                is Result.Error -> {
                    // handle error here
                }
            }
        }
    }

    fun updateRoom(room: RoomItemModel) {
        viewModelScope.launch {
            when (roomRepository.updateRoom(room)) {
                is Result.Success -> {
                    _editRoomResponse.value = true
                }

                is Result.Error -> {
                    // handle error here
                }
            }
        }
    }

    fun deleteRoom(room: String) {
        viewModelScope.launch {
            when (roomRepository.deleteRoom(room)) {
                is Result.Success -> {
                    _editRoomResponse.value = true
                }

                is Result.Error -> {
                    // handle error here
                }
            }
        }
    }

    override fun onRoomItemClick(id: RoomItemModel) {
        _onRoomClickAction.value = id
    }
}