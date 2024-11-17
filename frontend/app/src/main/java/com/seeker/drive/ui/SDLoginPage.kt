package com.seeker.drive.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.seeker.drive.MainViewModel
import com.seeker.drive.data.OAuth2LoginResponse
import com.seeker.drive.data.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SDLoginPage(viewModel: MainViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("登录失败") },
            text = { Text(errorMessage ?: "未知错误，请重试。") },
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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SDLogo()
        Spacer(modifier = Modifier.height(16.dp))
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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                RetrofitInstance.api.login("password", username, password)
                    .enqueue(object : Callback<OAuth2LoginResponse> {
                        override fun onResponse(
                            call: Call<OAuth2LoginResponse>,
                            response: Response<OAuth2LoginResponse>
                        ) {
                            if (response.isSuccessful) {
                                val token = response.body()?.access_token
                                if (token != null) {
                                    viewModel.saveToken(token)
                                }
                            } else {
                                errorMessage = "登录失败: ${response.message()}"
                                showDialog = true
                            }
                        }

                        override fun onFailure(call: Call<OAuth2LoginResponse>, t: Throwable) {
                            errorMessage = "网络错误: ${t.message}"
                            showDialog = true
                        }
                    })
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("登录")
        }

        errorMessage?.let {
            Text("Error: $it", color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                viewModel.navigateTo(MainViewModel.Screen.SDRegisterPage)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("没有账号？注册")
        }
    }
}