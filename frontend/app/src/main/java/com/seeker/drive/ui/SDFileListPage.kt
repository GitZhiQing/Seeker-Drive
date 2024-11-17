package com.seeker.drive.ui

import android.content.Context
import android.os.Environment
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

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
                fileList = emptyList()
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
                    FileItemView(file, viewModel.token, viewModel)
                }
            }
        }
    }
}

@Composable
fun FileItemView(file: FileItem, token: String?, viewModel: MainViewModel) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = file.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = "大小: ${file.size} bytes", style = MaterialTheme.typography.bodyMedium)
            Text(text = "状态: ${file.status}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "哈希: ${file.hash}", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "上传时间: ${convertToShanghaiTime(file.upload_time)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        IconButton(onClick = {
            token?.let {
                downloadFile(it, file.fid, file.name, context, viewModel)
            } ?: run {
                Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show()
            }
        }) {
            Icon(
                painter = painterResource(id = R.drawable.download),
                contentDescription = "下载"
            )
        }
    }
}

fun downloadFile(
    token: String,
    fileId: Int,
    fileName: String,
    context: Context,
    viewModel: MainViewModel
) {
    RetrofitJsonInstance.api.downloadFile("Bearer $token", fileId)
        .enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        saveFileToDisk(body, fileName, context, viewModel)
                    } ?: run {
                        viewModel.downloadResults.add("文件下载失败")
                    }
                } else {
                    viewModel.downloadResults.add("文件下载失败")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                viewModel.downloadResults.add("网络错误: ${t.message}")
            }
        })
}

fun saveFileToDisk(
    body: ResponseBody,
    fileName: String,
    context: Context,
    viewModel: MainViewModel
) {
    try {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            inputStream = body.byteStream()
            outputStream = FileOutputStream(file)
            val buffer = ByteArray(4096)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            outputStream.flush()
            viewModel.downloadResults.add("文件下载成功: ${file.absolutePath}")
        } catch (e: Exception) {
            viewModel.downloadResults.add("文件保存失败: ${e.message}")
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    } catch (e: Exception) {
        viewModel.downloadResults.add("文件创建失败: ${e.message}")
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