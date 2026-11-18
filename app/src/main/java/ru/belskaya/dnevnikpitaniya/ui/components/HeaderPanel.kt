package ru.belskaya.dnevnikpitaniya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.belskaya.dnevnikpitaniya.ui.theme.GrayPanel
import ru.belskaya.dnevnikpitaniya.ui.theme.White

/**
 * Верхняя серая панель из макетов:
 * «Сегодня / понедельник, 12 октября» + чип «Норма» + большая цифра «Осталось X ккал» + шкала.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderPanel(
    remaining: Int?,              // null = норма не задана
    norm: Int?,
    eaten: Int = 0,
    onNormClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(GrayPanel)
            .padding(horizontal = 20.dp, vertical = 18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Сегодня", color = White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(todayLabel(), color = White.copy(alpha = 0.85f), fontSize = 13.sp)
            }
            // чип «Норма»
            Surface(
                color = Color.White.copy(alpha = 0.28f),
                shape = RoundedCornerShape(16.dp),
                onClick = onNormClick,
            ) {
                Row(
                    Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("⚖", color = White, fontSize = 13.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        norm?.let { "Норма $it" } ?: "Норма",
                        color = White, fontSize = 13.sp, fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))
        if (remaining == null || norm == null) {
            Text("Норма не задана", color = White, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
        } else {
            Row(verticalAlignment = Alignment.Bottom) {
                Text("$remaining", fontSize = 54.sp, fontWeight = FontWeight.ExtraBold, color = White)
                Spacer(Modifier.width(6.dp))
                Text(
                    "ккал",
                    color = White, fontSize = 17.sp,
                    modifier = Modifier.padding(bottom = 10.dp),
                )
            }
            Text(
                "Съедено $eaten из $norm ккал",
                color = White.copy(alpha = 0.85f), fontSize = 13.sp,
            )
            Spacer(Modifier.height(4.dp))
        }
        CalorieScale(modifier = Modifier.padding(top = 6.dp))
    }
}
