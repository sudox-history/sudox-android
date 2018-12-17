package com.sudox.android.data.database.dao.user

import android.arch.persistence.room.*
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.models.users.UserType

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: List<User>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(user: User)

    @Query("UPDATE users SET type = 'UNKNOWN' WHERE type = :type")
    fun setUnknownByType(type: UserType)

    @Query("DELETE FROM users WHERE uid in (:ids)")
    fun removeAll(ids: List<Long>)

    @Query("DELETE FROM users WHERE uid = :id")
    fun removeOne(id: Long)

    @Query("SELECT * FROM users WHERE uid in (:ids)")
    fun loadByIds(ids: List<Long>): List<User>

    @Query("SELECT * FROM users WHERE uid = :id")
    fun loadById(id: Long): User?

    @Query("SELECT * FROM users WHERE type = :type")
    fun loadByType(type: UserType): List<User>

    @Query("UPDATE users SET type = :type WHERE uid IN (:ids)")
    fun setTypeAll(ids: List<Long>, type: UserType)

    @Query("UPDATE users SET type = :type WHERE uid = :id")
    fun setType(id: Long,  type: UserType)

    @Query("SELECT * FROM users WHERE phone = :phone AND type = :type LIMIT 1")
    fun loadByTypeAndPhone(phone: String, type: UserType): User?
}