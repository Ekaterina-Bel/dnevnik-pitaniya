package ru.belskaya.dnevnikpitaniya

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import ru.belskaya.dnevnikpitaniya.ui.navigation.AppNavHost
import ru.belskaya.dnevnikpitaniya.ui.theme.DnevnikTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DnevnikTheme {
                AppNavHost()
            }
        }
    }
}
