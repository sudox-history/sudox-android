package ru.sudox.android.auth.ui.verify

import androidx.lifecycle.MutableLiveData
import ru.sudox.android.auth.data.repositories.AuthRepository
import ru.sudox.android.auth.ui.AuthViewModel
import javax.inject.Inject

class AuthVerifyViewModel @Inject constructor(
        private val authRepository: AuthRepository
) : AuthViewModel(true, authRepository, false) {

    val successLiveData = MutableLiveData<Unit>()

    init {
        compositeDisposable.add(authRepository.listenRespondAuthVerify().subscribe {
            successLiveData.postValue(null)
        })
    }
}