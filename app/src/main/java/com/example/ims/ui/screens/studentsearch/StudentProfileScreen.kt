package com.example.ims.ui.screens.studentsearch

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ims.core.MockUserProfile
import com.example.ims.core.StudentRecord

@Composable
fun StudentProfileScreen(
    modifier: Modifier = Modifier,
    profile: MockUserProfile,
    details: StudentRecord,
    canView: Boolean,
    onBack: () -> Unit
) {
    if (!canView) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Access Denied.")
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FB))
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = "Student Profile",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color(0xFFDDE2E8), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile.displayName.take(1),
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color(0xFF00113A),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = profile.displayName,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFF00113A),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "#${details.studentId} • ${details.course}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF475569)
                        )
                        Text(
                            text = profile.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF27457A)
                        )
                    }
                }
            }

            InfoCard(
                title = "Academic Details",
                rows = listOf(
                    "Batch" to details.batch,
                    "Course" to details.course,
                    "Stream" to details.stream,
                    "CGPA" to String.format("%.1f", details.cgpa),
                    "Scholarship" to details.scholarship
                )
            )

            InfoCard(
                title = "Contact Details",
                rows = listOf(
                    "Username" to profile.username,
                    "Phone" to details.phone,
                    "Address" to details.address,
                    "Institute" to profile.institute
                )
            )
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    rows: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF00113A),
                fontWeight = FontWeight.SemiBold
            )

            rows.forEach { (label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
