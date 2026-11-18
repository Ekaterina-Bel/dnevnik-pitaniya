package ru.belskaya.dnevnikpitaniya.domain.model

enum class Gender(val label: String) { MALE("Мужской"), FEMALE("Женский") }

enum class ActivityLevel(val factor: Double, val label: String) {
    SEDENTARY(1.2, "Минимальная (сидячая работа)"),
    LIGHT(1.375, "Лёгкая (прогулки)"),
    MODERATE(1.55, "Средняя (тренировки 3-5 р/нед)"),
    HIGH(1.725, "Высокая (ежедневные тренировки)")
}

enum class MealType(val label: String) {
    BREAKFAST("Завтрак"), LUNCH("Обед"), DINNER("Ужин"), SNACK("Перекус")
}

/** Профиль пользователя. Формула Миффлина-Сан-Жеора — по IDEF0 */
data class UserProfile(
    val gender: Gender,
    val age: Int,
    val heightCm: Int,
    val weightKg: Double,
    val activity: ActivityLevel,
) {
    /** Базовый метаболизм (ккал/день) */
    val bmr: Double
        get() = 10 * weightKg + 6.25 * heightCm - 5 * age +
                if (gender == Gender.MALE) 5.0 else -161.0

    /** Суточная норма калорий с учётом активности */
    val dailyCalories: Int
        get() = (bmr * activity.factor).toInt()

    /** Норма воды: ~30 мл на кг веса */
    val dailyWaterMl: Int
        get() = (weightKg * 30).toInt()

    // БЖУ (25% белки / 30% жиры / 45% углеводы от нормы ккал)
    val proteinG: Int get() = (dailyCalories * 0.25 / 4).toInt()
    val fatG: Int get() = (dailyCalories * 0.30 / 9).toInt()
    val carbsG: Int get() = (dailyCalories * 0.45 / 4).toInt()
}

/** Запись о съеденном блюде за день */
data class FoodEntry(
    val id: Long = 0,
    val date: String,           // "yyyy-MM-dd"
    val mealType: MealType,
    val name: String,
    val grams: Int,
    val calories: Double,       // ккал за порцию
    val protein: Double,        // г белков за порцию
    val fat: Double,            // г жиров за порцию
    val carbs: Double,          // г углеводов за порцию
)
