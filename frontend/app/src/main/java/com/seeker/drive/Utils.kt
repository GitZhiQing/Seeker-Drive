package com.seeker.drive

import android.content.Context
import android.net.Uri
import com.seeker.drive.data.FileItem
import com.seeker.drive.data.RetrofitInstance
import com.seeker.drive.data.User
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun fetchCurrentUser(token: String, callback: (User?, String?) -> Unit) {
    RetrofitInstance.api.getCurrentUser("Bearer $token")
        .enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Failed to fetch user: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                callback(null, "Network error: ${t.message}")
            }
        })
}

fun convertToShanghaiTime(utcTimestamp: Long): String {
    val date = Date(utcTimestamp * 1000)
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone("Asia/Shanghai")
    return format.format(date)
}

fun createFileFromUri(context: Context, uri: Uri): File {
    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
    val fileName = getFileNameFromUri(context, uri)
    val file = File(context.cacheDir, fileName)
    val outputStream = FileOutputStream(file)
    inputStream?.copyTo(outputStream)
    inputStream?.close()
    outputStream.close()
    return file
}

fun getFileNameFromUri(context: Context, uri: Uri): String {
    var fileName = "temp_file"
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            fileName = it.getString(it.getColumnIndexOrThrow("_display_name"))
        }
    }
    return fileName
}

fun handleFileUpload(
    context: Context,
    uri: Uri,
    token: String?,
    callback: (String?, String?) -> Unit
) {
    val file = createFileFromUri(context, uri)
    val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
    val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)

    token?.let {
        RetrofitInstance.api.uploadFile("Bearer $token", multipartBody)
            .enqueue(object : Callback<FileItem> {
                override fun onResponse(call: Call<FileItem>, response: Response<FileItem>) {
                    if (response.isSuccessful) {
                        callback("文件上传成功: ${response.body()?.name}", null)
                    } else {
                        callback(null, "文件上传失败: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<FileItem>, t: Throwable) {
                    callback(null, "网络错误: ${t.message}")
                }
            })
    }
}