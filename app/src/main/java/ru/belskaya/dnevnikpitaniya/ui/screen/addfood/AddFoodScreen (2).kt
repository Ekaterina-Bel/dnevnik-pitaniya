package ru.belskaya.dnevnikpitaniya.ui.screen.addfood

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import ru.belskaya.dnevnikpitaniya.domain.model.MealType
import ru.belskaya.dnevnikpitaniya.ui.components.BlackButton
import ru.belskaya.dnevnikpitaniya.ui.components.HeaderPanel
import ru.belskaya.dnevnikpitaniya.ui.theme.Black
import ru.belskaya.dnevnikpitaniya.ui.theme.Blue
import ru.belskaya.dnevnikpitaniya.ui.theme.GrayText
import ru.belskaya.dnevnikpitaniya.ui.theme.LightGrayBorder
import ru.belskaya.dnevnikpitaniya.ui.theme.White

data class AddFoodUiState(
    val norm: Int? = null,
    val remainingBefore: Int = 0,
)

@HiltViewModel
class AddFoodViewModel @Inject constructor(
    private val repository: FoodRepository,
    profileRepository: UserProfileRepository,
) : ViewModel() {

    private val today: String =
        LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    /** Норма и остаток ДО добавления — для шапки и «Останется …» */
    val uiState: StateFlow<AddFoodUiState> = combine(
        profileRepository.profile,
        repository.entries(today),
    ) { profile, entries ->
        val eaten = entries.sumOf { it.calories }
        val norm = profile?.dailyCalories
        AddFoodUiState(
            norm = norm,
            remainingBefore = norm?.let { CalorieCalculator.caloriesLeft(it, eaten) } ?: 0,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AddFoodUiState())

    var name by mutableStateOf(""); private set
    var grams by mutableStateOf("150"); private set
    var kcalPer100g by mutableStateOf(""); private set
    var proteinPer100g by mutableStateOf(""); private set
    var fatPer100g by mutableStateOf(""); private set
    var carbsPer100g by mutableStateOf(""); private set
    var mealType by mutableStateOf(MealType.BREAKFAST); private set
    var error by mutableStateOf<String?>(null); private set

    fun onNameChange(v: String) { name = v }
    fun onGramsChange(v: String) { grams = v.filter(Char::isDigit).take(4) }
    fun onKcalChange(v: String) { kcalPer100g = v.filter { c -> c.isDigit() || c == ',' || c == '.' } }
    fun onProteinChange(v: String) { proteinPer100g = v.filter { c -> c.isDigit() || c == ',' || c == '.' } }
    fun onFatChange(v: String) { fatPer100g = v.filter { c -> c.isDigit() || c == ',' || c == '.' } }
    fun onCarbsChange(v: String) { carbsPer100g = v.filter { c -> c.isDigit() || c == ',' || c == '.' } }
    fun onMealChange(v: MealType) { mealType = v }

    fun onMinus() {
        grams = ((grams.toIntOrNull() ?: 150) - 10).coerceAtLeast(10).toString()
    }
    fun onPlus() {
        grams = ((grams.toIntOrNull() ?: 150) + 10).coerceAtMost(2000).toString()
    }
    fun onQuickGrams(v: Int) { grams = v.toString() }

    private fun currentPortionKcal(): Double? {
        val g = grams.toIntOrNull() ?: return null
        val kcal = kcalPer100g.replace(',', '.').toDoubleOrNull() ?: return null
        return CalorieCalculator.caloriesForPortion(kcal, g)
    }

    fun save(onDone: () -> Unit) {
        val g = grams.toIntOrNull()
        val kcal = kcalPer100g.replace(',', '.').toDoubleOrNull()
        if (name.isBlank() || g == null || g <= 0 || kcal == null || kcal <= 0.0) {
            error = "Заполни название, вес порции и калорийность на 100 г"
            return
        }
        val num = { s: String -> s.replace(',', '.').toDoubleOrNull() ?: 0.0 }
        viewModelScope.launch {
            repository.add(
                FoodEntry(
                    date = today,
                    mealType = mealType,
                    name = name.trim(),
                    grams = g,
                    calories = CalorieCalculator.caloriesForPortion(kcal, g),
                    protein = CalorieCalculator.nutrientForPortion(num(proteinPer100g), g),
                    fat = CalorieCalculator.nutrientForPortion(num(fatPer100g), g),
                    carbs = CalorieCalculator.nutrientForPortion(num(carbsPer100g), g),
                )
            )
            onDone()
        }
    }
}

/** Экран 4: Добавление еды — нижний лист из макета */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddFoodScreen(
    viewModel: AddFoodViewModel,
    onDone: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val portionKcal = viewModel.grams.toIntOrNull()?.let { g ->
        viewModel.kcalPer100g.replace(',', '.').toDoubleOrNull()?.let { k ->
            CalorieCalculator.caloriesForPortion(k, g)
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            HeaderPanel(
                remaining = state.remainingBefore,
                norm = state.norm,
                onNormClick = {},
            )

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
                    Text("Добавить еду", fontSize = 22.sp, fontWeight = FontWeight.Bold)

                    // Название и калорийность на 100 г (база продуктов пока не нужна — ввод вручную)
                    OutlinedTextField(
                        value = viewModel.name, onValueChange = viewModel::onNameChange,
                        label = { Text("Название продукта") }, singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Blue, unfocusedBorderColor = LightGrayBorder),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = viewModel.kcalPer100g, onValueChange = viewModel::onKcalChange,
                        label = { Text("Калорийность, ккал на 100 г") }, singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Blue, unfocusedBorderColor = LightGrayBorder),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    // БЖУ на 100 г — три маленьких поля в ряд
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf(
                            Triple(viewModel.proteinPer100g, viewModel::onProteinChange, "Белки, г"),
                            Triple(viewModel.fatPer100g, viewModel::onFatChange, "Жиры, г"),
                            Triple(viewModel.carbsPer100g, viewModel::onCarbsChange, "Углеводы, г"),
                        ).forEach { (value, onChange, label) ->
                            OutlinedTextField(
                                value = value, onValueChange = onChange,
                                label = { Text(label, fontSize = 11.sp) }, singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Blue, unfocusedBorderColor = LightGrayBorder),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }

                    Text("Вес порции", fontSize = 16.sp, fontWeight = FontWeight.Bold)

                    // Степпер − 150 г + как в макете
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        RoundStepButton("−", viewModel::onMinus)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                viewModel.grams,
                                fontSize = 36.sp, fontWeight = FontWeight.ExtraBold,
                            )
                            Text(" г", fontSize = 15.sp, color = GrayText, modifier = Modifier.padding(bottom = 6.dp))
                        }
                        RoundStepButton("+", viewModel::onPlus)
                    }

                    // Быстрые веса: 50 / 100 / 150 / 200 / 250
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(50, 100, 150, 200, 250).forEach { v ->
                            QuickChip(
                                label = "$v",
                                selected = viewModel.grams == v.toString(),
                                onClick = { viewModel.onQuickGrams(v) },
                            )
                        }
                    }

                    Text("Приём пищи", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MealType.entries.forEach { meal ->
                            QuickChip(
                                label = meal.label,
                                selected = viewModel.mealType == meal,
                                onClick = { viewModel.onMealChange(meal) },
                            )
                        }
                    }

                    // Итог: «В порции X ккал · Останется Y ккал»
                    if (portionKcal != null) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                "В порции ${portionKcal.toInt()} ккал",
                                fontSize = 15.sp, fontWeight = FontWeight.Bold,
                            )
                            state.norm?.let {
                                Text(
                                    "Останется ${(state.remainingBefore - portionKcal).toInt().coerceAtLeast(0)} ккал",
                                    fontSize = 14.sp, color = GrayText,
                                )
                            }
                        }
                    }
                    viewModel.error?.let { Text(it, fontSize = 13.sp, color = Blue) }

                    Spacer(Modifier.height(4.dp))
                    BlackButton(
                        text = "Добавить в ${viewModel.mealType.label.lowercase()}",
                        onClick = { viewModel.save(onDone) },
                    )
                }
            }
        }
    }
}

/** Круглая кнопка − / + */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoundStepButton(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        border = BorderStroke(1.dp, LightGrayBorder),
        color = White,
        modifier = Modifier.size(46.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(label, fontSize = 22.sp, fontWeight = FontWeight.Medium, color = Black)
        }
    }
}

/** Чип: выбранный — чёрный, иначе белый с рамкой */
@Composable
private fun QuickChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = White,
            labelColor = Black,
            selectedContainerColor = Black,
            selectedLabelColor = White,
        ),
        border = BorderStroke(1.dp, if (selected) Black else LightGrayBorder),
    )
}
