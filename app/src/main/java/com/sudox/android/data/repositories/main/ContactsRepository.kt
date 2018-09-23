package com.sudox.android.data.repositories.main

import com.sudox.android.data.database.dao.ContactsDao
import com.sudox.protocol.ProtocolClient
import javax.inject.Inject

class ContactsRepository @Inject constructor(private val protocolClient: ProtocolClient,
                                             private val contactsDao: ContactsDao) {


}