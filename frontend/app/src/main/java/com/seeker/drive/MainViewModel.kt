package com.seeker.drive

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    var currentScreen by mutableStateOf(Screen.SDLoginPage)
    var selectedItem by mutableIntStateOf(0)
    var presses by mutableIntStateOf(0)
    var isLogin by mutableIntStateOf(0)
    var correctUsername by mutableStateOf("seeker")
    var correctPassword by mutableStateOf("123456")

    enum class Screen {
        MainPage,
        SDLoginPage,
        SDRegisterPage
    }

    fun navigateTo(screen: Screen) {
        currentScreen = screen
    }

    fun login(username: String, password: String): Boolean {
        return username == correctUsername && password == correctPassword
    }
}