package ru.sudox.android.auth.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.sudox.android.auth.entities.AuthSessionEntity

@Dao
interface AuthSessionDAO {

    @Query("SELECT * FROM AuthSessionEntity WHERE phoneNumber = :phoneNumber")
    fun get(phoneNumber: String): Single<List<AuthSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(authSessionEntity: AuthSessionEntity): Single<Long>

    @Delete
    fun delete(authSessionEntity: AuthSessionEntity): Single<Int>
}