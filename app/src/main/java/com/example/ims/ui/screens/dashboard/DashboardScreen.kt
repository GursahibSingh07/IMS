package com.example.ims.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.HowToReg
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material.icons.outlined.NorthEast
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TableRows
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ims.core.MockUserProfile
import com.example.ims.core.Role

private data class OverlayStudent(
    val name: String,
    val studentId: String,
    val program: String,
    val onClick: () -> Unit
)

private data class OverlayNews(
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit
)

private data class OverlayCourse(
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit
)

@Suppress("UNUSED_PARAMETER")
@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    userProfile: MockUserProfile,
    onOpenTimetable: () -> Unit,
    onOpenStudentSearch: () -> Unit,
    onLogout: () -> Unit
) {
    var isNavigationMenuOpen by rememberSaveable { mutableStateOf(false) }
    var isRightMenuOpen by rememberSaveable { mutableStateOf(false) }
    var isSearchOverlayOpen by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val students = remember(onOpenStudentSearch) {
        listOf(
            OverlayStudent(
                name = "Jameson Miller",
                studentId = "#2024091",
                program = "Computer Science",
                onClick = onOpenStudentSearch
            ),
            OverlayStudent(
                name = "Jamie Chen",
                studentId = "#2024102",
                program = "Digital Marketing",
                onClick = onOpenStudentSearch
            )
        )
    }

    val news = remember {
        listOf(
            OverlayNews(
                title = "Jamaica Summer Internship",
                subtitle = "Applications open until May 15th",
                onClick = {}
            )
        )
    }

    val courses = remember(onOpenTimetable) {
        listOf(
            OverlayCourse(
                title = "Java & Microservices",
                subtitle = "CS304 - Dr. Aris Thorne",
                onClick = onOpenTimetable
            )
        )
    }

    val filteredStudents = remember(students, searchQuery) {
        if (searchQuery.isBlank() || userProfile.role != Role.ACADEMIC_OFFICE) {
            emptyList()
        } else {
            students.filter { student ->
                student.name.contains(searchQuery, ignoreCase = true) ||
                    student.studentId.contains(searchQuery, ignoreCase = true) ||
                    student.program.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val filteredNews = remember(news, searchQuery) {
        if (searchQuery.isBlank()) {
            news
        } else {
            news.filter { item ->
                item.title.contains(searchQuery, ignoreCase = true) ||
                    item.subtitle.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val filteredCourses = remember(courses, searchQuery) {
        if (searchQuery.isBlank()) {
            courses
        } else {
            courses.filter { course ->
                course.title.contains(searchQuery, ignoreCase = true) ||
                    course.subtitle.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val blurredModifier = if (isSearchOverlayOpen) Modifier.blur(1.dp) else Modifier

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(blurredModifier),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DashboardTopBar(
                displayName = userProfile.displayName,
                showNavigationButton = !isNavigationMenuOpen,
                onOpenNavigation = { isNavigationMenuOpen = true },
                onOpenRightMenu = { isRightMenuOpen = true }
            )

            Spacer(modifier = Modifier.height(22.dp))

            SearchBarModule(
                onClick = {
                    searchQuery = ""
                    isSearchOverlayOpen = true
                }
            )

            Spacer(modifier = Modifier.height(40.dp))

            LatestNewsSection()

            Spacer(modifier = Modifier.height(16.dp))
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
                },
                onOpenTimetable = {
                    isNavigationMenuOpen = false
                    onOpenTimetable()
                },
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

        if (isSearchOverlayOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x73000000))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        isSearchOverlayOpen = false
                    }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 72.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                SearchOverlayPanel(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    students = filteredStudents,
                    news = filteredNews,
                    courses = filteredCourses,
                    onViewAllStudents = {
                        isSearchOverlayOpen = false
                        onOpenStudentSearch()
                    },
                    onClose = { isSearchOverlayOpen = false }
                )
            }
        }
    }
}

@Composable
private fun DashboardTopBar(
    displayName: String,
    showNavigationButton: Boolean,
    onOpenNavigation: () -> Unit,
    onOpenRightMenu: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(56.dp)
            .background(Color(0xFF00113A))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.requiredWidth(40.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (showNavigationButton) {
                    IconButton(onClick = onOpenNavigation) {
                        Icon(
                            imageVector = Icons.Outlined.Menu,
                            contentDescription = "Open navigation menu",
                            tint = Color.White
                        )
                    }
                }
            }

            Text(
                text = "IMS Dashboard",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF27457A)),
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
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = "Open system preferences",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBarModule(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .requiredHeight(46.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(23.dp),
        border = BorderStroke(1.dp, Color(0xFFC5C6D2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = Color(0xFF002366),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Search",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF757682)
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = null,
                tint = Color(0xFF9AA3AF),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun LatestNewsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
    ) {
        Text(
            text = "Latest News",
            color = Color(0xFF0E2B5B),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(12.dp))

        FeaturedNewsCard(
            headline = "Expansion Project Milestone Reached on Main Campus"
        )

        Spacer(modifier = Modifier.height(14.dp))

        CompactNewsCard(
            title = "New Technology Grants Announced for Q3",
            summary = "The Department of Innovation is releasing a new series of funding opportunities."
        )

        Spacer(modifier = Modifier.height(12.dp))

        CompactNewsCard(
            title = "Annual Leadership Summit Registration Open",
            summary = "Secure your spot for the upcoming summit featuring keynote speakers and workshops."
        )
    }
}

@Composable
private fun FeaturedNewsCard(headline: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFFC9D1DD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(340.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF7EB0D9),
                                Color(0xFFAEC4D8),
                                Color(0xFFBCC9D6)
                            )
                        )
                    )
            )
            Text(
                text = headline,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF1D2939),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
            )
        }
    }
}

@Composable
private fun CompactNewsCard(
    title: String,
    summary: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFFD1D5DB)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .requiredWidth(64.dp)
                    .requiredHeight(64.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFD0D4D8))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF111827),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4B5563),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun NavigationMenuPanel(
    userRole: Role,
    onOpenDashboard: () -> Unit,
    onOpenTimetable: () -> Unit,
    onOpenStudentRegistry: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenLogout: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .requiredWidth(342.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 22.dp, end = 18.dp, top = 92.dp, bottom = 28.dp)
        ) {
            Text(
                text = "IMS Dashboard",
                color = Color(0xFF002366),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            NavigationMenuEntry(
                icon = Icons.Outlined.School,
                title = "Admission",
                onClick = onOpenDashboard
            )
            NavigationMenuEntry(
                icon = Icons.Outlined.HowToReg,
                title = "Attendance",
                onClick = onOpenDashboard
            )
            NavigationMenuEntry(
                icon = Icons.Outlined.AccountBalanceWallet,
                title = "Finance",
                onClick = onOpenDashboard
            )
            NavigationMenuEntry(
                icon = Icons.Outlined.CalendarMonth,
                title = "Time Table",
                onClick = onOpenTimetable
            )
            NavigationMenuEntry(
                icon = Icons.Outlined.MenuBook,
                title = "Courses",
                onClick = onOpenDashboard
            )

            if (userRole == com.example.ims.core.Role.ACADEMIC_OFFICE) {
                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "MANAGEMENT",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
                    color = Color(0xFF8A95A9),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                NavigationMenuEntry(
                    icon = Icons.Outlined.Groups,
                    title = "Student Registry",
                    onClick = onOpenStudentRegistry
                )
            }
            
            NavigationMenuEntry(
                icon = Icons.Outlined.Settings,
                title = "Settings",
                onClick = onOpenSettings
            )

            Spacer(modifier = Modifier.weight(1f))

            NavigationMenuEntry(
                icon = Icons.AutoMirrored.Outlined.ExitToApp,
                title = "Logout",
                onClick = onOpenLogout
            )
        }
    }
}

@Composable
fun NavigationMenuEntry(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(50.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF52617A),
            modifier = Modifier.size(21.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF52617A)
        )
    }
}

@Composable
fun RightMenuPanel(onLogout: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(top = 58.dp, end = 12.dp)
            .requiredWidth(250.dp)
            .requiredHeight(230.dp),
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE1E4E7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = "SYSTEM PREFERENCES",
                color = Color(0xFF7A8291),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsPreferenceRow(
                icon = Icons.Outlined.Language,
                title = "Language"
            )
            SettingsPreferenceRow(
                icon = Icons.Outlined.MonetizationOn,
                title = "Currency"
            )
            SettingsPreferenceRow(
                icon = Icons.Outlined.AccessTime,
                title = "Time Zone"
            )
        }
    }
}

@Composable
fun SettingsPreferenceRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit = {},
    titleColor: Color = Color(0xFF1A1F29)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(44.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (titleColor == Color(0xFF1A1F29)) Color(0xFF0B1E44) else titleColor,
            modifier = Modifier.size(19.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            color = titleColor,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF555E6E),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SearchOverlayPanel(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    students: List<OverlayStudent>,
    news: List<OverlayNews>,
    courses: List<OverlayCourse>,
    onViewAllStudents: () -> Unit,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier
            .requiredWidth(354.dp)
            .requiredHeight(505.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFF1C1D1F)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    singleLine = true,
                    placeholder = { Text("Search", color = Color(0xFF7B8190)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            tint = Color(0xFF002366)
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close search",
                            tint = Color(0xFF8C94A2),
                            modifier = Modifier.clickable(onClick = onClose)
                        )
                    },
                    shape = RoundedCornerShape(23.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeight(46.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF8F9FB),
                        unfocusedContainerColor = Color(0xFFF8F9FB),
                        focusedBorderColor = Color(0xFFBFC4CD),
                        unfocusedBorderColor = Color(0xFFBFC4CD)
                    )
                )
            }

            HorizontalDivider(color = Color(0xFFD5D9DF))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (students.isNotEmpty()) {
                    item {
                        OverlaySectionHeader(
                            title = "STUDENTS",
                            actionText = "View All",
                            onActionClick = onViewAllStudents
                        )
                    }
                    items(students) { student ->
                        StudentOverlayRow(item = student)
                    }
                }

                if (news.isNotEmpty()) {
                    item {
                        OverlaySectionHeader(title = "NEWS & ANNOUNCEMENTS")
                    }
                    items(news) { newsItem ->
                        NewsOverlayRow(item = newsItem)
                    }
                }

                if (courses.isNotEmpty()) {
                    item {
                        OverlaySectionHeader(title = "COURSES")
                    }
                    items(courses) { course ->
                        CourseOverlayRow(item = course)
                    }
                }

                if (students.isEmpty() && news.isEmpty() && courses.isEmpty()) {
                    item {
                        Text(
                            text = "No matching results",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF667085),
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFD5D9DF))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(52.dp)
                    .background(Color(0xFFF2F4F6))
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OverlayFooterItem(icon = Icons.Outlined.Check, label = "SELECT")
                OverlayFooterItem(icon = Icons.Outlined.NearMe, label = "NAVIGATE")
                Text(
                    text = "SCHOLARSLATE\nV2.4.0",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF738093),
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun OverlaySectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.8.sp),
            color = Color(0xFF64748B),
            fontWeight = FontWeight.Bold
        )

        if (actionText != null) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF00113A),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onActionClick)
            )
        }
    }
}

@Composable
private fun StudentOverlayRow(item: OverlayStudent) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = item.onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(Color(0xFFD7DCE3), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.name.take(1),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF1D2939),
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF1D2939),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "ID: ${item.studentId} - ${item.program}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF667085)
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFFC5CCD8)
        )
    }
}

@Composable
private fun NewsOverlayRow(item: OverlayNews) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = item.onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF01452C)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Campaign,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF1D2939),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF667085),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            imageVector = Icons.Outlined.NorthEast,
            contentDescription = null,
            tint = Color(0xFFC5CCD8)
        )
    }
}

@Composable
private fun CourseOverlayRow(item: OverlayCourse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = item.onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(Color(0xFFD5E4FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.TableRows,
                contentDescription = null,
                tint = Color(0xFF2A4386),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF1D2939),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF667085),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFFC5CCD8)
        )
    }
}

@Composable
private fun OverlayFooterItem(icon: ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF738093)
        )
    }
}

