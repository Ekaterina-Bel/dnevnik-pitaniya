package ru.belskaya.dnevnikpitaniya.domain

/**
 * Калькулятор калорий (Миффлина) — «Миффлин» на диаграмме компонентов.
 * Формула нормы вынесена в UserProfile (domain/model/Models.kt),
 * здесь — расчёт БЖУ и калорий для конкретной порции еды.
 */
object CalorieCalculator {

    /** Ккал за порцию: калорийность на 100 г * вес порции / 100 */
    fun caloriesForPortion(kcalPer100g: Double, grams: Int): Double =
        kcalPer100g * grams / 100.0

    /** Граммы нутриента (белок/жиры/углеводы) за порцию */
    fun nutrientForPortion(gramsPer100g: Double, grams: Int): Double =
        gramsPer100g * grams / 100.0

    /** Остаток калорий на день */
    fun caloriesLeft(dailyNorm: Int, eaten: Double): Int =
        (dailyNorm - eaten).toInt().coerceAtLeast(0)
}
