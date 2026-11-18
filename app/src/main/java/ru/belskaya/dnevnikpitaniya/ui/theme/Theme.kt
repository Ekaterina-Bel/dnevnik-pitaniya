package ru.belskaya.dnevnikpitaniya.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2E7D32),          // зелёный — «здоровое питание»
    secondary = Color(0xFF558B2F),
    tertiary = Color(0xFFEF6C00),         // оранжевый — акценты
)

@Composable
fun DnevnikTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColors, content = content)
}
