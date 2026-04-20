package com.example.ims.ui.screens.timetable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class ScheduleCell(
    val code: String,
    val title: String,
    val room: String,
    val bgColor: Color,
    val textColor: Color
)

@Composable
fun TimetableScreen(modifier: Modifier = Modifier) {
    var showCourseCreation by rememberSaveable { mutableStateOf(false) }

    if (showCourseCreation) {
        CourseCreationScreen(
            modifier = modifier,
            onBackToTimetableList = { showCourseCreation = false }
        )
    } else {
        TimetableListScreen(
            modifier = modifier,
            onOpenCourseCreation = { showCourseCreation = true }
        )
    }
}

@Composable
private fun TimetableListScreen(
    modifier: Modifier = Modifier,
    onOpenCourseCreation: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 152.dp)
        ) {
            TimetableTopBar(
                title = "IMS Time Table",
                actionLabel = "Time Table List",
                onActionClick = {}
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "WEEKLY ORCHESTRATION",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Time Table Management",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF00113A),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))
                TimetableGrid()
            }
        }

        TimetableBottomActions(
            modifier = Modifier.align(Alignment.BottomCenter),
            onOpenCourseCreation = onOpenCourseCreation
        )
    }
}

@Composable
private fun TimetableGrid() {
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
                    modifier = Modifier.requiredWidth(70.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                }
                DayHeaderCell("Mon", "12")
                DayHeaderCell("Tue", "13")
                DayHeaderCell("Wed", "14")
                DayHeaderCell("Thu", "15")
                DayHeaderCell("Fri", "16")
            }

            HorizontalDivider(color = Color(0xFFE7EBF0))

            TimeSlotRow(
                timeLabel = "09:00",
                cells = listOf(
                    ScheduleCell("ARCH101", "Design Theory", "Hall A", Color(0xFFDBE1FF), Color(0xFF00113A)),
                    null,
                    ScheduleCell("HIST204", "Urban Evolution", "R 302", Color(0xFFD4E4F6), Color(0xFF324255)),
                    null,
                    ScheduleCell("LAB10", "CAD Workshop", "Lab 4", Color(0xFFCFF0DE), Color(0xFF084D31))
                )
            )

            HorizontalDivider(color = Color(0xFFE7EBF0))

            TimeSlotRow(
                timeLabel = "11:00",
                cells = listOf(
                    null,
                    ScheduleCell("SEM402", "Thesis Seminar", "Annex", Color(0xFF00113A), Color.White),
                    null,
                    ScheduleCell("HIST204", "Urban Evolution", "Hall C", Color(0xFFD4E4F6), Color(0xFF324255)),
                    null
                )
            )
        }
    }
}

@Composable
private fun DayHeaderCell(day: String, date: String) {
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF64748B),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = date,
            style = MaterialTheme.typography.titleSmall,
            color = Color(0xFF00113A),
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
            .requiredHeight(110.dp)
    ) {
        Box(
            modifier = Modifier
                .requiredWidth(70.dp)
                .fillMaxSize(),
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
                    .padding(6.dp)
            ) {
                if (cell != null) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = cell.bgColor),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            Text(
                                text = cell.code,
                                style = MaterialTheme.typography.labelSmall,
                                color = cell.textColor,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = cell.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = cell.textColor,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = cell.room,
                                style = MaterialTheme.typography.labelSmall,
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
private fun TimetableBottomActions(
    modifier: Modifier = Modifier,
    onOpenCourseCreation: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE6EAF0))
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onOpenCourseCreation,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF00113A)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF00113A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Outlined.CalendarMonth, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ADD COURSE", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF00113A)),
                    border = BorderStroke(1.dp, Color(0xFF00113A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(0.55f)
                ) {
                    Icon(Icons.Outlined.Save, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("SAVE", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(0.6f)
                ) {
                    Icon(Icons.Outlined.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("DELETE", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            PlaceholderFooterButton()
            Spacer(modifier = Modifier.height(8.dp))
            PlaceholderFooterButton()
        }
    }
}

@Composable
private fun PlaceholderFooterButton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(42.dp)
            .border(1.dp, Color(0xFFE2E6EC), RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "BUTTON FULL-W PRIMARY",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF94A3B8),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CourseCreationScreen(
    modifier: Modifier = Modifier,
    onBackToTimetableList: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            TimetableTopBar(
                title = "IMS Time Table",
                actionLabel = null,
                onActionClick = onBackToTimetableList
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 14.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F6F6))
            ) {
                Column(modifier = Modifier.padding(horizontal = 26.dp, vertical = 26.dp)) {
                    Text(
                        text = "CURRICULUM MANAGEMENT",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.6.sp),
                        color = Color(0xFF62728A),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Add New Course",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF00113A),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Register a new academic module into the institutional database.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4F5F74)
                    )

                    Spacer(modifier = Modifier.height(18.dp))
                    CourseField("COURSE NAME", "e.g. Advanced Macroeconomics")
                    CourseField("COURSE CODE", "ECO-402")
                    CourseField("ROOM NUMBER", "Hall B-12")
                    DropdownField("COURSE DURATION", "Select Duration")
                    FacultySearchField()

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = onBackToTimetableList,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF002366), contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredHeight(50.dp)
                    ) {
                        Text("Submit Course", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Course details will be synchronized across the institute's timetable immediately after submission.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun CourseField(label: String, placeholder: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.2.sp),
        color = Color(0xFF00113A),
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(6.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(56.dp)
            .background(Color(0xFFE1E4E8), RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = placeholder, color = Color(0xFF6B7280), style = MaterialTheme.typography.bodyLarge)
    }
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
private fun DropdownField(label: String, value: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.2.sp),
        color = Color(0xFF00113A),
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(6.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(56.dp)
            .background(Color(0xFFE1E4E8), RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(value, color = Color(0xFF1F2937), style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.weight(1f))
        Icon(imageVector = Icons.Outlined.ArrowDropDown, contentDescription = null, tint = Color(0xFF64748B))
    }
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
private fun FacultySearchField() {
    Text(
        text = "ASSIGNED FACULTY",
        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.2.sp),
        color = Color(0xFF00113A),
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(6.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(56.dp)
            .background(Color(0xFFE3EBEB), RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Outlined.Search, contentDescription = null, tint = Color(0xFF2A4386))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Search faculty member...",
            color = Color(0xFF6B7280),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun TimetableTopBar(
    title: String,
    actionLabel: String?,
    onActionClick: () -> Unit
) {
    Surface(
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE6EAF0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Outlined.Menu,
                        contentDescription = null,
                        tint = Color(0xFF00113A)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF00113A),
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (actionLabel != null) {
                Text(
                    text = actionLabel.uppercase(),
                    modifier = Modifier
                        .background(Color(0xFF00113A), RoundedCornerShape(6.dp))
                        .clickable(onClick = onActionClick)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }
        }
    }
}
