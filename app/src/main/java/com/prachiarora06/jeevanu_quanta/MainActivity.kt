package com.prachiarora06.jeevanu_quanta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
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
        val main = py.getModule("test")

        setContent {
            JeevanuQuantaTheme {
                Text(main.callAttr("main").toString())
                //AppHome()
            }
        }
    }
}