package ru.belskaya.dnevnikpitaniya

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

// ЗАГЛУШКА для шага 2: пустой экран, чтобы проверить сборку APK.
// В шаге 3 этот файл заменим на Hilt + навигацию по твоим макетам.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Text("Дневник питания: сборка работает!")
            }
        }
    }
}
