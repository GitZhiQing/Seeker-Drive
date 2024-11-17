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
    val downloadResults = viewModel.downloadResults

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "文件传输", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Text("上传结果", style = MaterialTheme.typography.headlineMedium)
        uploadResult?.let {
            Text(it)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text("下载结果", style = MaterialTheme.typography.headlineMedium)
        downloadResults.forEach { result ->
            Text(result)
        }
    }
}