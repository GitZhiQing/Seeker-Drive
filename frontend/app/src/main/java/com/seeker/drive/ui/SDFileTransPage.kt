package com.seeker.drive.ui

import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.unit.sp

@Composable
fun SDFileTransPage(presses: Int) {
    Text(
        text = "文件传输页面，你已经点击了浮动操作按钮 $presses 次。",
        fontSize = 24.sp,
        color = MaterialTheme.colorScheme.onBackground
    )
}