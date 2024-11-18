package com.seeker.drive.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.*
import com.seeker.drive.MainViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.seeker.drive.R
import com.seeker.drive.convertToShanghaiTime
import com.seeker.drive.fetchCurrentUser


@Composable
fun SDProfilePage(viewModel: MainViewModel, drawerState: DrawerState) {
    var username by remember { mutableStateOf("Loading...") }
    var userInfo by remember { mutableStateOf("Loading...") }
    var avatar by remember { mutableStateOf("") }
    var uploadError by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            handleImageUpload(context, it, viewModel.token) { newAvatar, error ->
                avatar = newAvatar ?: avatar
                uploadError = error
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.token?.let { token ->
            coroutineScope.launch {
                fetchCurrentUser(token) { user, error ->
                    if (user != null) {
                        username = user.username
                        avatar = context.getString(R.string.api_protocol) +
                                context.getString(R.string.api_host) +
                                context.getString(R.string.api_port) +
                                "/static${user.avatar}"
                        userInfo =
                            "注册时间: ${user.register_time?.let { convertToShanghaiTime(it) }}"
                    } else {
                        username = "加载失败"
                        userInfo = error ?: "加载用户信息失败"
                    }
                }
            }
        } ?: run {
            username = "未登录"
            userInfo = "请先登录"
        }
    }

    SDProfilePageContent(
        username = username,
        userInfo = userInfo,
        avatar = avatar,
        uploadError = uploadError,
        onPickImage = { imagePickerLauncher.launch("image/*") },
        onLogout = { viewModel.logout() },
        drawerState = drawerState
    )
}

