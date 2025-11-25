package com.example.studentmaterialsapp

sealed class Screen(val route: String) {

    // --- Authentication Screens ---
    data object SignIn : Screen("sign_in_screen")
    data object SignUp : Screen("sign_up_screen")

    // --- Main Dashboard ---
    data object ClassSelection : Screen("class_selection_screen")

    // --- Flow Screens ---

    // 1. Select Subject (e.g. "Grade 10" -> Select "Math")
    data object SubjectSelection : Screen("subject_selection_screen/{className}") {
        fun createRoute(className: String) = "subject_selection_screen/$className"
    }

    // 2. Select Topic (e.g. "Math" -> Select "Algebra")
    data object TopicSelection : Screen("topic_selection_screen/{classLevel}/{subjectName}") {
        fun createRoute(classLevel: String, subjectName: String) =
            "topic_selection_screen/$classLevel/$subjectName"
    }

    // 3. View Materials (e.g. "Algebra" -> Notes, Quiz, Flashcards)
    data object MaterialList : Screen("materials_screen/{classLevel}/{subjectName}/{topicName}") {
        fun createRoute(classLevel: String, subjectName: String, topicName: String) =
            "materials_screen/$classLevel/$subjectName/$topicName"
    }

    // --- Content Screens ---

    // Notes
    data object Summary : Screen("summary_screen/{topicName}") {
        fun createRoute(topicName: String) = "summary_screen/$topicName"
    }

    // Discussion
    data object Discussion : Screen("discussion_screen/{topicName}") {
        fun createRoute(topicName: String) = "discussion_screen/$topicName"
    }

    // Quiz
    data object Quiz : Screen("quiz_screen")

    // Flashcards
    data object Flashcards : Screen("flashcard_screen/{topicName}") {
        fun createRoute(topicName: String) = "flashcard_screen/$topicName"
    }

    // --- Feature Screens ---
    data object TimeTable : Screen("time_table_screen")
    data object AskAI : Screen("ask_ai_screen")
    data object Progress : Screen("progress_screen")
}
