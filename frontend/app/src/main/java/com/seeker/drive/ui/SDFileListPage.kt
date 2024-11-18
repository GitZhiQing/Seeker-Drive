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
import com.seeker.drive.data.RetrofitInstance
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
            Text(text = "文件列表", style = MaterialTheme.typography.headlineLarge)
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

        if (errorMessage != null) {
            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn {
                items(fileList) { file ->
                    FileItemView(file, viewModel.token, viewModel, ::loadData)
                }
            }
        }
    }
}

@Composable
fun FileItemView(file: FileItem, token: String?, viewModel: MainViewModel, loadData: () -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = file.name, style = MaterialTheme.typography.bodyLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(text = "大小: ${file.size} bytes", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "状态: ${if (file.status == 1) "公开" else "私有"}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(text = "哈希: ${file.hash}", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "上传时间: ${convertToShanghaiTime(file.upload_time)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = {
                    token?.let {
                        downloadFile(it, file.fid, file.name, context, viewModel)
                    } ?: run {
                        Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.download),
                        contentDescription = "下载",
                        tint = MaterialTheme.colorScheme.primary // Blue
                    )
                }
                IconButton(onClick = {
                    token?.let {
                        deleteFile(it, file.fid) { success, error ->
                            if (success) {
                                Toast.makeText(context, "文件删除成功", Toast.LENGTH_SHORT).show()
                                loadData()
                            } else {
                                Toast.makeText(context, error ?: "删除文件失败", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    } ?: run {
                        Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = "删除",
                        tint = MaterialTheme.colorScheme.error // Red
                    )
                }
                IconButton(onClick = {
                    token?.let {
                        updateFileStatus(
                            it,
                            file.fid,
                            if (file.status == 1) 0 else 1
                        ) { success, error ->
                            if (success) {
                                Toast.makeText(context, "文件状态更新成功", Toast.LENGTH_SHORT)
                                    .show()
                                loadData()
                                if (file.status == 0) {
                                    viewModel.uploadResults.add("文件 ${file.name} 已公开, URL: ${viewModel.getApiProtocol()}${viewModel.getApiHost()}${viewModel.getApiPort()}${viewModel.getApiPrefix()}files/${file.fid}")
                                } else {
                                    viewModel.uploadResults.add("文件 ${file.name} 已私有")
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    error ?: "更新文件状态失败",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    } ?: run {
                        Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.share),
                        contentDescription = "分享",
                        tint = MaterialTheme.colorScheme.secondary // Green
                    )
                }
            }
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

fun deleteFile(token: String, fileId: Int, callback: (Boolean, String?) -> Unit) {
    RetrofitInstance.api.deleteFile("Bearer $token", fileId)
        .enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, "删除文件失败")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                callback(false, "网络错误: ${t.message}")
            }
        })
}

fun updateFileStatus(
    token: String,
    fileId: Int,
    status: Int,
    callback: (Boolean, String?) -> Unit
) {
    RetrofitJsonInstance.api.updateFileStatus("Bearer $token", fileId, status)
        .enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, "更新文件状态失败")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                callback(false, "网络错误: ${t.message}")
            }
        })
}