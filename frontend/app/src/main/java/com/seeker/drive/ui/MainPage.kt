package com.seeker.drive.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seeker.drive.MainViewModel
import com.seeker.drive.handleFileUpload
import kotlinx.coroutines.launch


@Composable
fun MainPage(viewModel: MainViewModel = viewModel()) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            handleFileUpload(context, it, viewModel.token) { result, error ->
                viewModel.uploadResults.add(result ?: error ?: "Unknown error")
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SDProfilePage(viewModel, drawerState)
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                SDTopBar(onNavigationIconClick = {
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
                    onClick = {
                        launcher.launch("*/*")
                    },
                    content = { Icon(Icons.Default.Add, contentDescription = "Add") },
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                when (viewModel.selectedItem) {
                    0 -> SDFileListPage(viewModel)
                    1 -> SDFileTransPage(viewModel)
                }
            }
        }
    }
}