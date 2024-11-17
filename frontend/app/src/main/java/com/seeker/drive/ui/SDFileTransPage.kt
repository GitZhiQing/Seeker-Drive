package com.seeker.drive.ui

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.seeker.drive.MainViewModel
import com.seeker.drive.R
import com.seeker.drive.data.RetrofitJsonInstance
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SDFileTransPage(viewModel: MainViewModel) {
    val uploadResults = viewModel.uploadResults
    val downloadResults = viewModel.downloadResults
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("文件传输", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Card {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically // 垂直居中对齐
            ) {
                UrlInputCard(viewModel, context)
            }
        }

        Text("上传结果", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
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
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("下载结果", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
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
}

@Composable
fun UrlInputCard(viewModel: MainViewModel, context: Context) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically // 垂直居中对齐
        ) {
            val url by viewModel.url.collectAsState()

            TextField(
                value = url,
                onValueChange = { viewModel.updateUrl(it) },
                label = { Text("URL") },
                modifier = Modifier.weight(3f)
            )
            IconButton(
                onClick = {
                    viewModel.token?.let {
                        downloadSharedFile(it, url, context, viewModel)
                    } ?: run {
                        Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.align(Alignment.Bottom) // 底部对齐
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.download),
                    contentDescription = "下载"
                )
            }
        }
    }
}

fun downloadSharedFile(
    token: String,
    url: String,
    context: Context,
    viewModel: MainViewModel
) {
    val fid = url.substringAfterLast('/').substringBeforeLast('.')
    RetrofitJsonInstance.api.downloadFile("Bearer $token", fid.toInt()).also { call ->
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    val contentDisposition = response.headers()["Content-Disposition"]
                    val fileName = contentDisposition?.substringAfter("filename=")?.trim('"')
                    if (body != null && fileName != null) {
                        try {
                            val file = File(context.getExternalFilesDir(null), fileName)
                            file.outputStream().use { it.write(body.bytes()) }
                            viewModel.downloadResults.add("下载成功：$url\nPath: ${file.absolutePath}")
                        } catch (e: Exception) {
                            viewModel.downloadResults.add("文件写入失败：$url\nError: ${e.message}")
                        }
                    } else {
                        viewModel.downloadResults.add("下载失败：$url\nError: 无法解析文件名或文件内容为空")
                    }
                } else {
                    viewModel.downloadResults.add("下载失败：$url\nError: 响应不成功")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                viewModel.downloadResults.add("下载失败：$url\nError: ${t.message}")
            }
        })
    }
}