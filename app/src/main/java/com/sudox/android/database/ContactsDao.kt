package com.sudox.android.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import io.reactivex.Flowable

@Dao
interface ContactsDao {

    @Insert(onConflict = REPLACE)
    fun insertContact(contact: Contact)

    @Query("delete from contacts_table where cid=:id")
    fun deleteContactById(id: String)

    @Query("SELECT * FROM contacts_table LIMIT :limit OFFSET :offset")
    fun getContacts(offset: Int, limit: Int): Flowable<List<Contact>>

    @Query("delete from contacts_table")
    fun deleteAllContacts()
}