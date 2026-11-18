package ru.belskaya.dnevnikpitaniya.ui.screen.welcome

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.launch
import ru.belskaya.dnevnikpitaniya.data.repository.UserProfileRepository
import ru.belskaya.dnevnikpitaniya.ui.components.CalorieScale
import ru.belskaya.dnevnikpitaniya.ui.theme.Blue
import ru.belskaya.dnevnikpitaniya.ui.theme.White
import ru.belskaya.dnevnikpitaniya.ui.theme.Yellow

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val repository: UserProfileRepository,
) : ViewModel() {

    /** null = ещё проверяем, true = профиль есть, false = новый пользователь */
    var hasProfile by mutableStateOf<Boolean?>(null)
        private set

    init {
        viewModelScope.launch { hasProfile = repository.hasProfile() }
    }
}

/** Яичница с часовой шкалой — логотип из макета */
@Composable
private fun EggClock(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(190.dp)) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        // часовая шкала вокруг
        val radius = w * 0.44f
        for (i in 0 until 60) {
            val angle = Math.toRadians((i * 6 - 90).toDouble())
            val isTop = i == 0
            val len = if (i % 5 == 0) w * 0.07f else w * 0.035f
            drawLine(
                color = if (isTop) Color(0xFFE53935) else Color.White.copy(alpha = 0.9f),
                start = Offset(cx + radius * cos(angle).toFloat(), cy + radius * sin(angle).toFloat()),
                end = Offset(cx + (radius + len) * cos(angle).toFloat(), cy + (radius + len) * sin(angle).toFloat()),
                strokeWidth = if (isTop) 8f else 4f,
            )
        }

        // белок (перекошенный овал)
        rotate(degrees = -14f, pivot = Offset(cx, cy)) {
            drawOval(
                color = White,
                topLeft = Offset(cx - w * 0.30f, cy - h * 0.21f),
                size = Size(w * 0.60f, h * 0.42f),
            )
        }
        // желток
        drawCircle(color = Yellow, radius = w * 0.14f, center = Offset(cx, cy))
        drawCircle(color = Color(0xFFF9CE4F), radius = w * 0.09f, center = Offset(cx, cy))
    }
}

/** Экран 1: Приветствие (синий сплэш из макета) */
@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel,
    onStart: (hasProfile: Boolean) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Blue),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            EggClock()
            Spacer(Modifier.height(48.dp))
            Text(
                "Дневник питания",
                color = White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold,
            )
            Spacer(Modifier.height(40.dp))
            Button(
                onClick = { onStart(viewModel.hasProfile ?: false) },
                enabled = viewModel.hasProfile != null,
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Blue),
                modifier = Modifier.height(52.dp),
            ) {
                Text("Начать", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
        CalorieScale(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp, start = 24.dp, end = 24.dp),
        )
    }
}
