package com.example.studentmaterialsapp.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 1. Data Model
data class Flashcard(
    val id: Int,
    val question: String,
    val answer: String
)

// 2. Main Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(
    topicName: String,
    onBackClick: () -> Unit
) {
    // Generate mock cards based on the topic
    val flashcards = remember { generateMockFlashcards(topicName) }

    // State for the swiping mechanism
    val pagerState = rememberPagerState(pageCount = { flashcards.size })

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Flashcards", fontWeight = FontWeight.Bold)
                        Text(
                            text = topicName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // The Swipeable Area
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 32.dp), // Shows hint of next/prev card
                pageSpacing = 16.dp,
                modifier = Modifier.weight(1f)
            ) { page ->
                // Center the card within the pager logic
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    FlippableCard(
                        flashcard = flashcards[page],
                        cardNumber = "${page + 1} / ${flashcards.size}"
                    )
                }
            }

            // Bottom Hint
            Column(
                modifier = Modifier.padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Swipe left for next card",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap card to flip",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// 3. The Flippable Card Component
@Composable
fun FlippableCard(
    flashcard: Flashcard,
    cardNumber: String,
    modifier: Modifier = Modifier
) {
    // State to track if the card is flipped (true = showing answer)
    var isFlipped by remember { mutableStateOf(false) }

    val density = LocalDensity.current.density

    // Animate the rotation value (0f to 180f)
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "cardFlip"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            // IMPORTANT FIX: clickable MUST come before graphicsLayer
            // This ensures the touch target stays flat and reachable even when visual is rotated
            .clickable { isFlipped = !isFlipped }
            .graphicsLayer {
                rotationY = rotation // Rotate along Y-axis
                cameraDistance = 12f * density // Adds 3D depth perspective
            },
        contentAlignment = Alignment.Center
    ) {
        if (rotation <= 90f) {
            // --- FRONT SIDE (Question) ---
            CardFace(
                text = flashcard.question,
                subText = "Tap to see answer",
                countText = cardNumber,
                backgroundColor = MaterialTheme.colorScheme.primaryContainer
            )
        } else {
            // --- BACK SIDE (Answer) ---
            // We must rotate the content another 180 deg so text isn't mirrored
            Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                CardFace(
                    text = flashcard.answer,
                    subText = "Tap to see question",
                    countText = cardNumber,
                    backgroundColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            }
        }
    }
}

// 4. Reusable UI for the Card Face
@Composable
fun CardFace(text: String, subText: String, countText: String, backgroundColor: Color) {
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
        ) {
            // Card Count (Top Right)
            Text(
                text = countText,
                modifier = Modifier.align(Alignment.TopEnd),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            // Main Text (Center)
            Text(
                text = text,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp,
                modifier = Modifier.align(Alignment.Center)
            )

            // Instruction (Bottom)
            Text(
                text = subText,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.BottomCenter),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

// 5. Helper to generate Mock Data
fun generateMockFlashcards(topic: String): List<Flashcard> {
    return when {
        topic.contains("Algebra") -> listOf(
            Flashcard(1, "What is a variable?", "A symbol (like x or y) used to represent a number."),
            Flashcard(2, "What is the slope formula?", "m = (y2 - y1) / (x2 - x1)"),
            Flashcard(3, "What is a quadratic equation?", "An equation where the highest exponent of the variable is 2."),
            Flashcard(4, "Solve: 2x = 10", "x = 5")
        )
        topic.contains("Cell") -> listOf(
            Flashcard(1, "What is the Powerhouse of the Cell?", "Mitochondria"),
            Flashcard(2, "What does the Nucleus do?", "It controls the cell's activities and contains DNA."),
            Flashcard(3, "Difference between Plant and Animal cells?", "Plant cells have a Cell Wall and Chloroplasts; Animal cells do not."),
            Flashcard(4, "What is Cytoplasm?", "The jelly-like substance filling the cell.")
        )
        topic.contains("History") -> listOf(
            Flashcard(1, "When did Malawi gain independence?", "July 6, 1964"),
            Flashcard(2, "Who was the first President?", "Dr. Hastings Kamuzu Banda"),
            Flashcard(3, "What was the Federation?", "The Federation of Rhodesia and Nyasaland (1953-1963).")
        )
        // General Fallback
        else -> listOf(
            Flashcard(1, "Key Concept 1 of $topic", "Definition of Concept 1..."),
            Flashcard(2, "Key Concept 2 of $topic", "Definition of Concept 2..."),
            Flashcard(3, "Important Date/Formula", "The specific detail to memorize."),
            Flashcard(4, "Summary of $topic", "A brief overview of this topic.")
        )
    }
}
