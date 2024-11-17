package com.seeker.drive

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences =
        application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _currentScreen = MutableStateFlow(Screen.SDLoginPage)
    val currentScreen: StateFlow<Screen> = _currentScreen

    var selectedItem by mutableIntStateOf(0)
    var presses by mutableIntStateOf(0)
    var token by mutableStateOf<String?>(null)
        private set

    init {
        token = sharedPreferences.getString("token", null)
        if (token != null) {
            _currentScreen.value = Screen.MainPage
        }
    }

    enum class Screen {
        MainPage,
        SDLoginPage,
        SDRegisterPage
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun saveToken(token: String) {
        this.token = token
        sharedPreferences.edit().putString("token", token).apply()
        navigateTo(Screen.MainPage)
    }

    fun logout() {
        token = null
        sharedPreferences.edit().remove("token").apply()
        navigateTo(Screen.SDLoginPage)
    }
}