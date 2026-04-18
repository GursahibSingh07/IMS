package com.example.ims.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ims.ui.navigation.AppDestination
import com.example.ims.ui.screens.dashboard.DashboardScreen
import com.example.ims.ui.screens.login.LoginScreen
import com.example.ims.ui.screens.studentsearch.StudentSearchScreen
import com.example.ims.ui.screens.timetable.TimetableScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IMSApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestination.LOGIN) }

    BackHandler(
        enabled = currentDestination != AppDestination.DASHBOARD &&
            currentDestination != AppDestination.LOGIN
    ) {
        currentDestination = AppDestination.DASHBOARD
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentDestination != AppDestination.LOGIN) {
                TopAppBar(title = { Text(text = currentDestination.title) })
            }
        }
    ) { innerPadding ->
        when (currentDestination) {
            AppDestination.LOGIN -> LoginScreen(
                modifier = Modifier.padding(innerPadding),
                onLoginClick = { currentDestination = AppDestination.DASHBOARD }
            )

            AppDestination.DASHBOARD -> DashboardScreen(
                modifier = Modifier.padding(innerPadding),
                onOpenTimetable = { currentDestination = AppDestination.TIMETABLE },
                onOpenStudentSearch = { currentDestination = AppDestination.STUDENT_SEARCH }
            )

            AppDestination.TIMETABLE -> TimetableScreen(modifier = Modifier.padding(innerPadding))
            AppDestination.STUDENT_SEARCH -> StudentSearchScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}

private val AppDestination.title: String
    get() = when (this) {
        AppDestination.LOGIN -> "IMS Login"
        AppDestination.DASHBOARD -> "IMS Dashboard"
        AppDestination.TIMETABLE -> "Time Table"
        AppDestination.STUDENT_SEARCH -> "Student Search"
    }
