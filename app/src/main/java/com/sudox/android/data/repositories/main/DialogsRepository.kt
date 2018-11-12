package com.sudox.android.data.repositories.main

import com.sudox.android.data.database.dao.UserDao
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.protocol.ProtocolClient
import javax.inject.Inject

class DialogsRepository @Inject constructor(val protocolClient: ProtocolClient,
                                            private val authRepository: AuthRepository,
                                            private val usersRepository: UsersRepository,
                                            private val userDao: UserDao) {
}