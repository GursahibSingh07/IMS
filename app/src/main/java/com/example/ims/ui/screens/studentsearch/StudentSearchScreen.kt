package com.example.ims.ui.screens.studentsearch

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ims.core.MockDatabase
import com.example.ims.core.MockUserProfile
import com.example.ims.core.Role
import com.example.ims.ui.screens.dashboard.NavigationMenuPanel
import com.example.ims.ui.screens.dashboard.RightMenuPanel

private data class StudentDirectoryEntry(
    val profile: MockUserProfile,
    val batch: String,
    val scholarship: String,
    val course: String,
    val stream: String,
    val cgpa: Double
)

private data class StudentMeta(
    val batch: String,
    val scholarship: String,
    val course: String,
    val stream: String,
    val cgpa: Double
)

private val studentMetaByUsername = mapOf(
    "student1" to StudentMeta(
        batch = "2024",
        scholarship = "Merit",
        course = "B.Tech",
        stream = "Computer Science",
        cgpa = 9.1
    ),
    "student2" to StudentMeta(
        batch = "2023",
        scholarship = "Need-Based",
        course = "BBA",
        stream = "Digital Marketing",
        cgpa = 8.3
    ),
    "student3" to StudentMeta(
        batch = "2022",
        scholarship = "Sports",
        course = "BFA",
        stream = "Fine Arts",
        cgpa = 7.4
    )
)

private val cgpaRanges = listOf("9.0+", "8.0 - 8.99", "7.0 - 7.99", "Below 7.0")

private fun matchesCgpaRange(cgpa: Double, selectedRange: String): Boolean {
    return when (selectedRange) {
        "9.0+" -> cgpa >= 9.0
        "8.0 - 8.99" -> cgpa in 8.0..<9.0
        "7.0 - 7.99" -> cgpa in 7.0..<8.0
        "Below 7.0" -> cgpa < 7.0
        else -> true
    }
}

@Composable
fun StudentSearchScreen(
    modifier: Modifier = Modifier,
    userProfile: MockUserProfile,
    onLogout: () -> Unit,
    onOpenTimetable: () -> Unit,
    onOpenDashboard: () -> Unit
) {
    if (userProfile.role != Role.ACADEMIC_OFFICE) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Access Denied.")
        }
        return
    }

    var isNavigationMenuOpen by rememberSaveable { mutableStateOf(false) }
    var isRightMenuOpen by rememberSaveable { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var showAdvancedFilters by rememberSaveable { mutableStateOf(false) }

    var selectedBatch by rememberSaveable { mutableStateOf(setOf<String>()) }
    var selectedScholarship by rememberSaveable { mutableStateOf(setOf<String>()) }
    var selectedCourse by rememberSaveable { mutableStateOf(setOf<String>()) }
    var selectedStream by rememberSaveable { mutableStateOf(setOf<String>()) }
    var selectedCgpaRange by rememberSaveable { mutableStateOf(setOf<String>()) }

    val studentDirectory = remember {
        MockDatabase.students.map { student ->
            val meta = studentMetaByUsername[student.username] ?: StudentMeta(
                batch = "2024",
                scholarship = "None",
                course = "General",
                stream = student.institute,
                cgpa = 7.0
            )

            StudentDirectoryEntry(
                profile = student,
                batch = meta.batch,
                scholarship = meta.scholarship,
                course = meta.course,
                stream = meta.stream,
                cgpa = meta.cgpa
            )
        }
    }

    val batchOptions = remember(studentDirectory) {
        studentDirectory.map { it.batch }.distinct().sortedDescending()
    }
    val scholarshipOptions = remember(studentDirectory) {
        studentDirectory.map { it.scholarship }.distinct().sorted()
    }
    val courseOptions = remember(studentDirectory) {
        studentDirectory.map { it.course }.distinct().sorted()
    }
    val streamOptions = remember(studentDirectory) {
        studentDirectory.map { it.stream }.distinct().sorted()
    }

    val filteredStudents = studentDirectory.filter { entry ->
        val queryMatches = query.isBlank() ||
            entry.profile.displayName.contains(query, ignoreCase = true) ||
            entry.profile.username.contains(query, ignoreCase = true) ||
            entry.course.contains(query, ignoreCase = true) ||
            entry.stream.contains(query, ignoreCase = true)

        val batchMatches = selectedBatch.isEmpty() || entry.batch in selectedBatch
        val scholarshipMatches = selectedScholarship.isEmpty() || entry.scholarship in selectedScholarship
        val courseMatches = selectedCourse.isEmpty() || entry.course in selectedCourse
        val streamMatches = selectedStream.isEmpty() || entry.stream in selectedStream
        val cgpaMatches = selectedCgpaRange.isEmpty() || selectedCgpaRange.any { matchesCgpaRange(entry.cgpa, it) }

        queryMatches && batchMatches && scholarshipMatches && courseMatches && streamMatches && cgpaMatches
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF7F9FB))
        ) {
            SearchTopBar(
                displayName = userProfile.displayName,
                onOpenNavigation = { isNavigationMenuOpen = true },
                onOpenRightMenu = { isRightMenuOpen = true }
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Student Directory",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF00113A)
                )

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search by name or username") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Advanced Search",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFF00113A),
                        fontWeight = FontWeight.Bold
                    )

                    TextButton(onClick = { showAdvancedFilters = !showAdvancedFilters }) {
                        Text(if (showAdvancedFilters) "Hide" else "Show")
                    }
                }

                AnimatedVisibility(visible = showAdvancedFilters) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        MultiSelectChipRow(
                            label = "Batch",
                            selectedValues = selectedBatch,
                            options = batchOptions,
                            onValuesChanged = { selectedBatch = it }
                        )
                        MultiSelectChipRow(
                            label = "Scholarship",
                            selectedValues = selectedScholarship,
                            options = scholarshipOptions,
                            onValuesChanged = { selectedScholarship = it }
                        )
                        MultiSelectChipRow(
                            label = "Course",
                            selectedValues = selectedCourse,
                            options = courseOptions,
                            onValuesChanged = { selectedCourse = it }
                        )
                        MultiSelectChipRow(
                            label = "Stream",
                            selectedValues = selectedStream,
                            options = streamOptions,
                            onValuesChanged = { selectedStream = it }
                        )
                        MultiSelectChipRow(
                            label = "CGPA",
                            selectedValues = selectedCgpaRange,
                            options = cgpaRanges,
                            onValuesChanged = { selectedCgpaRange = it }
                        )

                        TextButton(
                            onClick = {
                                selectedBatch = setOf()
                                selectedScholarship = setOf()
                                selectedCourse = setOf()
                                selectedStream = setOf()
                                selectedCgpaRange = setOf()
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Reset Filters")
                        }
                    }
                }

                Text(
                    text = "Results (${filteredStudents.size})",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF64748B)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredStudents) { student ->
                        StudentListItem(student)
                    }
                }

                if (filteredStudents.isEmpty()) {
                    Text(
                        text = "No students match the selected filters.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        if (isNavigationMenuOpen || isRightMenuOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xAA191C1E))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        isNavigationMenuOpen = false
                        isRightMenuOpen = false
                    }
            )
        }

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopStart),
            visible = isNavigationMenuOpen,
            enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
        ) {
            NavigationMenuPanel(
                userRole = userProfile.role,
                onOpenDashboard = {
                    isNavigationMenuOpen = false
                    onOpenDashboard()
                },
                onOpenTimetable = {
                    isNavigationMenuOpen = false
                    onOpenTimetable()
                },
                onOpenStudentRegistry = { isNavigationMenuOpen = false },
                onOpenSettings = {
                    isNavigationMenuOpen = false
                    isRightMenuOpen = true
                },
                onOpenLogout = {
                    isNavigationMenuOpen = false
                    onLogout()
                }
            )
        }

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopEnd),
            visible = isRightMenuOpen,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
        ) {
            RightMenuPanel(onLogout = onLogout)
        }
    }
}

@Composable
private fun SearchTopBar(
    displayName: String,
    onOpenNavigation: () -> Unit,
    onOpenRightMenu: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(56.dp)
            .background(Color(0xFF00113A))
            .padding(horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onOpenNavigation) {
                    Icon(Icons.Outlined.Menu, contentDescription = null, tint = Color.White)
                }
                Text(text = "IMS Search", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color(0xFF27457A), RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayName.take(1),
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onOpenRightMenu) {
                    Icon(Icons.Outlined.MoreVert, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun MultiSelectChipRow(
    label: String,
    selectedValues: Set<String>,
    options: List<String>,
    onValuesChanged: (Set<String>) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF334155)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = option in selectedValues,
                    onClick = {
                        val newSet = selectedValues.toMutableSet()
                        if (option in newSet) {
                            newSet.remove(option)
                        } else {
                            newSet.add(option)
                        }
                        onValuesChanged(newSet)
                    },
                    label = { Text(option) }
                )
            }
        }
    }
}

@Composable
private fun StudentListItem(student: StudentDirectoryEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDDE2E8)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = student.profile.displayName.take(1),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF00113A)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.profile.displayName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF00113A)
                )
                Text(
                    text = "${student.profile.username} • ${student.course}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "Batch ${student.batch} • ${student.stream} • CGPA ${"%.1f".format(student.cgpa)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF475569)
                )
                Text(
                    text = "Scholarship: ${student.scholarship}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF475569)
                )
                Text(
                    text = student.profile.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF27457A)
                )
            }
        }
    }
}
