package com.seeker.drive.ui

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.seeker.drive.R
import com.seeker.drive.createFileFromUri
import com.seeker.drive.data.RetrofitInstance
import com.seeker.drive.data.User
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SDProfilePageContent(
    username: String,
    userInfo: String,
    avatar: String,
    uploadError: String?,
    onPickImage: () -> Unit,
    onLogout: () -> Unit,
    drawerState: DrawerState
) {
    val widthFraction = if (drawerState.isClosed) 0.1f else 0.85f

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(widthFraction)
            .background(Color.White)
            .padding(top = 64.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            if (avatar.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(avatar),
                    contentDescription = "Profile Avatar",
                    modifier = Modifier.size(64.dp)
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.account_circle),
                    contentDescription = "Profile Icon",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = username,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = userInfo,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onPickImage,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "设置头像")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(text = "退出登录")
        }
        uploadError?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }
    }
}

fun handleImageUpload(
    context: Context,
    uri: Uri,
    token: String?,
    callback: (String?, String?) -> Unit
) {
    val file = createFileFromUri(context, uri)
    val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
    val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)

    token?.let {
        RetrofitInstance.api.updateAvatar("Bearer $token", multipartBody)
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        callback(
                            context.getString(R.string.api_protocol) +
                                    context.getString(R.string.api_host) +
                                    context.getString(R.string.api_port) +
                                    user?.avatar, null
                        )
                    } else {
                        callback(null, "上传头像失败: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    callback(null, "网络错误: ${t.message}")
                }
            })
    }
}
