package com.seeker.drive.ui

import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seeker.drive.MainViewModel
import com.seeker.drive.ui.theme.SeekerDriveTheme
import kotlinx.coroutines.launch


@Composable
fun MainPage(viewModel: MainViewModel = viewModel()) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SDProfilePage()
        }
    ) {
        Scaffold(
            topBar = {
                SDTopBar(viewModel, onNavigationIconClick = {
                    coroutineScope.launch {
                        drawerState.open()
                    }
                })
            },
            bottomBar = {
                SDBottomBar(viewModel)
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.presses++ },
                    content = { Icon(Icons.Default.Add, contentDescription = "Add") },
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                when (viewModel.selectedItem) {
                    0 -> SDFileListPage(viewModel.presses)
                    1 -> SDFileTransPage(viewModel.presses)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainPagePreview() {
    SeekerDriveTheme {
        MainPage()
    }
}