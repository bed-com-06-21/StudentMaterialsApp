package com.example.studentmaterialsapp.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Data model for a Question
data class Question(
    val id: Int,
    val text: String,
    val options: List<String>,
    val correctOptionIndex: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    subjectName: String,
    topicName: String,
    onBackClick: () -> Unit
) {
    // Generate 50 random questions once when the screen loads based on Subject & Topic
    val questions = remember { generateMockQuestions(subjectName, topicName) }

    // State to store user answers: Map<QuestionId, SelectedOptionIndex>
    val userAnswers = remember { mutableStateMapOf<Int, Int>() }

    var isSubmitted by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("$topicName Quiz")
                        Text(
                            text = subjectName,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF673AB7), // Google Forms Purple
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF0EBF8) // Light purple background
    ) { innerPadding ->

        if (isSubmitted) {
            // RESULT VIEW
            ResultView(
                score = score,
                total = questions.size,
                onRetry = {
                    isSubmitted = false
                    userAnswers.clear()
                    score = 0
                },
                onBack = onBackClick
            )
        } else {
            // QUIZ LIST VIEW
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                // Header Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "$topicName Assessment",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Answer all 50 questions to complete the $subjectName module.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    // Purple top strip decoration
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .background(Color(0xFF673AB7))
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(questions) { index, question ->
                        QuizQuestionCard(
                            index = index + 1,
                            question = question,
                            selectedOption = userAnswers[question.id],
                            onOptionSelected = { optionIndex ->
                                userAnswers[question.id] = optionIndex
                            }
                        )
                    }

                    // Submit Button
                    item {
                        Button(
                            onClick = {
                                // Calculate Score
                                var currentScore = 0
                                questions.forEach { q ->
                                    if (userAnswers[q.id] == q.correctOptionIndex) {
                                        currentScore++
                                    }
                                }
                                score = currentScore
                                isSubmitted = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
                            // Optional: Disable button if not all answered? currently enabled.
                        ) {
                            Text("Submit Quiz")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizQuestionCard(
    index: Int,
    question: Question,
    selectedOption: Int?,
    onOptionSelected: (Int) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "$index. ${question.text}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            question.options.forEachIndexed { i, option ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (selectedOption == i),
                            onClick = { onOptionSelected(i) },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (selectedOption == i),
                        onClick = null, // handled by Row
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF673AB7))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = option)
                }
            }
        }
    }
}

@Composable
fun ResultView(score: Int, total: Int, onRetry: () -> Unit, onBack: () -> Unit) {
    // Calculate percentage
    val percentage = if (total > 0) (score.toFloat() / total.toFloat()) * 100 else 0f

    // Determine color based on pass/fail (50% threshold)
    val scoreColor = if (percentage >= 50) Color(0xFF4CAF50) else Color(0xFFE91E63) // Green or Pink/Red

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Quiz Completed!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your Score",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Display Score out of Total
                Text(
                    text = "$score / $total",
                    style = MaterialTheme.typography.displayLarge,
                    color = scoreColor,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Display Percentage
                Text(
                    text = "${percentage.toInt()}%",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Motivational Message
                Text(
                    text = if (percentage >= 80) "Excellent Work! 🌟"
                    else if (percentage >= 50) "Good Job! Keep it up. 👍"
                    else "Don't give up! Try again. 💪",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f).height(50.dp)
            ) {
                Text("Finish")
            }

            Button(
                onClick = onRetry,
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
            ) {
                Text("Try Again")
            }
        }
    }
}

// --- Helper to generate mock questions based on Subject & Topic ---
fun generateMockQuestions(subject: String, topic: String): List<Question> {
    val list = mutableListOf<Question>()

    for (i in 1..50) {
        val (qText, options) = when {
            // --- MATHEMATICS LOGIC ---
            subject == "Mathematics" && topic.contains("Algebra") -> {
                val a = (2..10).random()
                val b = (1..20).random()
                val ans = (1..10).random()
                val result = a * ans + b
                Pair(
                    "Solve for x: ${a}x + $b = $result",
                    listOf("$ans", "${ans+2}", "${ans-1}", "${ans*2}")
                )
            }
            subject == "Mathematics" && topic.contains("Geometry") -> {
                val side = (2..12).random()
                Pair(
                    "What is the area of a square with side length $side cm?",
                    listOf("${side*side} cm²", "${side*4} cm²", "${side+side} cm²", "${side*2} cm²")
                )
            }

            // --- BIOLOGY LOGIC ---
            subject == "Biology" && topic.contains("Cell") -> {
                val parts = listOf("Mitochondria", "Nucleus", "Ribosome", "Chloroplast")
                val functions = listOf("Powerhouse of the cell", "Control center", "Protein synthesis", "Photosynthesis")
                val randIdx = (0..3).random()
                // Simulating correct pairing logic roughly
                Pair(
                    "What is the primary function of the ${parts[randIdx]}?",
                    listOf(functions[randIdx], functions[(randIdx+1)%4], functions[(randIdx+2)%4], functions[(randIdx+3)%4])
                )
            }

            // --- ENGLISH LOGIC ---
            subject == "English" && topic.contains("Grammar") -> {
                val sentences = listOf("He ___ to the store.", "They ___ playing football.", "She ___ very happy.")
                val opts = listOf(listOf("went", "go", "gone", "going"), listOf("are", "is", "was", "am"), listOf("is", "are", "were", "have"))
                val randIdx = (0..2).random()
                Pair("Complete the sentence: ${sentences[randIdx]}", opts[randIdx])
            }

            // --- FALLBACK / GENERAL LOGIC ---
            else -> {
                Pair(
                    "$subject ($topic) Question #$i: Select the most appropriate answer.",
                    listOf("Correct Answer (Option A)", "Distractor B", "Distractor C", "Distractor D")
                )
            }
        }

        // Shuffle options so the answer isn't always the first one
        val shuffledOptions = options.shuffled()
        // In our logic above, index 0 of the original list was always the correct answer
        val correctIndex = shuffledOptions.indexOf(options[0])

        list.add(Question(i, qText, shuffledOptions, correctIndex))
    }
    return list
}
