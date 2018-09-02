package com.sudox.android.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.sudox.android.database.model.Contact

@Dao
interface ContactsDao {

    @Insert(onConflict = REPLACE)
    fun insertContact(contact: Contact)

    @Query("delete from contacts_table where cid=:id")
    fun deleteContactById(id: String)

    @Query("SELECT * FROM contacts_table")
    fun getContacts(): List<Contact>

    @Query("delete from contacts_table")
    fun deleteAllContacts()
}