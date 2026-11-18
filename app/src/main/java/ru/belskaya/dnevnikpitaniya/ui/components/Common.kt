package ru.belskaya.dnevnikpitaniya.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import ru.belskaya.dnevnikpitaniya.ui.theme.Black
import ru.belskaya.dnevnikpitaniya.ui.theme.LightGrayBorder
import ru.belskaya.dnevnikpitaniya.ui.theme.Red
import ru.belskaya.dnevnikpitaniya.ui.theme.White

/** «понедельник, 12 октября» — подзаголовок из макета */
fun todayLabel(): String =
    SimpleDateFormat("EEEE, d MMMM", Locale("ru")).format(Date()).replaceFirstChar { it.uppercaseChar() }

/** Шкала-линейка из макетов: черточки с красной меткой посередине */
@Composable
fun CalorieScale(
    modifier: Modifier = Modifier,
    lineColor: Color = White,
    markerColor: Color = Red,
) {
    Canvas(modifier = modifier.fillMaxWidth().height(26.dp)) {
        val ticks = 41
        val w = size.width
        val h = size.height
        for (i in 0..ticks) {
            val x = w * i / ticks
            val isMarker = i == ticks / 2
            val tickHeight = if (isMarker) h * 0.95f else if (i % 5 == 0) h * 0.7f else h * 0.42f
            drawLine(
                color = if (isMarker) markerColor else lineColor,
                start = Offset(x, h - tickHeight),
                end = Offset(x, h),
                strokeWidth = if (isMarker) 6f else 3f,
            )
        }
    }
}

/** Чёрная скруглённая кнопка из макетов */
@Composable
fun BlackButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(26.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Black,
            contentColor = White,
            disabledContainerColor = LightGrayBorder,
            disabledContentColor = White,
        ),
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}
