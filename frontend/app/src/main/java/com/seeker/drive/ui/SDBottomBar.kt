package com.seeker.drive.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.seeker.drive.MainViewModel
import com.seeker.drive.R


@Composable
fun SDBottomBar(viewModel: MainViewModel) {
    val items = listOf("文件列表", "文件传输")

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    when (index) {
                        0 -> Icon(
                            painter = painterResource(id = R.drawable.text_box_multiple_outline),
                            contentDescription = "文件列表",
                            modifier = Modifier.size(32.dp)
                        )

                        1 -> Icon(
                            painter = painterResource(id = R.drawable.file_arrow_up_down_outline),
                            contentDescription = "文件传输",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                label = { Text(item) },
                selected = viewModel.selectedItem == index,
                onClick = { viewModel.selectedItem = index }
            )
        }
    }
}