package ru.belskaya.dnevnikpitaniya.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Цвета из макетов
val Blue = Color(0xFF2E43E0)        // фирменный синий (сплэш, акценты)
val Black = Color(0xFF111214)       // почти чёрный (кнопки, текст)
val GrayPanel = Color(0xFF9DA1A7)   // серая верхняя панель
val GrayText = Color(0xFF5B5E63)    // вторичный текст
val LightGrayBorder = Color(0xFFE3E4E6)
val LightBlueBg = Color(0xFFEEF0FE) // фон выбранной карточки
val Red = Color(0xFFE53935)         // красная метка шкалы
val Yellow = Color(0xFFF9B233)      // желток яичницы
val White = Color(0xFFFFFFFF)

private val LightColors = lightColorScheme(
    primary = Blue,
    onPrimary = White,
    secondary = Black,
    background = White,
    surface = White,
    onBackground = Black,
    onSurface = Black,
)

@Composable
fun DnevnikTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColors, content = content)
}
