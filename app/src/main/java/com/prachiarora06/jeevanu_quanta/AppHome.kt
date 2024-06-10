package com.prachiarora06.jeevanu_quanta

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppHome() {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add Image Button")
            }
        },
        modifier = Modifier.fillMaxSize()) { innerPadding ->
        Text(
            "Hello World!",
            modifier = Modifier.padding(innerPadding)
        )
    }
}