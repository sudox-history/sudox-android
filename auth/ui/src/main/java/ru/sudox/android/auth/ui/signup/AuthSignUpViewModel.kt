package ru.sudox.android.auth.ui.signup

import ru.sudox.android.auth.data.repositories.AuthRepository
import ru.sudox.android.auth.ui.AuthViewModel
import ru.sudox.android.core.livedata.SingleLiveEvent
import javax.inject.Inject

class AuthSignUpViewModel @Inject constructor(
        private val authRepository: AuthRepository
) : AuthViewModel(true, authRepository, false) {

    val successLiveData = SingleLiveEvent<Unit>()

    /**
     * Регистрирует пользователя.
     * Также начинает сессию пользователя в случае успешной регистрации.
     * Результат возвращает в соответствующие LiveData
     *
     * @param name Имя пользователя
     * @param nickname Никнейм пользователя
     */
    fun signUp(name: String, nickname: String) {
        loadingStateLiveData.postValue(true)

        compositeDisposable.add(doRequest(authRepository.signUp(name, nickname).flatMap {
            authRepository.createUserSession()
        }).subscribe({
            successLiveData.postValue(null)
            loadingStateLiveData.postValue(false)
        }, {
            errorsLiveData.postValue(it)
            loadingStateLiveData.postValue(false)
        }))
    }
}