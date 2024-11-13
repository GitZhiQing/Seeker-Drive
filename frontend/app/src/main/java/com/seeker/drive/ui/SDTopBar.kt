package com.seeker.drive.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.seeker.drive.MainViewModel
import com.seeker.drive.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SDTopBar(viewModel: MainViewModel, onNavigationIconClick: () -> Unit) {
    TopAppBar(
        title = {
            Text("Seeker Drive")
        },
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    painter = painterResource(id = R.drawable.tape_drive),
                    contentDescription = "Tape Drive",
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xffd8e2ff), // 设置背景色为浅蓝色
            titleContentColor = Color.Black, // 设置标题文字颜色为黑色
            navigationIconContentColor = Color.Black // 设置导航图标颜色为黑色
        )
    )
}