package com.prachiarora06.jeevanu_quanta

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

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
        if (uri != null) {
            imgUri = uri
            appState = AppState.IMAGE_SELECTED
        }
    }
    var threshold by remember {
        mutableFloatStateOf(185f)
    }
    var areaThreshold by remember {
        mutableFloatStateOf(20f)
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
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    item {
                        AsyncImage(
                            model = imgUri,
                            contentDescription = "Selected Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp)
                                .clip(RoundedCornerShape(28.dp))
                        )
                    }
                    item {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                "Threshold",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                            )
                            Slider(
                                value = threshold,
                                onValueChange = { threshold = it },
                                valueRange = 0f..255f
                            )
                        }
                    }
                    item {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                "Colony Size",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                            )
                            Slider(
                                value = areaThreshold,
                                onValueChange = { areaThreshold = it },
                                valueRange = 0f..100f
                            )

                        }
                    }
                }
            }

            AppState.RESULT_COMPUTED -> {}
        }
    }
}