package com.example.studentmaterialsapp.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    studentName: String = "Student", // You can pass actual name later
    onBackClick: () -> Unit
) {
    val subjectProgressList = remember { getMockProgress() }

    // Calculate average for the donut chart
    val overallProgress = subjectProgressList.map { it.progressPercentage }.average().toFloat()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Progress") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        containerColor = Color(0xFFF5F5F5) // Light gray background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 1. OVERALL DONUT CHART
            CircularProgressCard(
                overallProgress = overallProgress,
                name = studentName,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. DETAILED BREAKDOWN HEADER
            Text(
                text = "Subject Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            // 3. LIST OF SUBJECT BARS
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    subjectProgressList.forEach { subject ->
                        SubjectProgressRow(subject = subject)
                        if (subject != subjectProgressList.last()) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}
