package com.sudox.android.data.database.dao.user

import android.arch.persistence.room.*
import com.sudox.android.data.database.model.user.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: List<User>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(user: User)

    @Query("DELETE FROM users WHERE uid in (:ids)")
    fun removeAll(ids: List<Long>)

    @Query("DELETE FROM users WHERE uid = :id")
    fun removeOne(id: Long)

    @Query("SELECT * FROM users WHERE uid in (:ids)")
    fun loadByIds(ids: List<Long>): List<User>

    @Query("SELECT * FROM users WHERE uid = :id")
    fun loadById(id: Long): User
}