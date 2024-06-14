package com.prachiarora06.jeevanu_quanta

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.chaquo.python.PyObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHome(colCount: PyObject, contentResolver: ContentResolver, navController: NavController) {
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
    var numOfColonies by remember {
        mutableIntStateOf(0)
    }
    var colonySize by remember {
        mutableFloatStateOf(20f)
    }
    var expanded by remember {
        mutableStateOf(false)
    }
    var resultBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }
    var processingResult by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val computeResult: () -> Unit = {
        processingResult = true
        scope.launch(Dispatchers.Default) {
            val result = colCount.callAttr(
                "colCount",
                contentResolver.openInputStream(imgUri!!)?.use {
                    it.readBytes()
                },
                threshold.toInt(),
                colonySize.toInt()
            )
            numOfColonies = result.asList()[1].toInt()
            val resultImage = result.asList()[0].toJava(ByteArray::class.java)
            resultBitmap = BitmapFactory.decodeByteArray(
                resultImage,
                0,
                resultImage.size
            )
            appState = AppState.RESULT_COMPUTED
            processingResult = false
        }
    }
    val slidersAndButtons = @Composable {
        Column {
            if (processingResult) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                )
            } else {
                CountButton(computeResult)
            }
            ThresholdSlider(threshold) { threshold = it }
            ColonySizeSlider(colonySize) { colonySize = it }
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
                    IconButton(onClick = {
                        expanded = !expanded
                    }) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = "Menu"
                        )
                    }
                })
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
                .wrapContentSize(Alignment.TopEnd)
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = !expanded },
                modifier = Modifier
            ) {
                DropdownMenuItem(
                    text = {
                        Text("About")
                    },
                    onClick = {
                        navController.navigate("AboutPage")
                        expanded = !expanded
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text("Help")
                    },
                    onClick = {
                        navController.navigate("HelpPage")
                        expanded = !expanded
                    }
                )

            }
        }
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
                        slidersAndButtons()
                    }
                }
            }

            AppState.RESULT_COMPUTED -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    item {
                        AsyncImage(
                            model = resultBitmap,
                            contentDescription = "Result Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp)
                                .clip(RoundedCornerShape(28.dp))
                        )
                    }
                    item {
                        slidersAndButtons()
                    }
                    item {
                        Card(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                Text(
                                    "Results",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    modifier = Modifier
                                        .padding(bottom = 8.dp)
                                )
                                Text(
                                    buildAnnotatedString {
                                        append("Number of Colonies: ")
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("$numOfColonies")
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CountButton(computeResult: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        ElevatedButton(onClick = computeResult) {
            Text("Count")
        }
    }
}

@Composable
fun ColonySizeSlider(colonySize: Float, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            "Colony Size",
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 8.dp)
        )
        Slider(
            value = colonySize,
            onValueChange = onValueChange,
            valueRange = 0f..100f
        )

    }
}

@Composable
fun ThresholdSlider(threshold: Float, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            "Threshold",
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 8.dp)
        )
        Slider(
            value = threshold,
            onValueChange = onValueChange,
            valueRange = 0f..255f
        )
    }
}