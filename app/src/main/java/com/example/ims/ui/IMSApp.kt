package com.example.ims.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ims.core.MockUserProfile
import com.example.ims.core.Role
import com.example.ims.ui.navigation.AppDestination
import com.example.ims.ui.screens.dashboard.DashboardScreen
import com.example.ims.ui.screens.login.LoginScreen
import com.example.ims.ui.screens.studentsearch.StudentSearchScreen
import com.example.ims.ui.screens.timetable.TimetableScreen

@Composable
fun IMSApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestination.LOGIN) }
    var activeUser by rememberSaveable(stateSaver = MockUserProfileSaver) { mutableStateOf<MockUserProfile?>(null) }

    BackHandler(
        enabled = currentDestination != AppDestination.DASHBOARD &&
            currentDestination != AppDestination.LOGIN
    ) {
        currentDestination = AppDestination.DASHBOARD
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        val user = activeUser
        when (currentDestination) {
            AppDestination.LOGIN -> LoginScreen(
                modifier = Modifier.padding(innerPadding),
                onLoginSuccess = { profile ->
                    activeUser = profile
                    currentDestination = AppDestination.DASHBOARD
                }
            )

            AppDestination.DASHBOARD -> {
                if (user == null) {
                    currentDestination = AppDestination.LOGIN
                } else {
                    DashboardScreen(
                        modifier = Modifier.padding(innerPadding),
                        userProfile = user,
                        onOpenTimetable = { currentDestination = AppDestination.TIMETABLE },
                        onOpenStudentSearch = { currentDestination = AppDestination.STUDENT_SEARCH },
                        onLogout = {
                            activeUser = null
                            currentDestination = AppDestination.LOGIN
                        }
                    )
                }
            }

            AppDestination.TIMETABLE -> {
                if (user != null) {
                    TimetableScreen(
                        modifier = Modifier.padding(innerPadding),
                        userProfile = user,
                        onLogout = {
                            activeUser = null
                            currentDestination = AppDestination.LOGIN
                        },
                        onOpenStudentSearch = { currentDestination = AppDestination.STUDENT_SEARCH },
                        onOpenDashboard = { currentDestination = AppDestination.DASHBOARD }
                    )
                } else {
                    currentDestination = AppDestination.LOGIN
                }
            }
            AppDestination.STUDENT_SEARCH -> {
                if (user != null) {
                    StudentSearchScreen(
                        modifier = Modifier.padding(innerPadding),
                        userProfile = user,
                        onLogout = {
                            activeUser = null
                            currentDestination = AppDestination.LOGIN
                        },
                        onOpenTimetable = { currentDestination = AppDestination.TIMETABLE },
                        onOpenDashboard = { currentDestination = AppDestination.DASHBOARD }
                    )
                } else {
                    currentDestination = AppDestination.LOGIN
                }
            }
        }
    }
}

private val MockUserProfileSaver = androidx.compose.runtime.saveable.Saver<MockUserProfile?, String>(
    save = { profile ->
        profile?.let { "${it.displayName}|${it.role.name}|${it.institute}|${it.email}|${it.username}" }
    },
    restore = { encoded ->
        try {
            val parts = encoded.split('|')
            if (parts.size == 5) {
                MockUserProfile(
                    displayName = parts[0],
                    role = Role.valueOf(parts[1]),
                    institute = parts[2],
                    email = parts[3],
                    username = parts[4]
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
)
