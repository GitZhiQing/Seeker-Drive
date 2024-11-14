package com.seeker.drive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seeker.drive.ui.*
import com.seeker.drive.ui.theme.SeekerDriveTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp(mainViewModel: MainViewModel = viewModel()) {
    val currentScreen by mainViewModel::currentScreen
    when (currentScreen) {
        MainViewModel.Screen.MainPage -> MainPage()
        MainViewModel.Screen.SDLoginPage -> SDLoginPage(mainViewModel)
        MainViewModel.Screen.SDRegisterPage -> SDRegisterPage(mainViewModel)
    }
}