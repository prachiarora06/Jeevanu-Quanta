package com.prachiarora06.jeevanu_quanta

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHome() {
    var appState by remember {
        mutableStateOf(AppState.IMAGE_NOT_SELECTED)
    }
    var imgUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val imgPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if(uri != null) {
            imgUri = uri
            appState = AppState.IMAGE_SELECTED
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                imgPicker.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add Image Button"
                )
            }
        },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Jeevanu Quanta")
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = "Menu"
                        )
                    }
                })
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        when (appState) {
            AppState.IMAGE_NOT_SELECTED -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Text(
                        "Select an Image",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                }
            }

            AppState.IMAGE_SELECTED -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Text(
                        "Image Selected",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                }
            }
            AppState.RESULT_COMPUTED -> {}
        }
    }
}