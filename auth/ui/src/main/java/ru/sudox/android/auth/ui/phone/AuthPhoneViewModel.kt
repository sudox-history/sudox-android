package ru.sudox.android.auth.ui.phone

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.sudox.android.core.livedata.SingleLiveEvent

class AuthPhoneViewModel : ViewModel() {

    val loadingLiveData = MutableLiveData<Boolean>()
    val successLiveData = SingleLiveEvent<Nothing>()
    val errorsLiveData = MutableLiveData<Int>()

    fun createSession(userPhone: String) {
        loadingLiveData.postValue(true)
    }
}