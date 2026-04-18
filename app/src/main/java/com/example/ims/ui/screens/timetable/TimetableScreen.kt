package com.example.ims.ui.screens.timetable

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TimetableEntry(
    val id: Int,
    val subject: String,
    val teacher: String,
    val time: String,
    val day: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(modifier: Modifier = Modifier) {
    var entries by remember {
        mutableStateOf(
            listOf(
                TimetableEntry(1, "Mathematics", "Dr. Smith", "09:00 AM", "Monday"),
                TimetableEntry(2, "Physics", "Prof. Doe", "11:00 AM", "Monday")
            )
        )
    }

    var showDialog by remember { mutableStateOf(false) }
    var newSubject by remember { mutableStateOf("") }
    var newTime by remember { mutableStateOf("") }

    val workloadLimit = 5
    val currentWorkload = entries.size
    val isOverloaded = currentWorkload >= workloadLimit

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Entry")
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Weekly Schedule",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            
            // Real-time alert for workload limits
            if (isOverloaded) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Workload Limit Reached: $currentWorkload/$workloadLimit subjects assigned this week.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            } else {
                Text(
                    "Current Workload: $currentWorkload / $workloadLimit",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(entries) { entry ->
                    TimetableItem(
                        entry = entry,
                        onDelete = { entries = entries.filter { it.id != entry.id } }
                    )
                }
            }
            
            Text(
                "Tip: Long press to drag and reorder (Simulated)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp)
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Timetable Entry") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newSubject,
                        onValueChange = { newSubject = it },
                        label = { Text("Subject Name") }
                    )
                    OutlinedTextField(
                        value = newTime,
                        onValueChange = { newTime = it },
                        label = { Text("Time (e.g. 10:00 AM)") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newSubject.isNotBlank()) {
                        entries = entries + TimetableEntry(
                            entries.size + 1,
                            newSubject,
                            "TBD",
                            newTime,
                            "Monday"
                        )
                        newSubject = ""
                        newTime = ""
                        showDialog = false
                    }
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TimetableItem(entry: TimetableEntry, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(entry.subject, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text("${entry.time} | ${entry.teacher}", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
