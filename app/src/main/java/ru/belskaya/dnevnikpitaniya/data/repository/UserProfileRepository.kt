package ru.belskaya.dnevnikpitaniya.data.repository

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.belskaya.dnevnikpitaniya.data.local.dao.UserProfileDao
import ru.belskaya.dnevnikpitaniya.data.toDomain
import ru.belskaya.dnevnikpitaniya.data.toEntity
import ru.belskaya.dnevnikpitaniya.domain.model.UserProfile

@Singleton
class UserProfileRepository @Inject constructor(
    private val dao: UserProfileDao,
) {
    val profile: Flow<UserProfile?> = dao.observeProfile().map { it?.toDomain() }

    suspend fun hasProfile(): Boolean = dao.getProfile() != null

    suspend fun save(profile: UserProfile) = dao.save(profile.toEntity())
}
