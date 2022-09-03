package com.udacity.project4.ui.fragment.addreminder

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.project4.model.Reminder
import com.udacity.project4.ui.fragment.base.BaseFragmentViewModel
import com.udacity.project4.R
import com.udacity.project4.data.source.ReminderDataSource
import kotlinx.coroutines.launch

class AddReminderViewModel(val app: Application, val dataSource: ReminderDataSource) :
    BaseFragmentViewModel(app) {
    var reminder = Reminder("", "", "", 0.0, 0.0)
    private val _eventGoToMap = MutableLiveData(false)
    val eventGoToMap: LiveData<Boolean> get() = _eventGoToMap
    private val _eventGoToList = MutableLiveData(false)
    val eventGoToList: LiveData<Boolean> get() = _eventGoToList


    fun goToMapDestination() {
        _eventGoToMap.value = true
    }

    fun completeGoToMapDestination() {
        _eventGoToMap.value = false
    }


    fun completeGoToList() {
        _eventGoToMap.value = false
    }


    fun validateAndSaveReminder(): Boolean {

        return if (validateEnteredData()) {
            saveReminder()
            true
        } else
            false
    }

    private fun saveReminder() {

        showLoading.postValue(true)
        viewModelScope.launch {
            dataSource.saveReminder(updateReminder())
            showLoading.value = false
//            showSnackBarInt.value = R.string.reminder_saved
//            showToast.value = app.getString(R.string.reminder_saved)
//            Log.d("dapgoo", "saveReminder: ")
//            Log.d("dapgoo", "saveReminder:${showToast.value}")
//            Log.d("dapgoo", "saveReminder2:${showSnackBarInt.value}")
            _eventGoToList.value = true

        }
    }

    private fun validateEnteredData(): Boolean {

        if (reminder.location.isNullOrEmpty() || reminder.location.equals("")) {
            showSnackBarInt.value = R.string.error_select_location
            return false
        }
        if (reminder.title.isNullOrEmpty() || reminder.title.equals("")) {
            showSnackBarInt.value = R.string.err_enter_title
            return false
        }


        return true
    }

    fun set(reminder: Reminder) {

        this.reminder = reminder

    }

    fun updateReminder(): Reminder {

        return this.reminder
    }

    fun setTitle(title: String) {
        this.reminder.title = title


    }

    fun setDescription(des: String) {
        this.reminder.description = des


    }

    fun setLocation(location: String) {
        this.reminder.location = location


    }


}