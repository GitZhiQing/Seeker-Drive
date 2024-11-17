package com.seeker.drive.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seeker.drive.MainViewModel

@Composable
fun SDFileTransPage(viewModel: MainViewModel) {
    val uploadResult by viewModel.uploadResult

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "文件传输", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        uploadResult?.let {
            Text(it)
        }
    }
}