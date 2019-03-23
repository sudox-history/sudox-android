package com.sudox.android.data.database.dao.user

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.sudox.android.data.database.model.user.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: Array<out User>)

    @Query("DELETE FROM users WHERE uid in (:ids)")
    fun removeAll(ids: List<Long>)

    @Query("SELECT * FROM users WHERE uid in (:ids)")
    fun loadByIds(ids: List<Long>): List<User>

    @Query("SELECT * FROM users WHERE uid = :id")
    fun loadById(id: Long): User?

    @Query("SELECT uid FROM users WHERE uid IN (:ids) AND isContact = 1")
    fun filterContactsIds(ids: List<Long>): List<Long>

    @Query("SELECT uid FROM users WHERE uid IN (:ids)")
    fun filterExists(ids: LongArray): List<Long>

    @Query("SELECT * FROM users WHERE isContact = 1")
    fun loadContacts(): List<User>

    @Query("SELECT uid FROM users WHERE isContact = 1")
    fun loadContactsIds(): LongArray

    @Query("SELECT isContact FROM users WHERE phone = :phone")
    fun isContactByPhone(phone: String): Boolean
}