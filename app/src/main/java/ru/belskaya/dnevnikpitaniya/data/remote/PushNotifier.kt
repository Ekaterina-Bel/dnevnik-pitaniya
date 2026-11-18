package ru.belskaya.dnevnikpitaniya.data.remote

/**
 * ЗАГЛУШКА push-уведомлений (FCM/APNs) — как на диаграмме компонентов.
 * Позже здесь будет Firebase Messaging.
 */
interface PushNotifier {
    /** Напоминание записать приём пищи */
    fun scheduleMealReminder()

    fun cancelAll()
}
