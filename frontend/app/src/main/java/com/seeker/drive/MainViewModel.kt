package com.seeker.drive

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var selectedItem by mutableIntStateOf(0)
    var presses by mutableIntStateOf(0)
}