package com.seeker.drive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seeker.drive.data.RetrofitInstance
import com.seeker.drive.data.RetrofitJsonInstance
import com.seeker.drive.ui.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        RetrofitInstance.initialize(this)
        RetrofitJsonInstance.initialize(this)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp(mainViewModel: MainViewModel = viewModel()) {
    val currentScreen by mainViewModel.currentScreen.collectAsState()
    when (currentScreen) {
        MainViewModel.Screen.MainPage -> MainPage()
        MainViewModel.Screen.SDLoginPage -> SDLoginPage(mainViewModel)
        MainViewModel.Screen.SDRegisterPage -> SDRegisterPage(mainViewModel)
    }
}