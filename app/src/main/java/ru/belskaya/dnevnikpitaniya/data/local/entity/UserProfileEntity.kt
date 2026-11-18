package ru.belskaya.dnevnikpitaniya.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,          // профиль один — id всегда 1
    val gender: String,                     // "MALE" / "FEMALE"
    val age: Int,
    val heightCm: Int,
    val weightKg: Double,
    val activityFactor: Double,             // 1.2 / 1.375 / 1.55 / 1.725
)
