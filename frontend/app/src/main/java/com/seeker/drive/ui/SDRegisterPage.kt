package com.seeker.drive.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.seeker.drive.MainViewModel
import com.seeker.drive.data.RetrofitJsonInstance
import com.seeker.drive.data.User
import com.seeker.drive.data.UserCreate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SDRegisterPage(viewModel: MainViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("注册失败") },
            text = { Text(dialogMessage) },
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
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("用户名") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("确认密码") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
        Button(
            onClick = {
                when {
                    username.isEmpty() -> {
                        dialogMessage = "用户名不能为空"
                        showDialog = true
                    }

                    password.isEmpty() -> {
                        dialogMessage = "密码不能为空"
                        showDialog = true
                    }

                    password != confirmPassword -> {
                        dialogMessage = "两次输入的密码不一致"
                        showDialog = true
                    }

                    else -> {
                        val userCreate = UserCreate(username, password)
                        RetrofitJsonInstance.api.register(userCreate)
                            .enqueue(object : Callback<User> {
                                override fun onResponse(
                                    call: Call<User>,
                                    response: Response<User>
                                ) {
                                    if (response.isSuccessful) {
                                        viewModel.navigateTo(MainViewModel.Screen.SDLoginPage)
                                    } else {
                                        dialogMessage = "注册失败: ${response.message()}"
                                        showDialog = true
                                    }
                                }

                                override fun onFailure(call: Call<User>, t: Throwable) {
                                    dialogMessage = "网络错误: ${t.message}"
                                    showDialog = true
                                }
                            })
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("注册")
        }

        Button(
            onClick = {
                viewModel.navigateTo(MainViewModel.Screen.SDLoginPage)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("已有账号？登录")
        }
    }
}