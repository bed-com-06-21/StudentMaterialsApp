package com.example.studentmaterialsapp.appnavigationscreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.studentmaterialsapp.Screen
import com.example.studentmaterialsapp.home.AskAIScreen
import com.example.studentmaterialsapp.home.ClassSelectionScreen
import com.example.studentmaterialsapp.home.FlashcardScreen
import com.example.studentmaterialsapp.home.MaterialListScreen
import com.example.studentmaterialsapp.home.ProgressScreen
import com.example.studentmaterialsapp.home.QuizScreen
import com.example.studentmaterialsapp.home.SubjectSelectionScreen
import com.example.studentmaterialsapp.home.SummaryScreen
import com.example.studentmaterialsapp.home.TopicSelectionScreen
import com.example.studentmaterialsapp.timetable.TimeTableScreen
import com.example.studentmaterialsapp.signin.SignInScreen
import com.example.studentmaterialsapp.signin.SignUpScreen
import com.example.studentmaterialsapp.social.DiscussionScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    val startDestination = if (auth.currentUser != null) {
        Screen.ClassSelection.route
    } else {
        Screen.SignIn.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        // --- Authentication ---
        composable(Screen.SignIn.route) {
            SignInScreen(
                onSignInClick = { _, _ ->
                    navController.navigate(Screen.ClassSelection.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                },
                onSignUpClick = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpClick = { _, _, _ ->
                    navController.navigate(Screen.SignIn.route)
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        // --- Home Dashboard ---
        composable(Screen.ClassSelection.route) {
            ClassSelectionScreen(
                onClassSelected = { className ->
                    navController.navigate(Screen.SubjectSelection.createRoute(className))
                },
                onTimeTableClick = {
                    navController.navigate(Screen.TimeTable.route)
                },
                onAskAIClick = {
                    navController.navigate(Screen.AskAI.route)
                },
                onSignOutClick = {
                    auth.signOut()
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        // --- Subject Selection ---
        composable(
            route = Screen.SubjectSelection.route,
            arguments = listOf(navArgument("className") { type = NavType.StringType })
        ) { backStackEntry ->
            val className = backStackEntry.arguments?.getString("className") ?: "Unknown Class"

            SubjectSelectionScreen(
                className = className,
                onBackClick = { navController.popBackStack() },
                onSummaryClick = { subject ->
                    // Direct access to Summary (if user clicks "Notes" on subject card)
                    // Note: Ideally this should probably go to TopicSelection first, but keeping logic flexible.
                    // But since we want Topic Selection visible, we should prefer that flow.
                    // However, SubjectSelectionScreen has specific buttons for "Notes" and "Chat".
                    // If we want Topic Selection, maybe "Notes" button should go there?
                    // Let's redirect "Notes" button to Topic Selection for now to ensure it's seen.
                    navController.navigate(Screen.TopicSelection.createRoute(className, subject))
                },
                onDiscussionClick = { subject ->
                    navController.navigate(Screen.Discussion.createRoute(subject))
                },
                onTimeTableClick = {
                    navController.navigate(Screen.TimeTable.route)
                },
                onProgressClick = {
                    navController.navigate(Screen.Progress.route)
                }
            )
        }

        // --- Topic Selection (NEW) ---
        composable(
            route = Screen.TopicSelection.route,
            arguments = listOf(
                navArgument("classLevel") { type = NavType.StringType },
                navArgument("subjectName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val classLevel = backStackEntry.arguments?.getString("classLevel") ?: ""
            val subjectName = backStackEntry.arguments?.getString("subjectName") ?: ""

            TopicSelectionScreen(
                classLevel = classLevel,
                subjectName = subjectName,
                onTopicSelected = { topic ->
                    // Navigate to Material List
                    navController.navigate(Screen.MaterialList.createRoute(classLevel, subjectName, topic))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- Material List (NEW) ---
        composable(
            route = Screen.MaterialList.route,
            arguments = listOf(
                navArgument("classLevel") { type = NavType.StringType },
                navArgument("subjectName") { type = NavType.StringType },
                navArgument("topicName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val classLevel = backStackEntry.arguments?.getString("classLevel") ?: ""
            val subjectName = backStackEntry.arguments?.getString("subjectName") ?: ""
            val topicName = backStackEntry.arguments?.getString("topicName") ?: ""

            MaterialListScreen(
                classLevel = classLevel,
                subjectName = subjectName,
                topicName = topicName,
                onBackClick = { navController.popBackStack() },
                onQuizClick = { _, _ -> navController.navigate(Screen.Quiz.route) },
                onFlashcardClick = { topic -> navController.navigate(Screen.Flashcards.createRoute(topic)) },
                onDiscussionClick = { topic -> navController.navigate(Screen.Discussion.createRoute(topic)) },
                onSummaryClick = { topic -> navController.navigate(Screen.Summary.createRoute(topic)) }
            )
        }

        // --- Features ---
        composable(Screen.TimeTable.route) {
            TimeTableScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.AskAI.route) {
            AskAIScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.Progress.route) {
            ProgressScreen(onBackClick = { navController.popBackStack() })
        }

        // --- Content Screens ---
        
        // Summary
        composable(
            route = Screen.Summary.route,
            arguments = listOf(navArgument("topicName") { type = NavType.StringType })
        ) { backStackEntry ->
            val topicName = backStackEntry.arguments?.getString("topicName") ?: "General"
            SummaryScreen(
                topicName = topicName,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Discussion
        composable(
            route = Screen.Discussion.route,
            arguments = listOf(navArgument("topicName") { type = NavType.StringType })
        ) { backStackEntry ->
            val topicName = backStackEntry.arguments?.getString("topicName") ?: "General"
            DiscussionScreen(
                topicName = topicName,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Quiz
        composable(Screen.Quiz.route) {
            QuizScreen(
                subjectName = "General",
                topicName = "Assessment",
                onBackClick = { navController.popBackStack() }
            )
        }

        // Flashcards (NEW)
        composable(
            route = Screen.Flashcards.route,
            arguments = listOf(navArgument("topicName") { type = NavType.StringType })
        ) { backStackEntry ->
            val topicName = backStackEntry.arguments?.getString("topicName") ?: "General"
            FlashcardScreen(
                topicName = topicName,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
