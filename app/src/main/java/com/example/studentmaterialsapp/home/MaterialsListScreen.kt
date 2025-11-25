package com.example.studentmaterialsapp.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Data model for a study material (Book or Video)
data class MaterialItem(
    val title: String,
    val type: MaterialType,
    val url: String, // URL to open when clicked
    val description: String
)

enum class MaterialType {
    PDF, VIDEO
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialListScreen(
    classLevel: String,
    subjectName: String,
    topicName: String,
    onBackClick: () -> Unit,
    onQuizClick: (String, String) -> Unit, // (Subject, Topic)
    onFlashcardClick: (String) -> Unit,    // (Topic)
    onDiscussionClick: (String) -> Unit,   // (Topic)
    onSummaryClick: (String) -> Unit       // (Topic)
) {
    val context = LocalContext.current

    // Mock Data: In a real app, fetch this from Firebase based on 'topicName'
    val materials = listOf(
        MaterialItem(
            "Introduction to $topicName",
            MaterialType.PDF,
            "https://www.example.com/intro.pdf",
            "Comprehensive notes covering the basics."
        ),
        MaterialItem(
            "$topicName Video Tutorial",
            MaterialType.VIDEO,
            "https://www.youtube.com",
            "Watch this to understand the core concepts."
        ),
        MaterialItem(
            "Advanced $topicName Problems",
            MaterialType.PDF,
            "https://www.example.com/problems.pdf",
            "Practice questions for exam preparation."
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = topicName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$classLevel • $subjectName",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // SECTION 1: Interactive Tools (Grid-like layout using Rows)
            item {
                Text(
                    text = "Interactive Learning",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Row 1: Quiz & Flashcards
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FeatureCardSmall(
                            title = "Take Quiz",
                            icon = Icons.Default.Quiz,
                            color = Color(0xFF4CAF50), // Green
                            modifier = Modifier.weight(1f),
                            onClick = { onQuizClick(subjectName, topicName) }
                        )
                        FeatureCardSmall(
                            title = "Flashcards",
                            icon = Icons.Default.Style,
                            color = Color(0xFFFF9800), // Orange
                            modifier = Modifier.weight(1f),
                            onClick = { onFlashcardClick(topicName) }
                        )
                    }
                    // Row 2: Discussion & Summary
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FeatureCardSmall(
                            title = "Discussion",
                            icon = Icons.Default.Forum,
                            color = Color(0xFF2196F3), // Blue
                            modifier = Modifier.weight(1f),
                            onClick = { onDiscussionClick(topicName) }
                        )
                        FeatureCardSmall(
                            title = "My Summary",
                            icon = Icons.Default.Edit,
                            color = Color(0xFF9C27B0), // Purple
                            modifier = Modifier.weight(1f),
                            onClick = { onSummaryClick(topicName) }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Study Materials",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // SECTION 2: List of PDFs and Videos
            items(materials) { material ->
                MaterialCard(
                    material = material,
                    onClick = {
                        // Open URL in Browser
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(material.url))
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

// --- UI Components ---

@Composable
fun FeatureCardSmall(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(90.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

@Composable
fun MaterialCard(
    material: MaterialItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (material.type == MaterialType.PDF) Color(0xFFE3F2FD) else Color(0xFFFFEBEE)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (material.type == MaterialType.PDF) Icons.Default.PictureAsPdf else Icons.Default.PlayCircle,
                    contentDescription = null,
                    tint = if (material.type == MaterialType.PDF) Color(0xFF1E88E5) else Color(0xFFE53935),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = material.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = material.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Arrow Icon
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Note, // Or generic forward arrow
                contentDescription = "Open",
                tint = Color.LightGray
            )
        }
    }
}
