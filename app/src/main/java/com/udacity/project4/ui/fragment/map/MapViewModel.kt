package com.udacity.project4.ui.fragment.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class MapViewModel : ViewModel() {
    private val _eventGoToAddReminder = MutableLiveData(false)
    val eventGoToAddReminder: LiveData<Boolean> get() = _eventGoToAddReminder
    private var _stateOfLocation = MutableLiveData<LatLng?>()
    val stateOfLocation: LiveData<LatLng?> get() = _stateOfLocation

    fun setLocation(latLng: LatLng?) {
        _stateOfLocation.value = latLng
    }


    fun goToAddReminderDestination() {
        _eventGoToAddReminder.value = true
    }

    fun completeGoToAddReminderDestination() {
        _eventGoToAddReminder.value = false
    }

}