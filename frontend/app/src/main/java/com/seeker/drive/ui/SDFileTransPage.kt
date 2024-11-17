package com.seeker.drive.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seeker.drive.MainViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SDFileTransPage(viewModel: MainViewModel) {
    val uploadResults = viewModel.uploadResults
    val downloadResults = viewModel.downloadResults

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("文件传输", style = MaterialTheme.typography.headlineLarge) }
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)
            ) {
                item {
                    Text("上传结果", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(uploadResults) { result ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(result, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("下载结果", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(downloadResults) { result ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(result, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    )
}