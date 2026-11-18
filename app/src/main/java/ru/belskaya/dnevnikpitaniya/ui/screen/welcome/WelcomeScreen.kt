package ru.belskaya.dnevnikpitaniya.ui.screen.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import ru.belskaya.dnevnikpitaniya.data.repository.UserProfileRepository

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val repository: UserProfileRepository,
) : ViewModel() {

    /** null = ещё проверяем, true = профиль есть, false = новый пользователь */
    var hasProfile by androidx.compose.runtime.mutableStateOf<Boolean?>(null)
        private set

    init {
        viewModelScope.launch { hasProfile = repository.hasProfile() }
    }
}

/** Экран 1: Приветствие */
@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel,
    onStart: (hasProfile: Boolean) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Дневник питания",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Считай калории и следи за питанием",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(48.dp))
        Button(
            onClick = { onStart(viewModel.hasProfile ?: false) },
            enabled = viewModel.hasProfile != null,   // ждём проверку профиля
        ) {
            Text("Начать")
        }
    }
}
