package com.sudox.android.data.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.sudox.android.data.database.model.Contact

@Dao
interface ContactsDao {

    @Query("DELETE FROM contacts")
    fun removeAll()

    @Query("DELETE FROM contacts WHERE uid = :id")
    fun removeOne(id: String)

    @Insert
    fun insertAll(contacts: List<Contact>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(contact: Contact)

    @Query("SELECT * FROM contacts")
    fun loadAll(): LiveData<List<Contact>>
}