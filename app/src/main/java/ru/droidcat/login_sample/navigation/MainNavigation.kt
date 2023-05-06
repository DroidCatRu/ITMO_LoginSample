package ru.droidcat.login_sample.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.droidcat.login_sample.greeting.GreetingScreen
import ru.droidcat.login_sample.login.LoginScreen
import ru.droidcat.login_sample.profile.ProfileScreen
import ru.droidcat.login_sample.register.RegisterScreen

@Composable
fun NavHostController.MainNavigation() {
    NavHost(navController = this, startDestination = "greeting") {
        composable("greeting") {
            GreetingScreen(
                onLoginClick = { navigateToLogin() },
                onRegisterClick = { navigateToRegister() }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegister = { navigateToProfile() }
            )
        }
        composable("login") {
            LoginScreen(
                onLogin = { navigateToProfile() }
            )
        }
        composable("profile") {
            ProfileScreen(
                onLogout = { navigateToGreeting() }
            )
        }
    }
}

fun NavHostController.navigateToGreeting() {
    if (currentDestination?.route == "greeting") {
        return
    }
    navigate("greeting") {
        popUpTo(graph.startDestinationId) { inclusive = true }
    }
    graph.setStartDestination("greeting")
}

fun NavHostController.navigateToLogin() {
    if (currentDestination?.route == "login") {
        return
    }
    navigate("login")
}

fun NavHostController.navigateToRegister() {
    if (currentDestination?.route == "register") {
        return
    }
    navigate("register")
}

fun NavHostController.navigateToProfile() {
    if (currentDestination?.route == "profile") {
        return
    }
    navigate("profile") {
        popUpTo(graph.startDestinationId) { inclusive = true }
    }
    graph.setStartDestination("profile")
}