package com.sudox.android.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface ContactsDao {

    @Insert(onConflict = REPLACE)
    fun insertContact(contact: Contact)

    @Query("delete from contacts_table where cid=:id")
    fun deleteContactById(id: Long)

    @Query("select * from contacts_table")
    fun getAllContacts() : LiveData<List<Contact>>
}