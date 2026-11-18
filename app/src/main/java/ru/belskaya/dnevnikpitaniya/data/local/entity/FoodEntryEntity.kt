package ru.belskaya.dnevnikpitaniya.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_entries")
data class FoodEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,                       // "yyyy-MM-dd"
    val mealType: String,                   // "BREAKFAST" / "LUNCH" / "DINNER" / "SNACK"
    val name: String,
    val grams: Int,
    val calories: Double,                   // ккал за порцию
    val protein: Double,
    val fat: Double,
    val carbs: Double,
)
