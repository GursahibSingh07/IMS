package com.example.ims.ui.screens.studentsearch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Student(
    val id: String,
    val name: String,
    val batch: String,
    val status: String, // Existing or Former
    val admissionYear: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentSearchScreen(modifier: Modifier = Modifier) {
    var query by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("All") }
    
    val allStudents = remember {
        listOf(
            Student("STU001", "Alice Johnson", "Batch A", "Existing", "2023"),
            Student("STU002", "Bob Smith", "Batch B", "Existing", "2022"),
            Student("STU003", "Charlie Brown", "Batch A", "Former", "2020"),
            Student("STU004", "Diana Prince", "Batch C", "Existing", "2023"),
            Student("STU005", "Ethan Hunt", "Batch B", "Former", "2019")
        )
    }

    val filteredStudents = allStudents.filter {
        (it.name.contains(query, ignoreCase = true) || it.id.contains(query, ignoreCase = true)) &&
        (selectedStatus == "All" || it.status == selectedStatus)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Student Directory",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )

        // Advanced Search Bar
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search by name or student ID") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = { 
                IconButton(onClick = { /* Open advanced filters */ }) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filters")
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        // Filters Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedStatus == "All",
                onClick = { selectedStatus = "All" },
                label = { Text("All") }
            )
            FilterChip(
                selected = selectedStatus == "Existing",
                onClick = { selectedStatus = "Existing" },
                label = { Text("Existing") }
            )
            FilterChip(
                selected = selectedStatus == "Former",
                onClick = { selectedStatus = "Former" },
                label = { Text("Former") }
            )
        }

        Text(
            text = "Results (${filteredStudents.size})",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredStudents) { student ->
                StudentListItem(student)
            }
        }
    }
}

@Composable
fun StudentListItem(student: Student) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar Placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = student.name.take(1),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "${student.id} • ${student.batch}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                color = if (student.status == "Existing") Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = student.status,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (student.status == "Existing") Color(0xFF2E7D32) else Color(0xFFC62828)
                )
            }
        }
    }
}
