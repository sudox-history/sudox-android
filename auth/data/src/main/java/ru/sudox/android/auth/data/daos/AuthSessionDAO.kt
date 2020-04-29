package ru.sudox.android.auth.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Maybe
import ru.sudox.android.auth.data.entities.AuthSessionEntity

@Dao
interface AuthSessionDAO {

    @Query("SELECT * FROM AuthSessionEntity WHERE phoneNumber = :phoneNumber")
    fun get(phoneNumber: String): Maybe<AuthSessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(authSessionEntity: AuthSessionEntity): Long

    @Update
    fun update(authSessionEntity: AuthSessionEntity)

    @Delete
    fun delete(authSessionEntity: AuthSessionEntity): Int
}