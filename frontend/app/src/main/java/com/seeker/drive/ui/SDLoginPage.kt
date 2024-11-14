package com.seeker.drive.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotApplyResult
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.seeker.drive.MainViewModel

@Composable
fun SDLoginPage(viewModel: MainViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("登录失败") },
            text = { Text("用户名或密码错误，请重试。") },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("确定")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("用户名") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),

            // trailingIcon 用于在输入框右侧添加一个图标按钮
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Default.Visibility
                else Icons.Default.VisibilityOff

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        //创建一个登录按钮
        Button(
            onClick = {
                if (viewModel.login(username, password)) {
                    viewModel.isLogin = 1
                    viewModel.navigateTo(MainViewModel.Screen.MainPage)     //跳转到主界面
                } else {
                    showDialog = true
                    username = ""
                    password = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("登录")
        }

        //创建一个注册按钮
        Button(
            onClick = {
                viewModel.navigateTo(MainViewModel.Screen.SDRegisterPage)    //跳转到注册界面
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("注册")
        }
    }
}

@Composable
private fun extracted(viewModel: MainViewModel) {
    viewModel.isLogin = 2
}


@Preview(showBackground = true)
@Composable
private fun MainPagePreview() {
    SDLoginPage(viewModel = MainViewModel())
}