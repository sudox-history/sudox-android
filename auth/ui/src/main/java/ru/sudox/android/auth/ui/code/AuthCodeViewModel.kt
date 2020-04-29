package ru.sudox.android.auth.ui.code

import androidx.lifecycle.MutableLiveData
import ru.sudox.android.auth.data.repositories.AuthRepository
import ru.sudox.android.core.CoreViewModel
import ru.sudox.android.core.livedata.SingleLiveEvent
import java.util.concurrent.Semaphore
import javax.inject.Inject

class AuthCodeViewModel @Inject constructor(
        private val authRepository: AuthRepository
) : CoreViewModel() {

    val errorsLiveData = MutableLiveData<Throwable>()
    val loadingStateLiveData = MutableLiveData<Boolean>()
    val sessionDestroyedLiveData = SingleLiveEvent<Unit>()
    val successLiveData = SingleLiveEvent<Boolean>()

    private val sessionDestroyedSemaphore = Semaphore(1)
    private var sessionDestroyed: Boolean = false

    init {
        compositeDisposable.add(authRepository.sessionDestroyedSubject.subscribe {
            sessionDestroyedSemaphore.acquire()

            if (!sessionDestroyed) {
                sessionDestroyed = true
                sessionDestroyedLiveData.postValue(null)
            }

            sessionDestroyedSemaphore.release()
        })

        compositeDisposable.add(authRepository.timerObservable!!.subscribe {
            sessionDestroyedSemaphore.acquire()

            if (!sessionDestroyed) {
                sessionDestroyed = true
                sessionDestroyedLiveData.postValue(null)
            }

            sessionDestroyedSemaphore.release()
        })
    }

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
}