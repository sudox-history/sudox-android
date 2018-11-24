package com.sudox.android.data.database.dao

import android.arch.persistence.room.*
import com.sudox.android.data.database.model.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: List<User>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(user: User)

    @Query("DELETE FROM users WHERE uid in (:ids)")
    fun removeAll(ids: List<String>)

    @Query("DELETE FROM users WHERE uid = :id")
    fun removeOne(id: String)

    @Query("SELECT * FROM users WHERE uid in (:ids)")
    fun loadByIds(ids: List<String>): List<User>

    @Query("SELECT * FROM users WHERE uid = :id")
    fun loadById(id: String)
}