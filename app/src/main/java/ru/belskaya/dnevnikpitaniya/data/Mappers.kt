package ru.belskaya.dnevnikpitaniya.data

import ru.belskaya.dnevnikpitaniya.data.local.entity.FoodEntryEntity
import ru.belskaya.dnevnikpitaniya.data.local.entity.UserProfileEntity
import ru.belskaya.dnevnikpitaniya.domain.model.ActivityLevel
import ru.belskaya.dnevnikpitaniya.domain.model.FoodEntry
import ru.belskaya.dnevnikpitaniya.domain.model.Gender
import ru.belskaya.dnevnikpitaniya.domain.model.MealType
import ru.belskaya.dnevnikpitaniya.domain.model.UserProfile

fun UserProfile.toEntity() = UserProfileEntity(
    gender = gender.name,
    age = age,
    heightCm = heightCm,
    weightKg = weightKg,
    activityFactor = activity.factor,
)

fun UserProfileEntity.toDomain() = UserProfile(
    gender = Gender.valueOf(gender),
    age = age,
    heightCm = heightCm,
    weightKg = weightKg,
    activity = ActivityLevel.entries.first { it.factor == activityFactor },
)

fun FoodEntry.toEntity() = FoodEntryEntity(
    id = id,
    date = date,
    mealType = mealType.name,
    name = name,
    grams = grams,
    calories = calories,
    protein = protein,
    fat = fat,
    carbs = carbs,
)

fun FoodEntryEntity.toDomain() = FoodEntry(
    id = id,
    date = date,
    mealType = MealType.valueOf(mealType),
    name = name,
    grams = grams,
    calories = calories,
    protein = protein,
    fat = fat,
    carbs = carbs,
)
