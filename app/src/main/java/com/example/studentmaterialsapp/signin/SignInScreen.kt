package com.example.studentmaterialsapp.signin

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    onSignInClick: (String, String) -> Unit,
    onSignUpClick: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    // Context for SharedPreferences
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // "Remember Me" Checkbox State
    var isRememberMeChecked by remember { mutableStateOf(true) }

    var isPasswordVisible by remember { mutableStateOf(false) }
    val authState by viewModel.authState.collectAsState()

    // 1. LOAD SAVED CREDENTIALS ON START
    LaunchedEffect(Unit) {
        val savedEmail = sharedPref.getString("saved_email", "") ?: ""
        val savedPassword = sharedPref.getString("saved_password", "") ?: ""
        val rememberMe = sharedPref.getBoolean("remember_me", false)

        if (rememberMe) {
            email = savedEmail
            password = savedPassword
            isRememberMeChecked = true
        }
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                // Save credentials if "Remember Me" is checked
                val editor = sharedPref.edit()
                if (isRememberMeChecked) {
                    editor.putString("saved_email", email)
                    editor.putString("saved_password", password)
                    editor.putBoolean("remember_me", true)
                } else {
                    editor.clear() // Clear data if unchecked
                }
                editor.apply()

                // Navigate
                onSignInClick(email, password)
                viewModel.resetState()
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome Back!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
            trailingIcon = {
                val image = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(image, contentDescription = if (isPasswordVisible) "Hide password" else "Show password")
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Remember Me Checkbox
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isRememberMeChecked,
                onCheckedChange = { isRememberMeChecked = it }
            )
            Text(text = "Remember me on this device")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = authState !is AuthState.Loading
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Log In")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onSignUpClick) {
            Text("Don't have an account? Sign Up")
        }
    }
}
