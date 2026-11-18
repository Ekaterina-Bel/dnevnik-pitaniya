package ru.belskaya.dnevnikpitaniya.ui.screen.setup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import ru.belskaya.dnevnikpitaniya.data.repository.UserProfileRepository
import ru.belskaya.dnevnikpitaniya.domain.model.ActivityLevel
import ru.belskaya.dnevnikpitaniya.domain.model.Gender
import ru.belskaya.dnevnikpitaniya.domain.model.UserProfile
import ru.belskaya.dnevnikpitaniya.ui.components.BlackButton
import ru.belskaya.dnevnikpitaniya.ui.components.HeaderPanel
import ru.belskaya.dnevnikpitaniya.ui.theme.Black
import ru.belskaya.dnevnikpitaniya.ui.theme.Blue
import ru.belskaya.dnevnikpitaniya.ui.theme.GrayText
import ru.belskaya.dnevnikpitaniya.ui.theme.LightBlueBg
import ru.belskaya.dnevnikpitaniya.ui.theme.LightGrayBorder
import ru.belskaya.dnevnikpitaniya.ui.theme.White

@HiltViewModel
class ProfileSetupViewModel @Inject constructor(
    private val repository: UserProfileRepository,
) : ViewModel() {

    fun save(profile: UserProfile, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.save(profile)
            onDone()
        }
    }
}

/** Короткие названия и подписи уровней активности — как в макете */
private val activityHints: Map<ActivityLevel, Pair<String, String>> = mapOf(
    ActivityLevel.SEDENTARY to ("Почти не двигаюсь" to "Сидячая работа, без тренировок"),
    ActivityLevel.LIGHT to ("Лёгкая" to "Тренировки 1–3 раза в неделю"),
    ActivityLevel.MODERATE to ("Средняя" to "Тренировки 3–5 раз в неделю"),
    ActivityLevel.HIGH to ("Высокая" to "Каждый день или физический труд"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SmallNumberField(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    decimal: Boolean = false,
) {
    Column(modifier = modifier) {
        Text(label, fontSize = 12.sp, color = GrayText)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = if (decimal) KeyboardType.Decimal else KeyboardType.Number,
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Blue,
                unfocusedBorderColor = LightGrayBorder,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** Экран 2: Расчёт нормы по макету */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    viewModel: ProfileSetupViewModel,
    onDone: () -> Unit,
) {
    var gender by remember { mutableStateOf(Gender.FEMALE) }
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var activity by remember { mutableStateOf(ActivityLevel.SEDENTARY) }
    var error by remember { mutableStateOf<String?>(null) }

    val profile = remember(gender, age, height, weight, activity) {
        val a = age.toIntOrNull()
        val h = height.toIntOrNull()
        val w = weight.replace(',', '.').toDoubleOrNull()
        if (a != null && h != null && w != null && a in 1..120 && h in 50..250 && w in 20.0..300.0)
            UserProfile(gender, a, h, w, activity) else null
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            HeaderPanel(remaining = null, norm = null, onNormClick = {})

            Surface(
                modifier = Modifier.fillMaxSize().offset(y = (-24).dp),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = White,
                shadowElevation = 10.dp,
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Text("Сначала — ваша норма", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Считаем по формуле Миффлина — Сан-Жеора. Данные остаются на этом телефоне.",
                        fontSize = 13.sp, color = GrayText,
                    )

                    // Пол: выбранный — чёрная кнопка, иначе белая с рамкой
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Gender.entries.forEach { g ->
                            val selected = gender == g
                            Surface(
                                color = if (selected) Black else White,
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, if (selected) Black else LightGrayBorder),
                                modifier = Modifier.weight(1f).height(48.dp),
                                onClick = { gender = g },
                            ) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        g.label,
                                        color = if (selected) White else Black,
                                        fontWeight = FontWeight.Medium,
                                    )
                                }
                            }
                        }
                    }

                    // Возраст / рост / вес — три компактных поля в ряд
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        SmallNumberField(age, { age = it.filter(Char::isDigit).take(3) }, "Возраст, лет", Modifier.weight(1f))
                        SmallNumberField(height, { height = it.filter(Char::isDigit).take(3) }, "Рост, см", Modifier.weight(1f))
                        SmallNumberField(weight, { weight = it.filter { c -> c.isDigit() || c == ',' || c == '.' } }, "Вес, кг", Modifier.weight(1f), decimal = true)
                    }

                    Text("Как вы двигаетесь", fontSize = 16.sp, fontWeight = FontWeight.Bold)

                    // Карточки активности: выбранная — синяя рамка и голубой фон
                    ActivityLevel.entries.forEach { level ->
                        val selected = activity == level
                        val (title, subtitle) = activityHints.getValue(level)
                        Card(
                            onClick = { activity = level },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(
                                if (selected) 2.dp else 1.dp,
                                if (selected) Blue else LightGrayBorder,
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selected) LightBlueBg else White,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Text(subtitle, fontSize = 12.sp, color = GrayText)
                                }
                                Text("× ${level.factor}", fontSize = 14.sp, color = GrayText)
                            }
                        }
                    }

                    // Норма считается сразу — как в макете, где после расчёта
                    // показывается «Ваша норма 1917 ккал»
                    if (profile != null) {
                        Text(
                            "Ваша норма: ${profile.dailyCalories} ккал",
                            fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Blue,
                        )
                    }
                    error?.let {
                        Text(it, fontSize = 13.sp, color = MaterialTheme.colorScheme.error)
                    }

                    Spacer(Modifier.height(4.dp))
                    BlackButton(
                        text = "Рассчитать",
                        onClick = {
                            if (profile == null) {
                                error = "Проверь введённые данные (возраст, рост, вес)"
                            } else {
                                viewModel.save(profile, onDone)
                            }
                        },
                        enabled = profile != null,
                    )
                }
            }
        }
    }
}
