package com.udacity.project4.ui.fragment.reminder

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.project4.model.Reminder
import com.udacity.project4.data.source.ReminderDataSource
import com.udacity.project4.data.source.Result
import com.udacity.project4.ui.fragment.base.BaseFragmentViewModel
import kotlinx.coroutines.launch

class RemindersViewModel(app: Application, private val dataSource: ReminderDataSource) :
    BaseFragmentViewModel(app) {
    private val _reminders = MutableLiveData<List<Reminder>>()
    val reminders: LiveData<List<Reminder>> get() = _reminders
    private val _eventGoToAddReminder = MutableLiveData(false)
    val eventGoToAddReminder: LiveData<Boolean> get() = _eventGoToAddReminder


    init {
        getReminders()
    }

     fun getReminders() {
        showLoading.value = true
        viewModelScope.launch {
            showLoading.value = false
            when (val result = dataSource.getReminders()) {
                is Result.Success -> {
                    val data = result.data
                    Log.d("dapgoo", "Success:${data?.size}")
                    data?.let {
                        _reminders.postValue(it)
                        showNoData.value = it.isEmpty()
                        Log.d("dapgoo", "Success:${showNoData.value}")
                    }

                }
                is Result.Error -> {
                    showSnackBar.value = result.message
                }

            }
        }


    }


    fun goToAddReminder() {
        _eventGoToAddReminder.value = true
    }

    fun completeGoToAddReminder() {
        _eventGoToAddReminder.value = false
    }

}