package ru.belskaya.dnevnikpitaniya.ui.screen.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.belskaya.dnevnikpitaniya.data.repository.FoodRepository
import ru.belskaya.dnevnikpitaniya.data.repository.UserProfileRepository
import ru.belskaya.dnevnikpitaniya.domain.CalorieCalculator
import ru.belskaya.dnevnikpitaniya.domain.model.FoodEntry
import ru.belskaya.dnevnikpitaniya.domain.model.UserProfile
import ru.belskaya.dnevnikpitaniya.ui.components.HeaderPanel
import ru.belskaya.dnevnikpitaniya.ui.theme.Black
import ru.belskaya.dnevnikpitaniya.ui.theme.GrayText
import ru.belskaya.dnevnikpitaniya.ui.theme.LightGrayBorder
import ru.belskaya.dnevnikpitaniya.ui.theme.White

data class HomeUiState(
    val profile: UserProfile? = null,
    val entries: List<FoodEntry> = emptyList(),
) {
    val eaten: Int get() = entries.sumOf { it.calories }.toInt()
    val left: Int get() = profile?.let { CalorieCalculator.caloriesLeft(it.dailyCalories, eaten.toDouble()) } ?: 0
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    profileRepository: UserProfileRepository,
    private val foodRepository: FoodRepository,
) : ViewModel() {

    private val today: String =
        LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    val uiState: StateFlow<HomeUiState> = combine(
        profileRepository.profile,
        foodRepository.entries(today),
    ) { profile, entries -> HomeUiState(profile, entries) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun remove(entry: FoodEntry) {
        viewModelScope.launch { foodRepository.remove(entry) }
    }
}

/** Экран 3: Главный — по макету: серая панель + белый лист с записями */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAddFood: () -> Unit,
    onEditProfile: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val profile = state.profile

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            HeaderPanel(
                remaining = profile?.let { state.left },
                norm = profile?.dailyCalories,
                eaten = state.eaten,
                onNormClick = onEditProfile,
            )

            Surface(
                modifier = Modifier.fillMaxSize().offset(y = (-24).dp),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = White,
                shadowElevation = 10.dp,
            ) {
                Column(Modifier.fillMaxSize().padding(20.dp)) {
                    Text("Приёмы пищи", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    androidx.compose.foundation.layout.Spacer(Modifier.padding(10.dp))

                    if (state.entries.isEmpty()) {
                        Text(
                            "Пока пусто. Нажми «+» и добавь первый приём пищи!",
                            fontSize = 14.sp, color = GrayText,
                        )
                    }

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(state.entries, key = { it.id }) { entry ->
                            // карточка продукта как в макете: название + БЖУ + крестик
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, LightGrayBorder),
                                colors = CardDefaults.cardColors(containerColor = White),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Row(
                                    Modifier.fillMaxWidth().padding(start = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column(
                                        Modifier.weight(1f).padding(vertical = 12.dp),
                                    ) {
                                        Text(entry.name, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            "${entry.calories.toInt()} ккал на ${entry.grams} г · " +
                                                "Б ${"%.0f".format(entry.protein)} · " +
                                                "Ж ${"%.0f".format(entry.fat)} · " +
                                                "У ${"%.0f".format(entry.carbs)}",
                                            fontSize = 12.sp, color = GrayText,
                                        )
                                        Text(
                                            entry.mealType.label,
                                            fontSize = 12.sp, color = GrayText,
                                        )
                                    }
                                    IconButton(onClick = { viewModel.remove(entry) }) {
                                        Text("✕", fontSize = 16.sp, color = GrayText)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // чёрная кнопка «+» в стиле макета
        FloatingActionButton(
            onClick = onAddFood,
            containerColor = Black,
            contentColor = White,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp),
        ) {
            Icon(Icons.Default.Add, contentDescription = "Добавить еду")
        }
    }
}
