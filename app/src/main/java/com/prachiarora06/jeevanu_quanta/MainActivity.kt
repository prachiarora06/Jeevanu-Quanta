package com.prachiarora06.jeevanu_quanta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.prachiarora06.jeevanu_quanta.ui.theme.JeevanuQuantaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JeevanuQuantaTheme {
                AppHome()
            }
        }
    }
}