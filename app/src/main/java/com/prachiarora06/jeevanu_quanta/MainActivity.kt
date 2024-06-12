package com.prachiarora06.jeevanu_quanta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.prachiarora06.jeevanu_quanta.ui.theme.JeevanuQuantaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if(!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        val py = Python.getInstance()
        val colCount = py.getModule("colCount")

        setContent {
            JeevanuQuantaTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "AppHome") {
                    composable("AppHome") {
                        AppHome(navController)
                    }
                    composable("AboutPage") {
                        AboutPage(navController)
                    }
                    composable("HelpPage") {
                        HelpPage(navController)
                    }
                }
            }
        }
    }
}