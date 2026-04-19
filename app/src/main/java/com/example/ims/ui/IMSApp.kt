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
        when (currentDestination) {
            AppDestination.LOGIN -> LoginScreen(
                modifier = Modifier.padding(innerPadding),
                onLoginSuccess = { profile ->
                    activeUser = profile
                    currentDestination = AppDestination.DASHBOARD
                }
            )

            AppDestination.DASHBOARD -> {
                val userProfile = activeUser
                if (userProfile == null) {
                    LoginScreen(
                        modifier = Modifier.padding(innerPadding),
                        onLoginSuccess = { profile ->
                            activeUser = profile
                            currentDestination = AppDestination.DASHBOARD
                        }
                    )
                } else {
                    DashboardScreen(
                        modifier = Modifier.padding(innerPadding),
                        userProfile = userProfile,
                        onOpenTimetable = { currentDestination = AppDestination.TIMETABLE },
                        onOpenStudentSearch = { currentDestination = AppDestination.STUDENT_SEARCH },
                        onLogout = {
                            activeUser = null
                            currentDestination = AppDestination.LOGIN
                        }
                    )
                }
            }

            AppDestination.TIMETABLE -> TimetableScreen(modifier = Modifier.padding(innerPadding))
            AppDestination.STUDENT_SEARCH -> StudentSearchScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}

private val MockUserProfileSaver = androidx.compose.runtime.saveable.Saver<MockUserProfile?, String>(
    save = { profile ->
        profile?.let { "${it.displayName}|${it.role}|${it.institute}|${it.email}" }
    },
    restore = { encoded ->
        val parts = encoded.split('|')
        if (parts.size == 4) {
            MockUserProfile(
                displayName = parts[0],
                role = parts[1],
                institute = parts[2],
                email = parts[3]
            )
        } else {
            null
        }
    }
)
