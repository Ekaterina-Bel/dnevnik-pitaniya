package ru.belskaya.dnevnikpitaniya.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.belskaya.dnevnikpitaniya.data.local.entity.UserProfileEntity

@Dao
interface UserProfileDao {

    /** Профиль как реактивный поток: экран обновится сам при изменении */
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun observeProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getProfile(): UserProfileEntity?

    /** Вставить или обновить (профиль один, перезаписываем) */
    @Upsert
    suspend fun save(entity: UserProfileEntity)
}
