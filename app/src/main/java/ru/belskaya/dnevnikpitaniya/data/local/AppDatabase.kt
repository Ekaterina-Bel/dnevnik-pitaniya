package ru.belskaya.dnevnikpitaniya.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.belskaya.dnevnikpitaniya.data.local.dao.FoodEntryDao
import ru.belskaya.dnevnikpitaniya.data.local.dao.UserProfileDao
import ru.belskaya.dnevnikpitaniya.data.local.entity.FoodEntryEntity
import ru.belskaya.dnevnikpitaniya.data.local.entity.UserProfileEntity

/** Локальный кэш SQLite — как на диаграмме компонентов */
@Database(
    entities = [UserProfileEntity::class, FoodEntryEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun foodEntryDao(): FoodEntryDao
}
