package ru.belskaya.dnevnikpitaniya.ui.screen.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import ru.belskaya.dnevnikpitaniya.domain.model.ActivityLevel
import ru.belskaya.dnevnikpitaniya.domain.model.Gender
import ru.belskaya.dnevnikpitaniya.domain.model.UserProfile
import ru.belskaya.dnevnikpitaniya.data.repository.UserProfileRepository

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

/** Экран 2: Расчёт нормы (пол, возраст, рост, вес, активность) */
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
    var activityMenuExpanded by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Расчёт нормы калорий", style = MaterialTheme.typography.headlineSmall)

        // Пол
        Text("Пол", style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Gender.entries.forEach { g ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 16.dp),
                ) {
                    RadioButton(selected = gender == g, onClick = { gender = g })
                    Text(g.label)
                }
            }
        }

        // Возраст, рост, вес
        OutlinedTextField(
            value = age, onValueChange = { age = it.filter(Char::isDigit).take(3) },
            label = { Text("Возраст, лет") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = height, onValueChange = { height = it.filter(Char::isDigit).take(3) },
            label = { Text("Рост, см") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = weight, onValueChange = { weight = it.filter { c -> c.isDigit() || c == ',' || c == '.' } },
            label = { Text("Вес, кг") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
        )

        // Активность
        Text("Уровень активности", style = MaterialTheme.typography.titleMedium)
        ExposedDropdownMenuBox(
            expanded = activityMenuExpanded,
            onExpandedChange = { activityMenuExpanded = it },
        ) {
            OutlinedTextField(
                value = activity.label,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(activityMenuExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
            )
            ExposedDropdownMenu(
                expanded = activityMenuExpanded,
                onDismissRequest = { activityMenuExpanded = false },
            ) {
                ActivityLevel.entries.forEach { level ->
                    DropdownMenuItem(
                        text = { Text("${level.label} (x${level.factor})") },
                        onClick = { activity = level; activityMenuExpanded = false },
                    )
                }
            }
        }

        // Норма по Миффлину-Сан-Жеору
        val profile = remember(gender, age, height, weight, activity) {
            val a = age.toIntOrNull(); val h = height.toIntOrNull(); val w = weight.replace(',', '.').toDoubleOrNull()
            if (a != null && h != null && w != null && a in 1..120 && h in 50..250 && w in 20.0..300.0)
                UserProfile(gender, a, h, w, activity) else null
        }
        if (profile != null) {
            Text(
                "Ваша норма: ${profile.dailyCalories} ккал/день " +
                    "(Б ${profile.proteinG} / Ж ${profile.fatG} / У ${profile.carbsG} г)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                if (profile == null) {
                    error = "Проверь введённые данные (возраст, рост, вес)"
                } else {
                    viewModel.save(profile, onDone)
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Сохранить и перейти к дневнику")
        }
    }
}
