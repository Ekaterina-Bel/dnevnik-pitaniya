package ru.belskaya.dnevnikpitaniya.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.belskaya.dnevnikpitaniya.data.local.AppDatabase
import ru.belskaya.dnevnikpitaniya.data.local.dao.FoodEntryDao
import ru.belskaya.dnevnikpitaniya.data.local.dao.UserProfileDao

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /** Room (SQLite) — локальный кэш */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "dnevnik.db").build()

    @Provides
    fun provideUserProfileDao(db: AppDatabase): UserProfileDao = db.userProfileDao()

    @Provides
    fun provideFoodEntryDao(db: AppDatabase): FoodEntryDao = db.foodEntryDao()
}
