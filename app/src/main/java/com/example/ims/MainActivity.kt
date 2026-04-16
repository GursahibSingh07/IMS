package com.example.ims

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.ims.core.AppIdentifierReporter
import com.example.ims.ui.IMSApp
import com.example.ims.ui.theme.IMSTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            AppIdentifierReporter.sendIfConfigured()
        }
        enableEdgeToEdge()
        setContent {
            IMSTheme {
                IMSApp()
            }
        }
    }
}