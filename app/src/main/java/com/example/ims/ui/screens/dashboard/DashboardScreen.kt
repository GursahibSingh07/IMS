package com.example.ims.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ims.core.MockUserProfile

private data class SearchOverlayItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconContainerColor: Color,
    val iconTint: Color,
    val onClick: () -> Unit
)

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

    val searchItems = remember(onOpenTimetable, onOpenStudentSearch) {
        listOf(
            SearchOverlayItem(
                title = "Search Bar",
                subtitle = "Dashboard module",
                icon = Icons.Outlined.Search,
                iconContainerColor = Color(0xFFDBE1FF),
                iconTint = Color(0xFF0B3AA4)
            ) {
                isSearchOverlayOpen = true
            },
            SearchOverlayItem(
                title = "News",
                subtitle = "Latest institute updates",
                icon = Icons.Outlined.Campaign,
                iconContainerColor = Color(0xFFD4E4F6),
                iconTint = Color(0xFF0B3A61)
            ) {
                isSearchOverlayOpen = false
            },
            SearchOverlayItem(
                title = "Time Table",
                subtitle = "Open scheduling module",
                icon = Icons.Outlined.TableRows,
                iconContainerColor = Color(0xFF002F1D),
                iconTint = Color.White
            ) {
                isSearchOverlayOpen = false
                onOpenTimetable()
            },
            SearchOverlayItem(
                title = "Student Search",
                subtitle = "Open student module",
                icon = Icons.Outlined.Person,
                iconContainerColor = Color(0xFFD1E1F4),
                iconTint = Color(0xFF09395E)
            ) {
                isSearchOverlayOpen = false
                onOpenStudentSearch()
            }
        )
    }

    val filteredItems = remember(searchItems, searchQuery) {
        if (searchQuery.isBlank()) {
            searchItems
        } else {
            searchItems.filter { item ->
                item.title.contains(searchQuery, ignoreCase = true) ||
                    item.subtitle.contains(searchQuery, ignoreCase = true)
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
                onOpenNavigation = { isNavigationMenuOpen = true },
                onOpenRightMenu = { isRightMenuOpen = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            SearchBarModule(
                onClick = {
                    searchQuery = ""
                    isSearchOverlayOpen = true
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            NewsModuleCard()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "A concise dashboard surface aligned with your IMS workflow.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF50606F),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 29.dp)
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
            modifier = Modifier.align(Alignment.CenterStart),
            visible = isNavigationMenuOpen,
            enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
        ) {
            NavigationMenuPanel(
                userProfile = userProfile,
                onClose = { isNavigationMenuOpen = false },
                onOpenTimetable = {
                    isNavigationMenuOpen = false
                    onOpenTimetable()
                },
                onOpenStudentSearch = {
                    isNavigationMenuOpen = false
                    onOpenStudentSearch()
                }
            )
        }

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopEnd),
            visible = isRightMenuOpen,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
        ) {
            RightMenuPanel(
                userProfile = userProfile,
                onClose = { isRightMenuOpen = false },
                onLogout = {
                    isRightMenuOpen = false
                    onLogout()
                }
            )
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
                    .padding(top = 76.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                SearchOverlayPanel(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    items = filteredItems,
                    onClose = { isSearchOverlayOpen = false }
                )
            }
        }
    }
}

@Composable
private fun DashboardTopBar(
    displayName: String,
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
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onOpenNavigation) {
                Icon(
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = "Open navigation menu",
                    tint = Color.White
                )
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
                        .size(32.dp)
                        .background(Color(0xFFCBD5E1), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayName.take(1),
                        color = Color(0xFF00113A),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(onClick = onOpenRightMenu) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = "Open profile menu",
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
            .requiredWidth(354.dp)
            .requiredHeight(46.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(23.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = Color(0xFF757682)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Search modules and shortcuts",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF757682)
            )
        }
    }
}

@Composable
private fun NewsModuleCard() {
    Card(
        modifier = Modifier
            .requiredWidth(354.dp)
            .heightIn(min = 280.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "News",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF00113A),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))

            NewsLine("Batch transfer approvals are open for the spring semester.")
            HorizontalDivider(color = Color(0xFFE5E7EB), modifier = Modifier.padding(vertical = 8.dp))
            NewsLine("Faculty meeting scheduled on Wednesday at 2:30 PM.")
            HorizontalDivider(color = Color(0xFFE5E7EB), modifier = Modifier.padding(vertical = 8.dp))
            NewsLine("Midterm result moderation closes this Friday.")
        }
    }
}

@Composable
private fun NewsLine(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF344054)
    )
}

@Composable
private fun NavigationMenuPanel(
    userProfile: MockUserProfile,
    onClose: () -> Unit,
    onOpenTimetable: () -> Unit,
    onOpenStudentSearch: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .requiredWidth(320.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(64.dp)
                    .background(Color(0xFF00113A))
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Menu,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Navigation",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close navigation",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            NavigationMenuItem(
                title = "Dashboard",
                subtitle = userProfile.institute,
                onClick = onClose
            )
            NavigationMenuItem(
                title = "Time Table",
                subtitle = "Open scheduling module",
                onClick = onOpenTimetable
            )
            NavigationMenuItem(
                title = "Student Search",
                subtitle = "Open student module",
                onClick = onOpenStudentSearch
            )
        }
    }
}

@Composable
private fun NavigationMenuItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF00113A),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF50606F)
            )
        }
    }
}

@Composable
private fun RightMenuPanel(
    userProfile: MockUserProfile,
    onClose: () -> Unit,
    onLogout: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(top = 16.dp, end = 14.dp)
            .requiredWidth(256.dp)
            .requiredHeight(196.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE5E7EB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Profile Menu",
                    color = Color(0xFF00113A),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close profile menu",
                        tint = Color(0xFF00113A)
                    )
                }
            }

            Text(
                text = userProfile.displayName,
                color = Color(0xFF00113A),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = userProfile.role,
                color = Color(0xFF475569),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = userProfile.email,
                color = Color(0xFF475569),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onLogout),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                    contentDescription = null,
                    tint = Color(0xFF00113A)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF00113A),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun SearchOverlayPanel(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    items: List<SearchOverlayItem>,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier
            .requiredWidth(358.dp)
            .requiredHeight(631.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(84.dp)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    singleLine = true,
                    placeholder = { Text("Search", color = Color(0xFF757682)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            tint = Color(0xFF757682)
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close search",
                            tint = Color(0xFF757682),
                            modifier = Modifier.clickable(onClick = onClose)
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .requiredWidth(238.dp)
                        .requiredHeight(40.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF2F4F6),
                        unfocusedContainerColor = Color(0xFFF2F4F6),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                items(items) { item ->
                    SearchResultRow(item = item)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(62.dp)
                    .background(Color(0xFFF2F4F6))
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .requiredWidth(48.dp)
                            .requiredHeight(20.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFE0E3E5))
                    )
                    Box(
                        modifier = Modifier
                            .requiredWidth(60.dp)
                            .requiredHeight(20.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFE0E3E5))
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultRow(item: SearchOverlayItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = item.onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .requiredWidth(40.dp)
                .requiredHeight(40.dp)
                .background(item.iconContainerColor, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = item.iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF00113A),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF50606F)
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF00113A)
        )
    }
}
