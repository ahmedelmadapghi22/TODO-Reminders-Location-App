package com.udacity.project4.ui.fragment.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.udacity.project4.util.SingleLiveEvent

open class BaseFragmentViewModel(app: Application) : AndroidViewModel(app) {
    val showErrorMessage: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBarInt: SingleLiveEvent<Int> = SingleLiveEvent()
    var showToast: SingleLiveEvent<String> = SingleLiveEvent()
    val showLoading: MutableLiveData<Boolean> = MutableLiveData()
    val showNoData: MutableLiveData<Boolean> = MutableLiveData()


}