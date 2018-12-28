package com.sudox.android.data.repositories

import com.sudox.android.data.repositories.auth.AccountRepository
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.main.ContactsRepository
import com.sudox.android.data.repositories.main.UsersRepository
import com.sudox.android.data.repositories.messages.chats.ChatMessagesRepository
import com.sudox.android.data.repositories.messages.chats.DialogsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoriesContainer @Inject constructor(val accountRepository: AccountRepository,
                                                val authRepository: AuthRepository,
                                                val contactsRepository: ContactsRepository,
                                                val usersRepository: UsersRepository,
                                                val chatMessagesRepository: ChatMessagesRepository,
                                                val dialogsRepository: DialogsRepository) {

    init {
        // Репозитории начнут слушать события после создания этого обьекта.

        // Запуск костыля для избежания циклического инжекта
        authRepository.init(usersRepository)
    }
}