package ru.sudox.android.auth.ui.code

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import ru.sudox.android.auth.data.repositories.AuthRepository
import ru.sudox.android.auth.ui.AuthViewModel
import ru.sudox.android.core.livedata.SingleLiveEvent
import ru.sudox.android.countries.helpers.formatPhoneNumber
import javax.inject.Inject

class AuthCodeViewModel @Inject constructor(
        private val authRepository: AuthRepository,
        private val phoneNumberUtil: PhoneNumberUtil
) : AuthViewModel(true, authRepository, false) {

    val successLiveData = SingleLiveEvent<Boolean>()

    /**
     * Подтверждает сессию пользователя кодом.
     * Результаты возвращает в соответствующие LiveData.
     *
     * @param code Код подтверждения
     */
    fun checkCode(code: Int) {
        loadingStateLiveData.postValue(true)

        compositeDisposable.add(doRequest(authRepository.checkCode(code)).subscribe({
            successLiveData.postValue(it)
            errorsLiveData.postValue(null)
            loadingStateLiveData.postValue(false)
        }, {
            errorsLiveData.postValue(it)
            successLiveData.postValue(null)
            loadingStateLiveData.postValue(false)
        }))
    }

    /**
     * Создает ViewObject для экрана.
     */
    fun createViewObject(): AuthCodeScreenVO {
        val session = authRepository.currentSession!!
        val phone = phoneNumberUtil.formatPhoneNumber(session.phoneNumber)

        return AuthCodeScreenVO(phone, session.userExists)
    }
}