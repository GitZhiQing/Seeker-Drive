package com.seeker.drive

import android.app.Application
import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _url = MutableStateFlow("")
    val url: StateFlow<String> = _url
    fun updateUrl(newUrl: String) {
        _url.value = newUrl
    }

    private val sharedPreferences =
        application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _currentScreen = MutableStateFlow(Screen.SDLoginPage)
    val currentScreen: StateFlow<Screen> = _currentScreen

    var selectedItem by mutableIntStateOf(0)
    var token by mutableStateOf<String?>(null)
        private set

    val uploadResults = mutableStateListOf<String>()
    val downloadResults = mutableStateListOf<String>()

    init {
        token = sharedPreferences.getString("token", null)
        if (token != null) {
            _currentScreen.value = Screen.MainPage
        }
        loadResults()
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
        uploadResults.clear()
        downloadResults.clear()
        sharedPreferences.edit().remove("uploadResults").remove("downloadResults").apply()
        sharedPreferences.edit().remove("token").apply()
        navigateTo(Screen.SDLoginPage)
    }

    private fun loadResults() {
        val uploadResultsJson = sharedPreferences.getString("uploadResults", "[]")
        val downloadResultsJson = sharedPreferences.getString("downloadResults", "[]")

        val uploadResultsArray = JSONArray(uploadResultsJson)
        for (i in 0 until uploadResultsArray.length()) {
            uploadResults.add(uploadResultsArray.getString(i))
        }

        val downloadResultsArray = JSONArray(downloadResultsJson)
        for (i in 0 until downloadResultsArray.length()) {
            downloadResults.add(downloadResultsArray.getString(i))
        }
    }

    fun getApiProtocol(): String {
        return getApplication<Application>().getString(R.string.api_protocol)
    }

    fun getApiHost(): String {
        return getApplication<Application>().getString(R.string.api_host)
    }

    fun getApiPort(): String {
        return getApplication<Application>().getString(R.string.api_port)
    }

    fun getApiPrefix(): String {
        return getApplication<Application>().getString(R.string.api_prefix)
    }
}