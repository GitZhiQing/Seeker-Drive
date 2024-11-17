package com.seeker.drive.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.seeker.drive.MainViewModel
import com.seeker.drive.R
import com.seeker.drive.convertToShanghaiTime
import com.seeker.drive.data.FileItem
import com.seeker.drive.data.RetrofitJsonInstance
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.rotate

@Composable
fun SDFileListPage(viewModel: MainViewModel) {
    var fileList by remember { mutableStateOf<List<FileItem>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isRefreshing) 720f else 0f,
        animationSpec = tween(durationMillis = 1000), label = ""
    )

    fun loadData() {
        viewModel.token?.let { token ->
            coroutineScope.launch {
                isRefreshing = true
                fileList = emptyList() // Clear the list before fetching new data
                fetchFilesList(token) { files, error ->
                    if (files != null) {
                        fileList = files
                    } else {
                        errorMessage = error
                    }
                    isRefreshing = false
                }
            }
        } ?: run {
            errorMessage = "请先登录"
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "文件列表", style = MaterialTheme.typography.headlineSmall)
            IconButton(
                onClick = { loadData() },
                modifier = Modifier.rotate(rotation)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.refresh),
                    contentDescription = "刷新"
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (errorMessage != null) {
            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn {
                items(fileList) { file ->
                    FileItemView(file)
                }
            }
        }
    }
}

@Composable
fun FileItemView(file: FileItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = file.name, style = MaterialTheme.typography.bodyLarge)
        Text(text = "大小: ${file.size} bytes", style = MaterialTheme.typography.bodyMedium)
        Text(text = "状态: ${file.status}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "哈希: ${file.hash}", style = MaterialTheme.typography.bodyMedium)
        Text(
            text = "上传时间: ${convertToShanghaiTime(file.upload_time)}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

fun fetchFilesList(token: String, callback: (List<FileItem>?, String?) -> Unit) {
    RetrofitJsonInstance.api.getFilesList("Bearer $token")
        .enqueue(object : Callback<List<FileItem>> {
            override fun onResponse(
                call: Call<List<FileItem>>,
                response: Response<List<FileItem>>
            ) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "加载文件列表失败")
                }
            }

            override fun onFailure(call: Call<List<FileItem>>, t: Throwable) {
                callback(null, "网络错误: ${t.message}")
            }
        })
}