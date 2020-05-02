package ru.sudox.android.auth.ui

import androidx.lifecycle.MutableLiveData
import ru.sudox.android.auth.data.repositories.AuthRepository
import ru.sudox.android.core.CoreViewModel
import javax.inject.Inject

class AuthRequestViewModel @Inject constructor(
        private val authRepository: AuthRepository
) : CoreViewModel() {

    val newAuthLiveData = MutableLiveData<Unit>()

    init {
        compositeDisposable.add(authRepository.listenNewAuthSession().subscribe {
            newAuthLiveData.postValue(null)
        })
    }

    fun sendResponse(accept: Boolean) {
        compositeDisposable.add(authRepository.sendAuthResponse(accept).subscribe())
    }
}