package com.example.ims.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.ims.core.AppDataFile
import com.example.ims.core.AppDataStore
import com.example.ims.core.MockUserProfile
import com.example.ims.core.Role
import com.example.ims.core.toUserProfile
import com.example.ims.ui.navigation.AppDestination
import com.example.ims.ui.screens.dashboard.DashboardScreen
import com.example.ims.ui.screens.login.LoginScreen
import com.example.ims.ui.screens.studentsearch.StudentProfileScreen
import com.example.ims.ui.screens.studentsearch.StudentSearchScreen
import com.example.ims.ui.screens.timetable.TimetableScreen
import kotlinx.coroutines.launch

@Composable
fun IMSApp() {
    val context = LocalContext.current
    val appDataStore = remember(context) { AppDataStore(context.applicationContext) }
    val scope = rememberCoroutineScope()

    var currentDestination by rememberSaveable { mutableStateOf(AppDestination.LOGIN) }
    var activeUser by rememberSaveable(stateSaver = MockUserProfileSaver) { mutableStateOf<MockUserProfile?>(null) }
    var selectedStudentUsername by rememberSaveable { mutableStateOf<String?>(null) }
    var profileBackDestination by rememberSaveable { mutableStateOf(AppDestination.DASHBOARD.name) }
    var appData by remember { mutableStateOf<AppDataFile?>(null) }

    LaunchedEffect(appDataStore) {
        appData = appDataStore.load()
    }

    fun updateAppData(transform: (AppDataFile) -> AppDataFile) {
        val current = appData ?: return
        val updated = transform(current)
        appData = updated
        scope.launch {
            appDataStore.save(updated)
        }
    }

    if (appData == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val data = appData!!
    val userRecordsByUsername = remember(data.users) { data.users.associateBy { it.username } }
    val userProfiles = remember(data.users) { data.users.map { it.toUserProfile() } }
    val students = remember(userProfiles) { userProfiles.filter { it.role == Role.STUDENT } }
    val studentRecordsByUsername = remember(data.students) { data.students.associateBy { it.username } }

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
                quickUsers = userProfiles,
                onValidateCredentials = { username, password ->
                    data.users.firstOrNull {
                        it.username == username && it.password == password
                    }?.toUserProfile()
                },
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
                        onOpenMyProfile = {
                            selectedStudentUsername = user.username
                            profileBackDestination = AppDestination.DASHBOARD.name
                            currentDestination = AppDestination.STUDENT_PROFILE
                        },
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
                        timetables = data.timetables,
                        primaryTimetableId = data.primaryTimetableId,
                        onTimetablesChange = { updatedTimetables ->
                            updateAppData { current ->
                                current.copy(timetables = updatedTimetables)
                            }
                        },
                        onPrimaryTimetableChange = { timetableId ->
                            updateAppData { current ->
                                current.copy(primaryTimetableId = timetableId)
                            }
                        },
                        onLogout = {
                            activeUser = null
                            currentDestination = AppDestination.LOGIN
                        },
                        onOpenStudentSearch = { currentDestination = AppDestination.STUDENT_SEARCH },
                        onOpenMyProfile = {
                            selectedStudentUsername = user.username
                            profileBackDestination = AppDestination.TIMETABLE.name
                            currentDestination = AppDestination.STUDENT_PROFILE
                        },
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
                        students = students,
                        studentRecords = data.students,
                        onLogout = {
                            activeUser = null
                            currentDestination = AppDestination.LOGIN
                        },
                        onOpenTimetable = { currentDestination = AppDestination.TIMETABLE },
                        onOpenDashboard = { currentDestination = AppDestination.DASHBOARD },
                        onOpenStudentProfile = { username ->
                            selectedStudentUsername = username
                            profileBackDestination = AppDestination.STUDENT_SEARCH.name
                            currentDestination = AppDestination.STUDENT_PROFILE
                        }
                    )
                } else {
                    currentDestination = AppDestination.LOGIN
                }
            }

            AppDestination.STUDENT_PROFILE -> {
                val viewer = user
                val targetUsername = selectedStudentUsername ?: viewer?.username

                if (viewer == null || targetUsername == null) {
                    currentDestination = AppDestination.LOGIN
                } else {
                    val targetUser = userRecordsByUsername[targetUsername]?.toUserProfile()
                    val targetStudent = studentRecordsByUsername[targetUsername]
                    val canView = when (viewer.role) {
                        Role.ACADEMIC_OFFICE -> true
                        Role.STUDENT -> viewer.username == targetUsername
                        else -> false
                    }

                    if (targetUser == null || targetStudent == null) {
                        currentDestination = AppDestination.DASHBOARD
                    } else {
                        StudentProfileScreen(
                            modifier = Modifier.padding(innerPadding),
                            profile = targetUser,
                            details = targetStudent,
                            canView = canView,
                            onBack = {
                                currentDestination = AppDestination.valueOf(profileBackDestination)
                            }
                        )
                    }
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
