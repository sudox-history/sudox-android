package com.sudox.android.ui.main.profile

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.main.UsersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    // LiveData с текущим пользователем (могут и обновления кстати прилетать)
    val userLiveData: MutableLiveData<User> = MutableLiveData()

    fun start() = GlobalScope.launch(Dispatchers.IO) {
        for (user in authRepository
                .currentUserChannel
                .openSubscription()) {

            if (user == null) {
                // Данных больше не будет, ибо аккаунт был удален из системы => можно освободить поток
                return@launch
            }

            userLiveData.postValue(user)
        }

        println()
    }

    override fun onCleared() {
//        userSubscription.cancel()
    }
}