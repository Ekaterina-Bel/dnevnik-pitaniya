package ru.belskaya.dnevnikpitaniya.ui.screen.addfood

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.launch
import ru.belskaya.dnevnikpitaniya.data.repository.FoodRepository
import ru.belskaya.dnevnikpitaniya.domain.CalorieCalculator
import ru.belskaya.dnevnikpitaniya.domain.model.FoodEntry
import ru.belskaya.dnevnikpitaniya.domain.model.MealType

@HiltViewModel
class AddFoodViewModel @Inject constructor(
    private val repository: FoodRepository,
) : ViewModel() {

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
                    date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
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

/** Экран 4: Добавление еды (калорийность на 100 г вводится вручную) */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddFoodScreen(
    viewModel: AddFoodViewModel,
    onDone: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Добавить еду", style = MaterialTheme.typography.headlineSmall)

        // Приём пищи — чипсы как в макете
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MealType.entries.forEach { meal ->
                FilterChip(
                    selected = viewModel.mealType == meal,
                    onClick = { viewModel.onMealChange(meal) },
                    label = { Text(meal.label) },
                )
            }
        }

        OutlinedTextField(
            value = viewModel.name, onValueChange = viewModel::onNameChange,
            label = { Text("Название продукта") }, singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = viewModel.grams, onValueChange = viewModel::onGramsChange,
            label = { Text("Вес порции, г") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = viewModel.kcalPer100g, onValueChange = viewModel::onKcalChange,
            label = { Text("Калорийность, ккал на 100 г") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
        )

        Text("БЖУ на 100 г (можно пропустить)", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = viewModel.proteinPer100g, onValueChange = viewModel::onProteinChange,
            label = { Text("Белки, г") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = viewModel.fatPer100g, onValueChange = viewModel::onFatChange,
            label = { Text("Жиры, г") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = viewModel.carbsPer100g, onValueChange = viewModel::onCarbsChange,
            label = { Text("Углеводы, г") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
        )

        // Пересчёт на порцию «на лету»
        val g = viewModel.grams.toIntOrNull()
        val kcal = viewModel.kcalPer100g.replace(',', '.').toDoubleOrNull()
        if (g != null && kcal != null) {
            Text(
                "Итого за порцию ${g} г: ${"%.0f".format(kcal * g / 100.0)} ккал",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        viewModel.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { viewModel.save(onDone) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Добавить в дневник")
        }
    }
}
