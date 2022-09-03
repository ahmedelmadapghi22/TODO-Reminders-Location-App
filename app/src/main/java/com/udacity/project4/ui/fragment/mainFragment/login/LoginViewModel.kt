package com.udacity.project4.ui.fragment.mainFragment.login

import androidx.lifecycle.LiveData
import  androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    val authenticationState: LiveData<AuthenticationState> = map(FirebaseUserLiveData()) {
        if (it != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }
}

