package com.tinnomore.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tinnomore.data.db.entity.UserRole
import com.tinnomore.ui.screen.admin.AdminScreen
import com.tinnomore.ui.screen.auth.LoginScreen
import com.tinnomore.ui.screen.patient.CrisisScreen
import com.tinnomore.ui.screen.patient.PatientMainScreen
import com.tinnomore.ui.screen.specialist.SpecialistHomeScreen
import com.tinnomore.viewmodel.AuthViewModel

sealed class Screen(val route: String) {
    object Login          : Screen("login")
    object PatientMain    : Screen("patient_main")   // v0.2: reemplaza PatientHome
    object Crisis         : Screen("crisis")
    object SpecialistHome : Screen("specialist_home")
    object Admin          : Screen("admin")
}

@Composable
fun AppNavigation() {
    val navController  = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val currentUser      by authViewModel.currentUser.collectAsState()
    val isSessionLoading by authViewModel.isSessionLoading.collectAsState()
    // ── Mientras se verifica la sesión guardada, mostrar spinner ──────────
    if (currentUser?.role == UserRole.PATIENT || currentUser?.role == UserRole.SPECIALIST || currentUser?.role == UserRole.ADMIN) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    // ── Auto-navegar si ya hay sesión restaurada (sin tocar el NavHost) ───
    // Se ejecuta una sola vez al pasar isSessionLoading → false con usuario.
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            val dest = when (user.role) {
                UserRole.PATIENT    -> Screen.PatientMain.route
                UserRole.SPECIALIST -> Screen.SpecialistHome.route
                UserRole.ADMIN      -> Screen.Admin.route
            }
            // Solo navegar si estamos en la pantalla de login
            val current = navController.currentDestination?.route
            if (current == Screen.Login.route || current == null) {
                navController.navigate(dest) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        }
    }

    NavHost(navController = navController, startDestination = Screen.Login.route) {

        // ── Login ─────────────────────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel  = authViewModel,
                onLoginSuccess = { user ->
                    val dest = when (user.role) {
                        UserRole.PATIENT    -> Screen.PatientMain.route
                        UserRole.SPECIALIST -> Screen.SpecialistHome.route
                        UserRole.ADMIN      -> Screen.Admin.route
                    }
                    navController.navigate(dest) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Paciente: pantalla principal con barra inferior (v0.2) ────────
        composable(Screen.PatientMain.route) {
            PatientMainScreen(
                user          = currentUser,
                onCrisisClick = { navController.navigate(Screen.Crisis.route) },
                onLogout      = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Crisis: pantalla modal completa ───────────────────────────────
        composable(Screen.Crisis.route) {
            CrisisScreen(
                patientId = currentUser?.id ?: 0L,
                onBack    = { navController.popBackStack() }
            )
        }

        // ── Especialista ──────────────────────────────────────────────────
        composable(Screen.SpecialistHome.route) {
            SpecialistHomeScreen(
                specialist = currentUser,
                onLogout   = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        // ── Administrador ─────────────────────────────────────────────────
        composable(Screen.Admin.route) {
            AdminScreen(
                user     = currentUser,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                }
            )
        }
    }
}
