package ru.belskaya.dnevnikpitaniya.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.belskaya.dnevnikpitaniya.data.local.entity.FoodEntryEntity

@Dao
interface FoodEntryDao {

    /** Все записи за дату, обновляются автоматически */
    @Query("SELECT * FROM food_entries WHERE date = :date ORDER BY id DESC")
    fun observeByDate(date: String): Flow<List<FoodEntryEntity>>

    @Insert
    suspend fun insert(entry: FoodEntryEntity): Long

    @Delete
    suspend fun delete(entry: FoodEntryEntity)
}
