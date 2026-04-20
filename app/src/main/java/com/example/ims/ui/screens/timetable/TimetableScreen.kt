package com.example.ims.ui.screens.timetable

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ims.core.MockUserProfile
import com.example.ims.core.Role
import com.example.ims.ui.screens.dashboard.NavigationMenuPanel
import com.example.ims.ui.screens.dashboard.RightMenuPanel

private data class ScheduleCell(
    val code: String,
    val title: String,
    val room: String,
    val faculty: String,
    val department: String,
    val courseName: String,
    val timeSlot: String,
    val bgColor: Color,
    val textColor: Color
)

private data class CourseEntry(
    val faculty: String,
    val department: String,
    val courseName: String,
    val room: String,
    val timeSlot: String
)

private val facultyList = listOf(
    "Dr. Aris Thorne",
    "Prof. Sarah Lee",
    "Dr. James Bond",
    "Dr. Maria Garcia",
    "Prof. John Smith"
)

private val departmentList = listOf(
    "Computer Science",
    "Digital Marketing",
    "Security Studies",
    "Business Administration",
    "Fine Arts"
)

private val courseNameList = listOf(
    "Data Structures",
    "Web Development",
    "Database Management",
    "Cloud Computing",
    "Cybersecurity Basics",
    "Marketing Analytics",
    "Business Law"
)

private val roomList = listOf(
    "A101",
    "A102",
    "B201",
    "B202",
    "C301",
    "C302",
    "Auditorium 1"
)

private val timeSlotList = listOf(
    "09:00 AM - 10:30 AM",
    "10:45 AM - 12:15 PM",
    "01:00 PM - 02:30 PM",
    "02:45 PM - 04:15 PM",
    "04:30 PM - 06:00 PM",
    "06:15 PM - 07:45 PM"
)

private val initialTimetableEntries = listOf(
    CourseEntry("Dr. Aris Thorne", "Computer Science", "Intro to CS", "Hall A", "09:00 AM - 10:30 AM"),
    CourseEntry("Prof. Sarah Lee", "Digital Marketing", "Marketing Strategy", "R 302", "09:00 AM - 10:30 AM"),
    CourseEntry("Dr. James Bond", "Security Studies", "Cybersecurity Basics", "Lab 4", "09:00 AM - 10:30 AM"),
    CourseEntry("Dr. Maria Garcia", "Fine Arts", "Fine Arts", "Annex", "11:00 AM - 12:30 PM"),
    CourseEntry("Prof. John Smith", "Business Administration", "Business Law", "Hall C", "11:00 AM - 12:30 PM")
)

private fun CourseEntry.toScheduleCell(codeFallback: String, bgColor: Color, textColor: Color): ScheduleCell {
    return ScheduleCell(
        code = codeFallback,
        title = courseName,
        room = room,
        faculty = faculty,
        department = department,
        courseName = courseName,
        timeSlot = timeSlot,
        bgColor = bgColor,
        textColor = textColor
    )
}

@Composable
fun TimetableScreen(
    modifier: Modifier = Modifier,
    userProfile: MockUserProfile,
    onLogout: () -> Unit,
    onOpenStudentSearch: () -> Unit,
    onOpenDashboard: () -> Unit
) {
    var showCourseCreation by rememberSaveable { mutableStateOf(false) }
    var isNavigationMenuOpen by rememberSaveable { mutableStateOf(false) }
    var isRightMenuOpen by rememberSaveable { mutableStateOf(false) }
    var timetableEntries by rememberSaveable { mutableStateOf(initialTimetableEntries) }

    Box(modifier = modifier.fillMaxSize()) {
        if (showCourseCreation && userProfile.role == Role.ACADEMIC_OFFICE) {
            CourseCreationScreen(
                existingEntries = timetableEntries,
                onBackToTimetableList = { showCourseCreation = false },
                onSaveCourse = { entry ->
                    timetableEntries = timetableEntries + entry
                    showCourseCreation = false
                }
            )
        } else {
            TimetableListScreen(
                userProfile = userProfile,
                entries = timetableEntries,
                onOpenCourseCreation = { showCourseCreation = true },
                onOpenNavigation = { isNavigationMenuOpen = true },
                onOpenRightMenu = { isRightMenuOpen = true }
            )
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
                onOpenTimetable = { isNavigationMenuOpen = false },
                onOpenStudentRegistry = {
                    isNavigationMenuOpen = false
                    onOpenStudentSearch()
                },
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
private fun TimetableListScreen(
    userProfile: MockUserProfile,
    entries: List<CourseEntry>,
    onOpenCourseCreation: () -> Unit,
    onOpenNavigation: () -> Unit,
    onOpenRightMenu: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (userProfile.role == Role.ACADEMIC_OFFICE) 152.dp else 16.dp)
        ) {
            TimetableTopBar(
                title = "IMS Time Table",
                displayName = userProfile.displayName,
                onOpenNavigation = onOpenNavigation,
                onOpenRightMenu = onOpenRightMenu
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "WEEKLY ORCHESTRATION",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF50606F),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Time Table Management",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF00113A),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))
                TimetableGrid(entries)

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "CURRENT COURSE ENTRIES",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF50606F),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    entries.forEach { entry ->
                        CourseEntryCard(entry)
                    }
                }
            }
        }

        if (userProfile.role == Role.ACADEMIC_OFFICE) {
            TimetableBottomActions(
                modifier = Modifier.align(Alignment.BottomCenter),
                onOpenCourseCreation = onOpenCourseCreation
            )
        }
    }
}

@Composable
private fun TimetableTopBar(
    title: String,
    displayName: String,
    onOpenNavigation: () -> Unit,
    onOpenRightMenu: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(56.dp)
            .background(Color(0xFF00113A))
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onOpenNavigation) {
                    Icon(Icons.Outlined.Menu, contentDescription = null, tint = Color.White)
                }
                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold)
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
private fun TimetableGrid(entries: List<CourseEntry>) {
    val monday = entries.filterIndexed { index, entry -> index % 2 == 0 }
    val tuesday = entries.filterIndexed { index, entry -> index % 2 == 1 }

    Surface(
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFFDDE2E8)),
        color = Color.White
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF2F4F6))
                    .padding(vertical = 10.dp)
            ) {
                Box(
                    modifier = Modifier.requiredWidth(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                }
                DayHeaderCell("Mon")
                DayHeaderCell("Tue")
                DayHeaderCell("Wed")
                DayHeaderCell("Thu")
                DayHeaderCell("Fri")
            }

            HorizontalDivider(color = Color(0xFFE7EBF0))

            TimeSlotRow(
                timeLabel = "09:00",
                cells = listOf(
                    monday.getOrNull(0)?.toScheduleCell("CS101", Color(0xFFDBE1FF), Color(0xFF00113A)),
                    null,
                    tuesday.getOrNull(0)?.toScheduleCell("MKT201", Color(0xFFD4E4F6), Color(0xFF324255)),
                    null,
                    entries.getOrNull(2)?.toScheduleCell("CS102", Color(0xFFCFF0DE), Color(0xFF084D31))
                )
            )

            HorizontalDivider(color = Color(0xFFE7EBF0))

            TimeSlotRow(
                timeLabel = "11:00",
                cells = listOf(
                    entries.getOrNull(3)?.toScheduleCell("ART105", Color(0xFFFDE8E8), Color(0xFF9B1C1C)),
                    null,
                    entries.getOrNull(4)?.toScheduleCell("ENG301", Color(0xFFFEF3C7), Color(0xFF92400E)),
                    null,
                    null
                )
            )
        }
    }
}

@Composable
private fun RowScope.DayHeaderCell(day: String) {
    Box(
        modifier = Modifier.weight(1f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF64748B),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TimeSlotRow(
    timeLabel: String,
    cells: List<ScheduleCell?>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(80.dp)
    ) {
        Box(
            modifier = Modifier
                .requiredWidth(60.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = timeLabel,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF64748B)
            )
        }

        cells.forEach { cell ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(2.dp)
            ) {
                if (cell != null) {
                    Card(
                        shape = RoundedCornerShape(4.dp),
                        colors = CardDefaults.cardColors(containerColor = cell.bgColor),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = cell.code,
                                style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = cell.textColor
                            )
                            Text(
                                text = cell.title,
                                style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.SemiBold),
                                color = cell.textColor
                            )
                            Text(
                                text = cell.room,
                                style = TextStyle(fontSize = 9.sp),
                                color = cell.textColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseEntryCard(entry: CourseEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = entry.courseName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00113A)
            )
            Text(
                text = "${entry.faculty} • ${entry.department}",
                color = Color(0xFF475569),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "${entry.room} • ${entry.timeSlot}",
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun TimetableBottomActions(
    modifier: Modifier = Modifier,
    onOpenCourseCreation: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onOpenCourseCreation,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00113A))
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("ADD COURSE")
            }
        }
    }
}

@Composable
private fun CourseCreationScreen(
    existingEntries: List<CourseEntry>,
    onBackToTimetableList: () -> Unit,
    onSaveCourse: (CourseEntry) -> Unit
) {
    var facultyName by rememberSaveable { mutableStateOf("") }
    var department by rememberSaveable { mutableStateOf("") }
    var courseName by rememberSaveable { mutableStateOf("") }
    var room by rememberSaveable { mutableStateOf("") }
    var timeSlot by rememberSaveable { mutableStateOf("") }
    var validationError by rememberSaveable { mutableStateOf<String?>(null) }

    fun submitCourse() {
        val trimmedFaculty = facultyName.trim()
        val trimmedDepartment = department.trim()
        val trimmedCourse = courseName.trim()
        val trimmedRoom = room.trim()
        val trimmedTimeSlot = timeSlot.trim()

        validationError = when {
            trimmedFaculty.isBlank() || trimmedDepartment.isBlank() || trimmedCourse.isBlank() || trimmedRoom.isBlank() || trimmedTimeSlot.isBlank() -> {
                "Select a value for every field."
            }
            existingEntries.any { it.timeSlot == trimmedTimeSlot && it.faculty.equals(trimmedFaculty, ignoreCase = true) } -> {
                "${trimmedFaculty} is already assigned at ${trimmedTimeSlot}."
            }
            existingEntries.any { it.timeSlot == trimmedTimeSlot && it.room.equals(trimmedRoom, ignoreCase = true) } -> {
                "Room ${trimmedRoom} is already booked at ${trimmedTimeSlot}."
            }
            else -> null
        }

        if (validationError == null) {
            onSaveCourse(
                CourseEntry(
                    faculty = trimmedFaculty,
                    department = trimmedDepartment,
                    courseName = trimmedCourse,
                    room = trimmedRoom,
                    timeSlot = trimmedTimeSlot
                )
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF7F9FB))) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(56.dp)
                .background(Color(0xFF00113A))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Add Timetable Entry", color = Color.White, fontWeight = FontWeight.Bold)
                Text(
                    text = "Cancel",
                    color = Color.White,
                    modifier = Modifier.clickable { onBackToTimetableList() },
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "COURSE SELECTION",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
                color = Color(0xFF50606F),
                fontWeight = FontWeight.Bold
            )

            SearchableDropdownField(
                label = "Faculty Name",
                placeholder = "Search faculty",
                value = facultyName,
                options = facultyList,
                onValueChange = { facultyName = it }
            )

            SearchableDropdownField(
                label = "Department",
                placeholder = "Search department",
                value = department,
                options = departmentList,
                onValueChange = { department = it }
            )

            SearchableDropdownField(
                label = "Course Name",
                placeholder = "Search course",
                value = courseName,
                options = courseNameList,
                onValueChange = { courseName = it }
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "LOGISTICS",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
                color = Color(0xFF50606F),
                fontWeight = FontWeight.Bold
            )

            SearchableDropdownField(
                label = "Room",
                placeholder = "Search room",
                value = room,
                options = roomList,
                onValueChange = { room = it }
            )

            SearchableDropdownField(
                label = "Time Slot",
                placeholder = "Search time slot",
                value = timeSlot,
                options = timeSlotList,
                onValueChange = { timeSlot = it }
            )

            validationError?.let {
                Text(
                    text = it,
                    color = Color(0xFFBA1A1A),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Surface(color = Color.White, shadowElevation = 10.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBackToTimetableList,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("CANCEL")
                }
                Button(
                    onClick = { submitCourse() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00113A)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("SAVE ENTRY", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SearchableDropdownField(
    label: String,
    placeholder: String,
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var searchText by rememberSaveable(label) { mutableStateOf(value) }
    val filteredOptions = remember(searchText, options) {
        options.filter { option ->
            searchText.isBlank() || option.contains(searchText, ignoreCase = true)
        }
    }

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF334155),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Box {
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    expanded = true
                    onValueChange(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                placeholder = { Text(placeholder) },
                trailingIcon = { Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = null) },
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            DropdownMenu(
                expanded = expanded && filteredOptions.isNotEmpty(),
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth().background(Color.White)
            ) {
                filteredOptions.take(6).forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            searchText = option
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


