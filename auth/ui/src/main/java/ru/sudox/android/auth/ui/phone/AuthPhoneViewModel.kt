package ru.sudox.android.auth.ui.phone

import ru.sudox.android.auth.data.entities.AuthSessionEntity
import ru.sudox.android.auth.data.repositories.AuthRepository
import ru.sudox.android.auth.ui.AuthViewModel
import ru.sudox.android.core.livedata.SingleLiveEvent
import javax.inject.Inject

class AuthPhoneViewModel @Inject constructor(
        private val authRepository: AuthRepository
) : AuthViewModel(false, authRepository, true) {

    val successLiveData = SingleLiveEvent<AuthSessionEntity?>()

    /**
     * Создает сессию авторизации пользователя.
     * Результаты возвращает в соответствующие LiveData.
     *
     * @param phone Номер телефона пользователя.
     */
    fun createSession(phone: String) {
        loadingStateLiveData.postValue(true)

        compositeDisposable.add(doRequest(authRepository.createOrRestoreSession(phone)).subscribe({
            errorsLiveData.postValue(null)
            successLiveData.postValue(it)
            loadingStateLiveData.postValue(false)
        }, {
            errorsLiveData.postValue(it)
            successLiveData.postValue(null)
            loadingStateLiveData.postValue(false)
        }))
    }
}