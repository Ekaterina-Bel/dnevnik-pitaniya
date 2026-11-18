package ru.belskaya.dnevnikpitaniya.data.remote

import ru.belskaya.dnevnikpitaniya.domain.model.FoodEntry

/**
 * ЗАГЛУШКА сетевого клиента (REST) — как на диаграмме компонентов.
 * Позже здесь будет Retrofit + API Gateway на бэкенде.
 * Сейчас интерфейс просто фиксирует контракт, реализация не нужна.
 */
interface NutritionApi {
    /** Поиск продукта в базе на сервере */
    suspend fun searchProduct(query: String): List<FoodEntry>

    /** Синхронизация локального кэша с сервером */
    suspend fun sync()
}
