package com.example.ims.ui.screens.timetable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.ims.core.MockUserProfile
import com.example.ims.core.Role
import com.example.ims.ui.screens.dashboard.NavigationMenuPanel
import com.example.ims.ui.screens.dashboard.RightMenuPanel
import java.util.UUID

// ─── Data Models ─────────────────────────────────────────────────────────────

data class CourseInstance(
    val id: String,
    val faculty: String,
    val classroom: String,
    val course: String
)

data class CourseAllocation(
    val id: String,
    val templateId: String,
    val faculty: String,
    val classroom: String,
    val course: String,
    val day: String,
    val startBlockIndex: Int,
    val blockCount: Int
)

data class TimetableRecord(
    val id: String,
    val name: String,
    val instances: List<CourseInstance>,
    val allocations: List<CourseAllocation>
)

private data class AllocationKey(
    val allocationId: String
)

private data class DragSource(
    val templateId: String? = null,
    val allocationId: String? = null
)

private data class GridCellKey(
    val day: String,
    val blockIndex: Int
)

// ─── Static Data ─────────────────────────────────────────────────────────────

private val facultyList = listOf(
    "Dr. Aris Thorne",
    "Prof. Sarah Lee",
    "Dr. James Bond",
    "Dr. Maria Garcia",
    "Prof. John Smith"
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

private val classroomList = listOf(
    "A101",
    "A102",
    "B201",
    "B202",
    "C301",
    "C302",
    "Auditorium 1"
)

private val dayList = listOf("Mon", "Tue", "Wed", "Thu", "Fri")

private val teachingBlocks = listOf(
    "08:30 - 10:00",
    "10:00 - 11:30",
    "11:30 - 01:00",
    "02:00 - 03:30",
    "03:30 - 05:00",
    "05:00 - 06:30"
)

// ─── Workload / Subject Limits ───────────────────────────────────────────────

/** Max blocks a single subject can appear across the entire week. */
private const val MAX_SUBJECT_BLOCKS_PER_WEEK = 6

/** Max teaching blocks a faculty member can have across the entire week. */
private const val MAX_FACULTY_BLOCKS_PER_WEEK = 12

// ─── Light-Theme Colours matching the SVG ────────────────────────────────────

private val screenBackground = Color(0xFFF7F9FB)
private val panelBackground = Color(0xFFFFFFFF)
private val cardBackground = Color(0xFFF2F4F6)
private val strokeColor = Color(0xFFE2E5E9)
private val primaryText = Color(0xFF00113A)
private val mutedText = Color(0xFF50606F)
private val accent = Color(0xFF00113A)
private val accentLight = Color(0xFFD1E1F4)
private val warningColor = Color(0xFFE65100)
private val dangerColor = Color(0xFFD32F2F)
private val successGreen = Color(0xFF2E7D32)

// ─── Helpers ─────────────────────────────────────────────────────────────────

private fun instanceCardKey(instanceId: String): String = "inst:$instanceId"
private fun allocationCardKey(key: AllocationKey): String =
    "alloc:${key.allocationId}"

// ─── Root Screen ─────────────────────────────────────────────────────────────

@Composable
fun TimetableScreen(
    modifier: Modifier = Modifier,
    userProfile: MockUserProfile,
    timetables: List<TimetableRecord>,
    primaryTimetableId: String,
    onTimetablesChange: (List<TimetableRecord>) -> Unit,
    onPrimaryTimetableChange: (String) -> Unit,
    onLogout: () -> Unit,
    onOpenStudentSearch: () -> Unit,
    onOpenMyProfile: () -> Unit,
    onOpenDashboard: () -> Unit
) {
    var showCourseCreation by rememberSaveable { mutableStateOf(false) }
    var editingInstance by remember { mutableStateOf<CourseInstance?>(null) }
    var isNavigationMenuOpen by rememberSaveable { mutableStateOf(false) }
    var isRightMenuOpen by rememberSaveable { mutableStateOf(false) }
    var selectedTimetableId by rememberSaveable { mutableStateOf(primaryTimetableId) }

    val visibleTimetableId = if (userProfile.role == Role.STUDENT) {
        primaryTimetableId
    } else {
        selectedTimetableId
    }

    val activeTimetable = timetables.find { it.id == visibleTimetableId } ?: timetables.firstOrNull()

    fun updateActiveTimetable(update: (TimetableRecord) -> TimetableRecord) {
        val current = activeTimetable ?: return
        onTimetablesChange(
            timetables.map { timetable ->
                if (timetable.id == current.id) update(timetable) else timetable
            }
        )
    }

    fun createTimetable() {
        val newTimetable = TimetableRecord(
            id = UUID.randomUUID().toString(),
            name = "Timetable ${timetables.size + 1}",
            instances = emptyList(),
            allocations = emptyList()
        )
        onTimetablesChange(timetables + newTimetable)
        selectedTimetableId = newTimetable.id
    }

    fun deleteActiveTimetable() {
        val current = activeTimetable ?: return
        if (timetables.size <= 1) return

        val updated = timetables.filterNot { it.id == current.id }
        onTimetablesChange(updated)

        val replacement = updated.firstOrNull()
        if (replacement != null) {
            selectedTimetableId = replacement.id
            if (primaryTimetableId == current.id) {
                onPrimaryTimetableChange(replacement.id)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            activeTimetable == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(screenBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No timetable available.", color = primaryText)
                        if (userProfile.role == Role.ACADEMIC_OFFICE) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { createTimetable() }) {
                                Text("Create Timetable")
                            }
                        }
                    }
                }
            }

            showCourseCreation && userProfile.role == Role.ACADEMIC_OFFICE -> {
                CourseCreationScreen(
                    templates = activeTimetable.instances,
                    onBackToTimetableList = { showCourseCreation = false },
                    onSaveCourse = { instance ->
                        updateActiveTimetable { timetable ->
                            timetable.copy(instances = timetable.instances + instance)
                        }
                        showCourseCreation = false
                    }
                )
            }

            editingInstance != null && userProfile.role == Role.ACADEMIC_OFFICE -> {
                CourseEditScreen(
                    instance = editingInstance!!,
                    templates = activeTimetable.instances,
                    onBack = { editingInstance = null },
                    onSave = { updated ->
                        updateActiveTimetable { timetable ->
                            timetable.copy(
                                instances = timetable.instances.map { instance ->
                                    if (instance.id == updated.id) updated else instance
                                }
                            )
                        }
                        editingInstance = null
                    }
                )
            }

            else -> {
                TimetableListScreen(
                    userProfile = userProfile,
                    timetableName = activeTimetable.name,
                    availableTimetables = timetables,
                    selectedTimetableId = activeTimetable.id,
                    primaryTimetableId = primaryTimetableId,
                    onSelectTimetable = { selectedTimetableId = it },
                    onCreateTimetable = { createTimetable() },
                    onRenameCurrentTimetable = { newName ->
                        updateActiveTimetable { timetable -> timetable.copy(name = newName) }
                    },
                    onDeleteCurrentTimetable = { deleteActiveTimetable() },
                    onSetPrimaryTimetable = { onPrimaryTimetableChange(activeTimetable.id) },
                    instances = activeTimetable.instances,
                    allocations = activeTimetable.allocations,
                    onInstancesChange = { updatedInstances ->
                        updateActiveTimetable { timetable ->
                            timetable.copy(instances = updatedInstances)
                        }
                    },
                    onAllocationsChange = { updatedAllocations ->
                        updateActiveTimetable { timetable ->
                            timetable.copy(allocations = updatedAllocations)
                        }
                    },
                    onOpenCourseCreation = { showCourseCreation = true },
                    onEditInstance = { editingInstance = it },
                    onOpenNavigation = { isNavigationMenuOpen = true },
                    onOpenRightMenu = { isRightMenuOpen = true },
                    onOpenProfile = {
                        if (userProfile.role == Role.STUDENT) {
                            onOpenMyProfile()
                        }
                    },
                    showTemplateAndStats = userProfile.role != Role.STUDENT,
                    allowTimetableManagement = userProfile.role == Role.ACADEMIC_OFFICE
                )
            }
        }

        // Overlay scrim for drawers
        if (isNavigationMenuOpen || isRightMenuOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xAA090B11))
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

// ─── Main List Screen ────────────────────────────────────────────────────────

@Composable
private fun TimetableListScreen(
    userProfile: MockUserProfile,
    timetableName: String,
    availableTimetables: List<TimetableRecord>,
    selectedTimetableId: String,
    primaryTimetableId: String,
    onSelectTimetable: (String) -> Unit,
    onCreateTimetable: () -> Unit,
    onRenameCurrentTimetable: (String) -> Unit,
    onDeleteCurrentTimetable: () -> Unit,
    onSetPrimaryTimetable: () -> Unit,
    instances: List<CourseInstance>,
    allocations: List<CourseAllocation>,
    onInstancesChange: (List<CourseInstance>) -> Unit,
    onAllocationsChange: (List<CourseAllocation>) -> Unit,
    onOpenCourseCreation: () -> Unit,
    onEditInstance: (CourseInstance) -> Unit,
    onOpenNavigation: () -> Unit,
    onOpenRightMenu: () -> Unit,
    onOpenProfile: () -> Unit,
    showTemplateAndStats: Boolean,
    allowTimetableManagement: Boolean
) {
    val cellBounds = remember { mutableStateMapOf<GridCellKey, Rect>() }
    val cardBounds = remember { mutableStateMapOf<String, Rect>() }

    var dragSource by remember { mutableStateOf<DragSource?>(null) }
    var dragPointer by remember { mutableStateOf(Offset.Zero) }
    var pendingError by remember { mutableStateOf<String?>(null) }
    var selectedAllocationKey by remember { mutableStateOf<AllocationKey?>(null) }
    var workloadAlerts by remember { mutableStateOf<List<String>>(emptyList()) }
    var showWorkloadDialog by remember { mutableStateOf(false) }
    var timetableMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var showRenameDialog by rememberSaveable { mutableStateOf(false) }
    var renameValue by rememberSaveable { mutableStateOf("") }

    val templates = instances

    // ── Helpers local to this composable ──────────────────────────────────

    fun isSlotRangeValid(startBlockIndex: Int, blockCount: Int): Boolean {
        val endExclusive = startBlockIndex + blockCount
        return startBlockIndex in teachingBlocks.indices && endExclusive <= teachingBlocks.size
    }

    fun hasConflict(
        template: CourseInstance,
        day: String,
        startBlockIndex: Int,
        blockCount: Int,
        ignoreAllocationId: String? = null
    ): String? {
        val endExclusive = startBlockIndex + blockCount
        val sameDayAllocations = allocations.filter { allocation ->
            allocation.day == day && (
                ignoreAllocationId == null || allocation.id != ignoreAllocationId
                )
        }

        sameDayAllocations.forEach { allocation ->
            val otherStart = allocation.startBlockIndex
            val otherEnd = allocation.startBlockIndex + allocation.blockCount
            val overlaps = startBlockIndex < otherEnd && endExclusive > otherStart
            if (!overlaps) return@forEach

            if (allocation.faculty.equals(template.faculty, ignoreCase = true)) {
                return "Faculty conflict: ${template.faculty} already has a class at this time."
            }
            if (allocation.classroom.equals(template.classroom, ignoreCase = true)) {
                return "Room conflict: ${template.classroom} is already occupied at this time."
            }
        }
        return null
    }

    /** Check weekly limits and return a list of warning messages. */
    fun computeWorkloadAlerts(
        currentAllocations: List<CourseAllocation>,
        currentTemplates: List<CourseInstance>
    ): List<String> {
        val alerts = mutableListOf<String>()

        // Subject weekly limit
        val subjectBlockCounts = mutableMapOf<String, Int>()
        currentAllocations.forEach { alloc ->
            subjectBlockCounts[alloc.course] =
                (subjectBlockCounts[alloc.course] ?: 0) + alloc.blockCount
        }
        subjectBlockCounts.forEach { (subject, count) ->
            if (count > MAX_SUBJECT_BLOCKS_PER_WEEK) {
                alerts.add("⚠ $subject has $count blocks/week (limit: $MAX_SUBJECT_BLOCKS_PER_WEEK).")
            }
        }

        // Faculty workload limit
        val facultyBlockCounts = mutableMapOf<String, Int>()
        currentAllocations.forEach { alloc ->
            facultyBlockCounts[alloc.faculty] =
                (facultyBlockCounts[alloc.faculty] ?: 0) + alloc.blockCount
        }
        facultyBlockCounts.forEach { (faculty, count) ->
            if (count > MAX_FACULTY_BLOCKS_PER_WEEK) {
                alerts.add("⚠ $faculty has $count blocks/week (limit: $MAX_FACULTY_BLOCKS_PER_WEEK).")
            }
        }

        return alerts
    }

    fun resolveDropCell(pointer: Offset): GridCellKey? {
        return cellBounds.entries.firstOrNull { (_, rect) -> rect.contains(pointer) }?.key
    }

    fun dropDraggedSourceOnCell(dropCell: GridCellKey?) {
        val source = dragSource ?: return
        if (dropCell == null) {
            pendingError = "Drop the instance into a valid timetable block."
            return
        }

        val blockCount = 1
        if (!isSlotRangeValid(dropCell.blockIndex, blockCount)) {
            pendingError = "Invalid placement: exceeds day end."
            return
        }

        val newAllocations = when {
            source.templateId != null -> {
                val template = templates.find { it.id == source.templateId }
                if (template == null) {
                    pendingError = "Selected template could not be found."
                    return
                }

                val conflict = hasConflict(
                    template = template,
                    day = dropCell.day,
                    startBlockIndex = dropCell.blockIndex,
                    blockCount = blockCount
                )
                if (conflict != null) {
                    pendingError = conflict
                    return
                }

                allocations + CourseAllocation(
                    id = UUID.randomUUID().toString(),
                    templateId = template.id,
                    faculty = template.faculty,
                    classroom = template.classroom,
                    course = template.course,
                    day = dropCell.day,
                    startBlockIndex = dropCell.blockIndex,
                    blockCount = blockCount
                )
            }

            source.allocationId != null -> {
                val allocationToMove = allocations.find { it.id == source.allocationId }
                if (allocationToMove == null) {
                    pendingError = "Selected instance could not be found."
                    return
                }

                val conflict = hasConflict(
                    template = CourseInstance(
                        id = allocationToMove.templateId,
                        faculty = allocationToMove.faculty,
                        classroom = allocationToMove.classroom,
                        course = allocationToMove.course
                    ),
                    day = dropCell.day,
                    startBlockIndex = dropCell.blockIndex,
                    blockCount = blockCount,
                    ignoreAllocationId = allocationToMove.id
                )
                if (conflict != null) {
                    pendingError = conflict
                    return
                }

                allocations.map { allocation ->
                    if (allocation.id == allocationToMove.id) {
                        allocation.copy(day = dropCell.day, startBlockIndex = dropCell.blockIndex)
                    } else {
                        allocation
                    }
                }
            }

            else -> {
                pendingError = "No drag source selected."
                return
            }
        }

        onAllocationsChange(newAllocations)

        // Recalculate workload alerts after every drop
        workloadAlerts = computeWorkloadAlerts(newAllocations, instances)
        if (workloadAlerts.isNotEmpty()) {
            showWorkloadDialog = true
        }
    }

    // ── UI ─────────────────────────────────────────────────────────────────

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (userProfile.role == Role.ACADEMIC_OFFICE) 136.dp else 16.dp)
        ) {
            TimetableTopBar(
                title = "IMS Time Table",
                displayName = userProfile.displayName,
                onOpenNavigation = onOpenNavigation,
                onOpenProfile = onOpenProfile,
                onOpenRightMenu = onOpenRightMenu
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                if (allowTimetableManagement || userProfile.role == Role.STUDENT) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = panelBackground),
                        border = BorderStroke(1.dp, strokeColor)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = if (userProfile.role == Role.STUDENT) "PRIMARY TIMETABLE" else "TIMETABLE",
                                style = MaterialTheme.typography.labelSmall,
                                color = mutedText,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = allowTimetableManagement) {
                                        if (allowTimetableManagement) timetableMenuExpanded = true
                                    },
                                shape = RoundedCornerShape(8.dp),
                                color = cardBackground,
                                border = BorderStroke(1.dp, strokeColor)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = timetableName,
                                        color = primaryText,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    if (allowTimetableManagement) {
                                        Icon(
                                            imageVector = Icons.Outlined.KeyboardArrowDown,
                                            contentDescription = null,
                                            tint = mutedText
                                        )
                                    }
                                }
                            }

                            DropdownMenu(
                                expanded = timetableMenuExpanded,
                                onDismissRequest = { timetableMenuExpanded = false }
                            ) {
                                availableTimetables.forEach { timetable ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                if (timetable.id == primaryTimetableId) {
                                                    "${timetable.name} (Primary)"
                                                } else {
                                                    timetable.name
                                                }
                                            )
                                        },
                                        onClick = {
                                            onSelectTimetable(timetable.id)
                                            timetableMenuExpanded = false
                                        }
                                    )
                                }
                            }

                            if (allowTimetableManagement) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = onCreateTimetable,
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(44.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryText)
                                        ) {
                                            Text(
                                                "Create",
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.Center,
                                                maxLines = 1,
                                                softWrap = false,
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                renameValue = timetableName
                                                showRenameDialog = true
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(44.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryText)
                                        ) {
                                            Text(
                                                "Rename",
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.Center,
                                                maxLines = 1,
                                                softWrap = false,
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = onSetPrimaryTimetable,
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(44.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryText)
                                        ) {
                                            Text(
                                                "Set Primary",
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.Center,
                                                maxLines = 1,
                                                softWrap = false,
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }
                                        OutlinedButton(
                                            onClick = onDeleteCurrentTimetable,
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(44.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                            enabled = availableTimetables.size > 1,
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = dangerColor,
                                                disabledContentColor = mutedText
                                            )
                                        ) {
                                            Text(
                                                "Delete",
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.Center,
                                                maxLines = 1,
                                                softWrap = false,
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                // ── Schedule Header ──
                Text(
                    text = "BLOCK SCHEDULE",
                    style = MaterialTheme.typography.labelSmall,
                    color = mutedText,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "8:30 AM to 6:30 PM (Break: 1:00 - 2:00 PM)",
                    style = MaterialTheme.typography.titleMedium,
                    color = primaryText,
                    fontWeight = FontWeight.Bold
                )

                // ── Workload Summary Badge ──
                val currentAlerts = computeWorkloadAlerts(allocations, instances)
                if (currentAlerts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showWorkloadDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFF3E0),
                        border = BorderStroke(1.dp, Color(0xFFFFCC80))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Warning,
                                contentDescription = null,
                                tint = warningColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "${currentAlerts.size} workload alert(s) – tap for details",
                                color = warningColor,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                TimetableGrid(
                    instances = instances,
                    allocations = allocations,
                    cellBounds = cellBounds,
                    cardBounds = cardBounds,
                    draggingTemplateId = when {
                        dragSource?.templateId != null -> dragSource?.templateId
                        dragSource?.allocationId != null -> {
                            val draggedAllocation = allocations.find { it.id == dragSource?.allocationId }
                            draggedAllocation?.templateId
                        }
                        else -> null
                    },
                    dragPointer = dragPointer,
                    onAllocationTap = { allocationKey -> selectedAllocationKey = allocationKey },
                    onAllocationDragStart = { allocationKey, startPointerInCard ->
                        if (userProfile.role != Role.ACADEMIC_OFFICE) return@TimetableGrid
                        val cardKey = allocationCardKey(allocationKey)
                        val bounds = cardBounds[cardKey] ?: return@TimetableGrid
                        dragSource = DragSource(
                            allocationId = allocationKey.allocationId
                        )
                        dragPointer = bounds.topLeft + startPointerInCard
                    },
                    onAllocationDragMove = { dragAmount ->
                        dragPointer += dragAmount
                    },
                    onAllocationDragEnd = {
                        dropDraggedSourceOnCell(resolveDropCell(dragPointer))
                        dragSource = null
                    },
                    onAllocationDragCancel = {
                        dragSource = null
                    }
                )

                if (showTemplateAndStats) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "CLASS TEMPLATES",
                        style = MaterialTheme.typography.labelSmall,
                        color = mutedText,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    if (templates.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = panelBackground),
                            border = BorderStroke(1.dp, strokeColor)
                        ) {
                            Text(
                                text = "No class templates created yet. Tap \" + \" to add one.",
                                modifier = Modifier.padding(14.dp),
                                color = mutedText,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            templates.forEach { instance ->
                                val canDrag = userProfile.role == Role.ACADEMIC_OFFICE
                                UnallocatedInstanceCard(
                                    instance = instance,
                                    cardBounds = cardBounds,
                                    canDrag = canDrag,
                                    onDragStart = { startPointerInCard ->
                                        val bounds =
                                            cardBounds[instanceCardKey(instance.id)]
                                                ?: return@UnallocatedInstanceCard
                                        dragSource =
                                            DragSource(templateId = instance.id)
                                        dragPointer = bounds.topLeft + startPointerInCard
                                    },
                                    onDragMove = { dragAmount ->
                                        dragPointer += dragAmount
                                    },
                                    onDragEnd = {
                                        dropDraggedSourceOnCell(resolveDropCell(dragPointer))
                                        dragSource = null
                                    },
                                    onDragCancel = {
                                        dragSource = null
                                    },
                                    onDelete = if (userProfile.role == Role.ACADEMIC_OFFICE) {
                                        {
                                            onInstancesChange(instances.filterNot { it.id == instance.id })
                                        }
                                    } else null,
                                    onEdit = if (userProfile.role == Role.ACADEMIC_OFFICE) {
                                        { onEditInstance(instance) }
                                    } else null
                                )
                            }
                        }
                    }

                    // ── Quick Stats ──
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "QUICK STATS",
                        style = MaterialTheme.typography.labelSmall,
                        color = mutedText,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = panelBackground),
                        border = BorderStroke(1.dp, strokeColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val totalBlocks = allocations.sumOf { it.blockCount }
                            val uniqueFaculty = allocations.map { it.faculty }.toSet()
                            val uniqueRooms = allocations.map { it.classroom }.toSet()
                            StatRow("Total templates created", instances.size.toString())
                            StatRow("Total blocks allocated", totalBlocks.toString())
                            StatRow("Faculty teaching", uniqueFaculty.size.toString())
                            StatRow("Rooms in use", uniqueRooms.size.toString())
                        }
                    }
                }
            }
        }

        // ── Drag Ghost ──
        if (dragSource != null) {
            val instance = when {
                dragSource?.templateId != null -> instances.find { it.id == dragSource?.templateId }
                dragSource?.allocationId != null -> {
                    val allocation = allocations.find { it.id == dragSource?.allocationId }
                    allocation?.let {
                        CourseInstance(
                            id = it.templateId,
                            faculty = it.faculty,
                            classroom = it.classroom,
                            course = it.course
                        )
                    }
                }
                else -> null
            }
            if (instance != null) {
                DragGhostCard(
                    instance = instance,
                    dragPointer = dragPointer,
                    modifier = Modifier.zIndex(2f)
                )
            }
        }

        // ── Bottom Action FAB ──
        if (userProfile.role == Role.ACADEMIC_OFFICE && showTemplateAndStats) {
            TimetableBottomActions(
                modifier = Modifier.align(Alignment.BottomCenter),
                onOpenCourseCreation = onOpenCourseCreation
            )
        }
    }

    // ── Dialogs ──

    if (pendingError != null) {
        AlertDialog(
            onDismissRequest = { pendingError = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Outlined.Warning, contentDescription = null, tint = dangerColor)
                    Text("Allocation Error", fontWeight = FontWeight.Bold)
                }
            },
            text = { Text(pendingError.orEmpty()) },
            confirmButton = {
                Button(
                    onClick = { pendingError = null },
                    colors = ButtonDefaults.buttonColors(containerColor = accent)
                ) { Text("OK") }
            }
        )
    }

    if (selectedAllocationKey != null) {
        val key = selectedAllocationKey!!
        val selectedAllocation = allocations.find { it.id == key.allocationId }
        val selectedInstance = selectedAllocation?.let { alloc ->
            CourseInstance(
                id = alloc.templateId,
                faculty = alloc.faculty,
                classroom = alloc.classroom,
                course = alloc.course
            )
        }
        AlertDialog(
            onDismissRequest = { selectedAllocationKey = null },
            title = { Text("Allocated Slot", fontWeight = FontWeight.Bold, color = primaryText) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (selectedInstance != null) {
                        Text("${selectedInstance.course}", fontWeight = FontWeight.SemiBold)
                        Text("Room: ${selectedInstance.classroom}")
                        Text("Faculty: ${selectedInstance.faculty}")
                        Text(
                            "Day: ${selectedAllocation?.day ?: ""}  |  Block: ${teachingBlocks.getOrNull(selectedAllocation?.startBlockIndex ?: -1) ?: ""}"
                        )
                    } else {
                        Text("Tap unallocate to remove this scheduled instance.")
                    }
                }
            },
            confirmButton = {
                if (userProfile.role == Role.ACADEMIC_OFFICE) {
                    Button(
                        onClick = {
                            val newAllocations = allocations.filterNot { it.id == key.allocationId }
                            onAllocationsChange(newAllocations)
                            selectedAllocationKey = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = dangerColor)
                    ) {
                        Text("Unallocate")
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { selectedAllocationKey = null }
                ) {
                    Text("Close")
                }
            }
        )
    }

    if (showWorkloadDialog) {
        AlertDialog(
            onDismissRequest = { showWorkloadDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Outlined.Warning, contentDescription = null, tint = warningColor)
                    Text("Workload Alerts", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    workloadAlerts.forEach { alert ->
                        Text(alert, color = warningColor, style = MaterialTheme.typography.bodyMedium)
                    }
                    if (workloadAlerts.isEmpty()) {
                        Text("All within limits.", color = successGreen)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showWorkloadDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = accent)
                ) { Text("Dismiss") }
            }
        )
    }

    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename Timetable", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = renameValue,
                    onValueChange = { renameValue = it },
                    label = { Text("Timetable Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = renameValue.trim()
                        if (trimmed.isNotBlank()) {
                            onRenameCurrentTimetable(trimmed)
                        }
                        showRenameDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accent)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showRenameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ─── Stat Row helper ─────────────────────────────────────────────────────────

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = mutedText, style = MaterialTheme.typography.bodySmall)
        Text(value, color = primaryText, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
    }
}

// ─── Top Bar ─────────────────────────────────────────────────────────────────

@Composable
private fun TimetableTopBar(
    title: String,
    displayName: String,
    onOpenNavigation: () -> Unit,
    onOpenProfile: () -> Unit,
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
                        .background(Color(0xFF27457A), RoundedCornerShape(4.dp))
                        .clickable { onOpenProfile() },
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

// ─── Timetable Grid ──────────────────────────────────────────────────────────

@Composable
private fun TimetableGrid(
    instances: List<CourseInstance>,
    allocations: List<CourseAllocation>,
    cellBounds: MutableMap<GridCellKey, Rect>,
    cardBounds: MutableMap<String, Rect>,
    draggingTemplateId: String?,
    dragPointer: Offset,
    onAllocationTap: (AllocationKey) -> Unit,
    onAllocationDragStart: (AllocationKey, Offset) -> Unit,
    onAllocationDragMove: (Offset) -> Unit,
    onAllocationDragEnd: () -> Unit,
    onAllocationDragCancel: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, strokeColor),
            color = panelBackground,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(cardBackground)
                        .padding(vertical = 10.dp)
                ) {
                    Box(
                        modifier = Modifier.requiredWidth(42.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            tint = mutedText,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    dayList.forEach { day -> DayHeaderCell(day) }
                }

                HorizontalDivider(color = strokeColor)

                teachingBlocks.forEachIndexed { index, blockLabel ->
                    if (index == 3) {
                        BreakRow(label = "BREAK 01:00 - 02:00 PM")
                        HorizontalDivider(color = strokeColor)
                    }
                    TimeBlockRow(
                        blockIndex = index,
                        blockLabel = blockLabel,
                        instances = instances,
                        allocations = allocations,
                        cellBounds = cellBounds,
                        cardBounds = cardBounds,
                        draggingTemplateId = draggingTemplateId,
                        dragPointer = dragPointer,
                        onAllocationTap = onAllocationTap,
                        onAllocationDragStart = onAllocationDragStart,
                        onAllocationDragMove = onAllocationDragMove,
                        onAllocationDragEnd = onAllocationDragEnd,
                        onAllocationDragCancel = onAllocationDragCancel
                    )
                    if (index < teachingBlocks.lastIndex) HorizontalDivider(color = strokeColor)
                }
            }
        }
    }
}

@Composable
private fun RowScope.DayHeaderCell(day: String) {
    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
        Text(
            text = day,
            style = MaterialTheme.typography.labelSmall,
            color = mutedText,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
        )
    }
}

@Composable
private fun BreakRow(label: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(44.dp)
            .background(Color(0xFFFFF8E1)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFFE65100),
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun TimeBlockRow(
    blockIndex: Int,
    blockLabel: String,
    instances: List<CourseInstance>,
    allocations: List<CourseAllocation>,
    cellBounds: MutableMap<GridCellKey, Rect>,
    cardBounds: MutableMap<String, Rect>,
    draggingTemplateId: String?,
    dragPointer: Offset,
    onAllocationTap: (AllocationKey) -> Unit,
    onAllocationDragStart: (AllocationKey, Offset) -> Unit,
    onAllocationDragMove: (Offset) -> Unit,
    onAllocationDragEnd: () -> Unit,
    onAllocationDragCancel: () -> Unit
) {
    val currentOnAllocationDragStart by rememberUpdatedState(onAllocationDragStart)
    val currentOnAllocationDragMove by rememberUpdatedState(onAllocationDragMove)
    val currentOnAllocationDragEnd by rememberUpdatedState(onAllocationDragEnd)
    val currentOnAllocationDragCancel by rememberUpdatedState(onAllocationDragCancel)

    Row(modifier = Modifier.fillMaxWidth().requiredHeight(100.dp)) {
        Box(
            modifier = Modifier
                .requiredWidth(42.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            val parts = blockLabel.split(" - ")
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = parts.getOrNull(0) ?: "", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = mutedText)
                Text(text = "-", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = mutedText)
                Text(text = parts.getOrNull(1) ?: "", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = mutedText)
            }
        }

        dayList.forEach { day ->
            val key = GridCellKey(day = day, blockIndex = blockIndex)
            val blockAllocations = allocations.filter {
                it.day == day && blockIndex in it.startBlockIndex until (it.startBlockIndex + it.blockCount)
            }
            val isDropHover =
                draggingTemplateId != null && (cellBounds[key]?.contains(dragPointer) == true)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(1.dp)
                    .background(
                        color = when {
                            isDropHover -> Color(0xFFBBDEFB)
                            blockAllocations.isNotEmpty() -> Color(0xFFE3F2FD)
                            else -> Color(0xFFF5F7FA)
                        },
                        shape = RoundedCornerShape(4.dp)
                    )
                    .onGloballyPositioned { coordinates ->
                        cellBounds[key] = coordinates.boundsInRoot()
                    }
                    .padding(horizontal = 2.dp, vertical = 2.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    blockAllocations.forEach { allocation ->
                        val allocationKey = AllocationKey(
                            allocationId = allocation.id
                        )
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned {
                                    cardBounds[allocationCardKey(allocationKey)] =
                                        it.boundsInRoot()
                                }
                                .pointerInput(
                                    allocationKey.allocationId
                                ) {
                                    detectDragGestures(
                                        onDragStart = { offset ->
                                            currentOnAllocationDragStart(allocationKey, offset)
                                        },
                                        onDragEnd = { currentOnAllocationDragEnd() },
                                        onDragCancel = { currentOnAllocationDragCancel() },
                                        onDrag = { _, dragAmount ->
                                            currentOnAllocationDragMove(dragAmount)
                                        }
                                    )
                                }
                                .clickable { onAllocationTap(allocationKey) },
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF00113A),
                            border = BorderStroke(1.dp, Color(0xFF1A3A6E))
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 3.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = allocation.course,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    color = Color.White,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = allocation.classroom,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 8.sp,
                                    color = Color(0xFFBBDEFB),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = allocation.faculty,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 8.sp,
                                    color = Color(0xFF90CAF9),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Unallocated Instance Card ───────────────────────────────────────────────

@Composable
private fun UnallocatedInstanceCard(
    instance: CourseInstance,
    cardBounds: MutableMap<String, Rect>,
    canDrag: Boolean,
    onDragStart: (Offset) -> Unit,
    onDragMove: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    onDelete: (() -> Unit)?,
    onEdit: (() -> Unit)?
) {
    val currentOnDragStart by rememberUpdatedState(onDragStart)
    val currentOnDragMove by rememberUpdatedState(onDragMove)
    val currentOnDragEnd by rememberUpdatedState(onDragEnd)
    val currentOnDragCancel by rememberUpdatedState(onDragCancel)

    Card(
        modifier = Modifier
            .width(230.dp)
            .onGloballyPositioned {
                cardBounds[instanceCardKey(instance.id)] = it.boundsInRoot()
            }
            .then(
                if (canDrag) {
                    Modifier.pointerInput(instance.id) {
                        detectDragGestures(
                            onDragStart = { currentOnDragStart(it) },
                            onDragEnd = { currentOnDragEnd() },
                            onDragCancel = { currentOnDragCancel() },
                            onDrag = { _, dragAmount -> currentOnDragMove(dragAmount) }
                        )
                    }
                } else Modifier
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = panelBackground),
        border = BorderStroke(1.dp, strokeColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    instance.course,
                    color = primaryText,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 2
                )
                Row {
                    if (onEdit != null) {
                        IconButton(onClick = onEdit, modifier = Modifier.size(20.dp)) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Edit",
                                tint = accent,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(Modifier.width(4.dp))
                    }
                    if (onDelete != null) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(20.dp)) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete instance",
                                tint = dangerColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
            Text(instance.faculty, color = mutedText, style = MaterialTheme.typography.bodySmall)
            Text(
                "Room: ${instance.classroom}",
                color = mutedText,
                style = MaterialTheme.typography.bodySmall
            )
            if (canDrag) {
                Text(
                    "Drag to allocate",
                    color = accent,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ─── Drag Ghost ──────────────────────────────────────────────────────────────

@Composable
private fun DragGhostCard(
    instance: CourseInstance,
    dragPointer: Offset,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(
                start = maxOf(0f, dragPointer.x - 110f).dp,
                top = maxOf(0f, dragPointer.y - 24f).dp
            )
            .width(220.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF00113A).copy(alpha = 0.9f)),
        border = BorderStroke(1.dp, Color(0xFF4D73BF))
    ) {
        Text(
            text = "${instance.course} • ${instance.classroom}",
            modifier = Modifier.padding(10.dp),
            color = Color.White,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ─── Bottom Actions ──────────────────────────────────────────────────────────

@Composable
private fun TimetableBottomActions(
    modifier: Modifier = Modifier,
    onOpenCourseCreation: () -> Unit
) {
    Surface(modifier = modifier.fillMaxWidth(), color = panelBackground, shadowElevation = 8.dp) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onOpenCourseCreation,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accent,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("ADD TIMETABLE ENTRY", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─── Course Creation Screen ──────────────────────────────────────────────────

@Composable
private fun CourseCreationScreen(
    templates: List<CourseInstance>,
    onBackToTimetableList: () -> Unit,
    onSaveCourse: (CourseInstance) -> Unit
) {
    var facultyName by rememberSaveable { mutableStateOf("") }
    var courseName by rememberSaveable { mutableStateOf("") }
    var classroom by rememberSaveable { mutableStateOf("") }
    var errorPopupMessage by rememberSaveable { mutableStateOf<String?>(null) }

    fun submitCourse() {
        val trimmedFaculty = facultyName.trim()
        val trimmedCourse = courseName.trim()
        val trimmedClassroom = classroom.trim()
        if (trimmedFaculty.isBlank() || trimmedCourse.isBlank() || trimmedClassroom.isBlank()) {
            errorPopupMessage = "Faculty, Classroom, and Course are required."
            return
        }
        val exists = templates.any {
            it.faculty.equals(trimmedFaculty, ignoreCase = true) &&
            it.course.equals(trimmedCourse, ignoreCase = true) &&
            it.classroom.equals(trimmedClassroom, ignoreCase = true)
        }
        if (exists) {
            errorPopupMessage = "This exact class template already exists."
            return
        }
        onSaveCourse(
            CourseInstance(
                id = UUID.randomUUID().toString(),
                faculty = trimmedFaculty,
                classroom = trimmedClassroom,
                course = trimmedCourse
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(screenBackground)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
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
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Add New Course",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
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
                    "Register a new course, then allocate it on the timetable using drag-and-drop.",
                    color = mutedText,
                    style = MaterialTheme.typography.bodySmall
                )
                SelectionDropdownField(
                    label = "Faculty",
                    placeholder = "Select faculty",
                    value = facultyName,
                    options = facultyList,
                    onValueChange = { facultyName = it }
                )
                SelectionDropdownField(
                    label = "Course",
                    placeholder = "Select course",
                    value = courseName,
                    options = courseNameList,
                    onValueChange = { courseName = it }
                )
                SelectionDropdownField(
                    label = "Classroom",
                    placeholder = "Select classroom",
                    value = classroom,
                    options = classroomList,
                    onValueChange = { classroom = it }
                )
            }

            Surface(color = panelBackground, shadowElevation = 10.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBackToTimetableList,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, strokeColor),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryText)
                    ) { Text("CANCEL") }
                    Button(
                        onClick = { submitCourse() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accent,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("SAVE TIMETABLE", fontWeight = FontWeight.Bold) }
                }
            }
        }

        if (errorPopupMessage != null) {
            AlertDialog(
                onDismissRequest = { errorPopupMessage = null },
                title = { Text("Cannot Save") },
                text = { Text(errorPopupMessage.orEmpty()) },
                confirmButton = {
                    Button(
                        onClick = { errorPopupMessage = null },
                        colors = ButtonDefaults.buttonColors(containerColor = accent)
                    ) { Text("OK") }
                }
            )
        }
    }
}

// ─── Course Edit Screen ──────────────────────────────────────────────────────

@Composable
private fun CourseEditScreen(
    instance: CourseInstance,
    templates: List<CourseInstance>,
    onBack: () -> Unit,
    onSave: (CourseInstance) -> Unit
) {
    var facultyName by rememberSaveable { mutableStateOf(instance.faculty) }
    var courseName by rememberSaveable { mutableStateOf(instance.course) }
    var classroom by rememberSaveable { mutableStateOf(instance.classroom) }
    var errorPopupMessage by rememberSaveable { mutableStateOf<String?>(null) }

    fun submitEdit() {
        val trimmedFaculty = facultyName.trim()
        val trimmedCourse = courseName.trim()
        val trimmedClassroom = classroom.trim()
        if (trimmedFaculty.isBlank() || trimmedCourse.isBlank() || trimmedClassroom.isBlank()) {
            errorPopupMessage = "Faculty, Classroom, and Course are required."
            return
        }
        val exists = templates.any {
            it.id != instance.id &&
            it.faculty.equals(trimmedFaculty, ignoreCase = true) &&
            it.course.equals(trimmedCourse, ignoreCase = true) &&
            it.classroom.equals(trimmedClassroom, ignoreCase = true)
        }
        if (exists) {
            errorPopupMessage = "Another class template with these details already exists."
            return
        }
        onSave(
            instance.copy(
                faculty = trimmedFaculty,
                classroom = trimmedClassroom,
                course = trimmedCourse
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(screenBackground)) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                    Text(
                        text = "Edit Course",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Cancel",
                        color = Color.White,
                        modifier = Modifier.clickable { onBack() },
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
                    "Update the details for this course instance.",
                    color = mutedText,
                    style = MaterialTheme.typography.bodySmall
                )
                SelectionDropdownField(
                    label = "Faculty",
                    placeholder = "Select faculty",
                    value = facultyName,
                    options = facultyList,
                    onValueChange = { facultyName = it }
                )
                SelectionDropdownField(
                    label = "Course",
                    placeholder = "Select course",
                    value = courseName,
                    options = courseNameList,
                    onValueChange = { courseName = it }
                )
                SelectionDropdownField(
                    label = "Classroom",
                    placeholder = "Select classroom",
                    value = classroom,
                    options = classroomList,
                    onValueChange = { classroom = it }
                )
            }

            Surface(color = panelBackground, shadowElevation = 10.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, strokeColor),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryText)
                    ) { Text("CANCEL") }
                    Button(
                        onClick = { submitEdit() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = accent),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("SAVE CHANGES", fontWeight = FontWeight.Bold) }
                }
            }
        }

        if (errorPopupMessage != null) {
            AlertDialog(
                onDismissRequest = { errorPopupMessage = null },
                title = { Text("Cannot Save") },
                text = { Text(errorPopupMessage.orEmpty()) },
                confirmButton = {
                    Button(
                        onClick = { errorPopupMessage = null },
                        colors = ButtonDefaults.buttonColors(containerColor = accent)
                    ) { Text("OK") }
                }
            )
        }
    }
}

// ─── Dropdown Field (matching SVG design) ────────────────────────────────────

@Composable
private fun SelectionDropdownField(
    label: String,
    placeholder: String,
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit
) {
    var expanded by rememberSaveable(label) { mutableStateOf(false) }
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = primaryText,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                shape = RoundedCornerShape(8.dp),
                color = cardBackground,
                border = BorderStroke(1.dp, strokeColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (value.isBlank()) placeholder else value,
                        color = if (value.isBlank()) mutedText else primaryText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                        tint = mutedText
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(),
                containerColor = panelBackground
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, color = primaryText) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

private fun maxOf(a: Float, b: Float): Float = if (a > b) a else b
