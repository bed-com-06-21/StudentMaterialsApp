package com.example.studentmaterialsapp.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectSelectionScreen(
    className: String,
    onSummaryClick: (String) -> Unit,
    onDiscussionClick: (String) -> Unit,
    onTimeTableClick: () -> Unit,
    onProgressClick: () -> Unit,
    onBackClick: () -> Unit
) {
    // Example subjects
    val subjects = listOf("Mathematics", "Physics", "Chemistry", "Biology", "English", "History", "Geography", "Agriculture")

    // State for the selected mode (Notes vs Chat)
    var selectedMode by remember { mutableIntStateOf(0) } // 0 = Notes, 1 = Chat
    val modes = listOf("Study Notes", "Discussion")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(className, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // --- Feature Buttons (Time Table & Progress) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FeatureButton(
                    text = "Time Table",
                    icon = Icons.Default.CalendarToday,
                    color = MaterialTheme.colorScheme.tertiary,
                    onClick = onTimeTableClick,
                    modifier = Modifier.weight(1f)
                )
                FeatureButton(
                    text = "Progress",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    color = MaterialTheme.colorScheme.secondary,
                    onClick = onProgressClick,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- MODE SELECTION (Notes vs Chat) ---
            Text(
                text = "What would you like to do?",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            TabRow(
                selectedTabIndex = selectedMode,
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedMode]),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                modes.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedMode == index,
                        onClick = { selectedMode = index },
                        text = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (index == 0) Icons.Default.EditNote else Icons.AutoMirrored.Filled.Chat, 
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(title, fontWeight = FontWeight.SemiBold)
                            }
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Select a Subject:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Subject Grid ---
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(subjects) { subject ->
                    SubjectCard(
                        subjectName = subject,
                        isChatMode = selectedMode == 1,
                        onClick = {
                            if (selectedMode == 0) {
                                onSummaryClick(subject)
                            } else {
                                onDiscussionClick(subject)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.height(50.dp),
        elevation = ButtonDefaults.buttonElevation(2.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun SubjectCard(
    subjectName: String,
    isChatMode: Boolean,
    onClick: () -> Unit
) {
    // Define a gradient for the card background
    val cardGradient = Brush.verticalGradient(
        colors = listOf(
            if (isChatMode) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.surface
        )
    )

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = cardGradient)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Subject Icon
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.LibraryBooks,
                    contentDescription = null,
                    tint = if (isChatMode) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp).padding(bottom = 8.dp)
                )
                
                Text(
                    text = subjectName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Indicator of action
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isChatMode) "Open Chat" else "View Notes",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isChatMode) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (isChatMode) Icons.AutoMirrored.Filled.Chat else Icons.Default.EditNote,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (isChatMode) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
