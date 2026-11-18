package ru.belskaya.dnevnikpitaniya.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

data class HomeUiState(
    val profile: UserProfile? = null,
    val entries: List<FoodEntry> = emptyList(),
) {
    val eaten: Double get() = entries.sumOf { it.calories }
    val left: Int get() = profile?.let { CalorieCalculator.caloriesLeft(it.dailyCalories, eaten) } ?: 0
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

/** Экран 3: Главный — норма / съедено / осталось + список еды */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAddFood: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val profile = state.profile

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddFood) {
                Icon(Icons.Default.Add, contentDescription = "Добавить еду")
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
        ) {
            Text("Сегодня", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            if (profile == null) {
                Text("Профиль не заполнен. Пройди расчёт нормы.")
            } else {
                // Как в макете: Норма / Съедено / Осталось
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    StatItem("Норма", "${profile.dailyCalories}", MaterialTheme.colorScheme.primary)
                    StatItem("Съедено", "${state.eaten.toInt()}", MaterialTheme.colorScheme.tertiary)
                    StatItem("Осталось", "${state.left}", MaterialTheme.colorScheme.secondary)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "БЖУ за день: Б ${"%.0f".format(state.entries.sumOf { it.protein })} г / " +
                        "Ж ${"%.0f".format(state.entries.sumOf { it.fat })} г / " +
                        "У ${"%.0f".format(state.entries.sumOf { it.carbs })} г",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(Modifier.height(24.dp))
            Text("Приёмы пищи", style = MaterialTheme.typography.titleMedium)

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.entries, key = { it.id }) { entry ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("${entry.mealType.label} · ${entry.grams} г", style = MaterialTheme.typography.labelMedium)
                                Text(entry.name, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    "${entry.calories.toInt()} ккал · Б ${"%.1f".format(entry.protein)} / " +
                                        "Ж ${"%.1f".format(entry.fat)} / У ${"%.1f".format(entry.carbs)}",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                            IconButton(onClick = { viewModel.remove(entry) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Удалить")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Text(value, style = MaterialTheme.typography.headlineMedium, color = color)
    }
}
