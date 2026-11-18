package ru.belskaya.dnevnikpitaniya.data.repository

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.belskaya.dnevnikpitaniya.data.local.dao.FoodEntryDao
import ru.belskaya.dnevnikpitaniya.data.toDomain
import ru.belskaya.dnevnikpitaniya.data.toEntity
import ru.belskaya.dnevnikpitaniya.domain.model.FoodEntry

@Singleton
class FoodRepository @Inject constructor(
    private val dao: FoodEntryDao,
) {
    fun entries(date: String): Flow<List<FoodEntry>> =
        dao.observeByDate(date).map { list -> list.map { it.toDomain() } }

    suspend fun add(entry: FoodEntry) = dao.insert(entry.toEntity())

    suspend fun remove(entry: FoodEntry) = dao.delete(entry.toEntity())
}
