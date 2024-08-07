package com.prachiarora06.jeevanu_quanta

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
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
            appState = AppState.CROP_IMAGE
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
    var quadrantCount by remember {
        mutableStateOf(arrayOf(0))
    }
    var cropRect by remember {
        mutableStateOf(Rect(10F, 10F, 400F, 300F))
    }
    var canvasRect by remember {
        mutableStateOf(Rect.Zero)
    }
    var draggedAt by remember {
        mutableStateOf<OverlayDraggedAt?>(null)
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
                arrayOf(
                    canvasRect.center.x,
                    canvasRect.center.y,
                    canvasRect.width / 2,
                    canvasRect.height / 2,
                ),
                arrayOf(
                    cropRect.center.x,
                    cropRect.center.y,
                    cropRect.width / 2,
                    cropRect.height / 2,
                ),
                threshold.toInt(),
                colonySize.toInt()
            )
            val resultList = result.asList()
            val resultImage = resultList[0].toJava(ByteArray::class.java)
            resultBitmap = BitmapFactory.decodeByteArray(
                resultImage,
                0,
                resultImage.size
            )
            numOfColonies = resultList[1].toInt()
            quadrantCount = resultList[2].asList().map { it.toInt() }.toTypedArray()
            processingResult = false
            appState = AppState.RESULT_COMPUTED
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

            AppState.CROP_IMAGE -> {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = imgUri,
                            contentDescription = "Selected Image",
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        Canvas(
                            modifier = Modifier
                                .matchParentSize()
                                .onSizeChanged { size ->
                                    canvasRect =  Rect(Offset.Zero, size.toSize())}
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = {offset ->
                                            draggedAt = detectDraggedAt(offset, cropRect, 48F)
                                        },
                                        onDragEnd = {
                                            draggedAt = null
                                        }
                                    ) { _, dragAmount ->
                                        if (draggedAt != null) {
                                            cropRect =
                                                transformOverlay(dragAmount, cropRect, canvasRect, draggedAt!!, 48F)
                                        }
                                    }
                                }
                        ) {
                            drawRect(
                                color = Color.White,
                                size = cropRect.size,
                                topLeft = cropRect.topLeft,
                                style = Stroke(width = 4F)
                            )
                            drawCircle(
                                color = Color.White,
                                center = cropRect.topLeft,
                                radius = 10F,
                            )
                            drawCircle(
                                color = Color.White,
                                center = cropRect.topRight,
                                radius = 10F,
                            )
                            drawCircle(
                                color = Color.White,
                                center = cropRect.bottomLeft,
                                radius = 10F,
                            )
                            drawCircle(
                                color = Color.White,
                                center = cropRect.bottomRight,
                                radius = 10F,
                            )
                            drawOval(
                                color = Color.White,
                                size = cropRect.size,
                                topLeft = cropRect.topLeft,
                                style = Stroke(width = 4F)
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        ElevatedButton(
                            onClick = {appState = AppState.IMAGE_SELECTED},
                        ) {
                            Text("Crop")
                        }
                    }
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
                                    .fillMaxWidth()
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
                                        append("Total Number of colonies: ")
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("$numOfColonies")
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(bottom = 12.dp)
                                )
                                QuadrantTable(quadrantCount)
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

@Composable
fun QuadrantTable(count: Array<Int>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, MaterialTheme.colorScheme.secondary),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .border(.5.dp, MaterialTheme.colorScheme.secondary),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Number of colonies per quadrant")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .border(.5.dp, MaterialTheme.colorScheme.secondary),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "${count[1]}",
                    fontWeight = FontWeight.Bold
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .border(.5.dp, MaterialTheme.colorScheme.secondary),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "${count[0]}",
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .border(.5.dp, MaterialTheme.colorScheme.secondary),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "${count[2]}",
                    fontWeight = FontWeight.Bold
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .border(.5.dp, MaterialTheme.colorScheme.secondary),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "${count[3]}",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}