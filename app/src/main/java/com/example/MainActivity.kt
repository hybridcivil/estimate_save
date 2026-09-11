package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ProjectSummaryScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.EstimateViewModel

enum class ScreenState {
    SPLASH,
    LOGIN,
    DASHBOARD,
    SUMMARY
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BuildingEstimateApp()
                }
            }
        }
    }
}

@Composable
fun BuildingEstimateApp(
    viewModel: EstimateViewModel = viewModel()
) {
    val context = LocalContext.current
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val userRole by viewModel.userRole.collectAsState()

    val projects by viewModel.allProjects.collectAsState()
    val activeProject by viewModel.activeProject.collectAsState()
    val activeItems by viewModel.activeProjectItems.collectAsState()
    val summary by viewModel.projectSummary.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    var currentScreen by remember { mutableStateOf(ScreenState.SPLASH) }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearUserMessage()
        }
    }

    Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
        when (screen) {
            ScreenState.SPLASH -> {
                SplashScreen(
                    onSplashFinished = {
                        currentScreen = if (isLoggedIn) ScreenState.DASHBOARD else ScreenState.LOGIN
                    }
                )
            }

            ScreenState.LOGIN -> {
                LoginScreen(
                    onLoginSuccess = { name, role, rememberMe ->
                        viewModel.login(name, role, rememberMe)
                        currentScreen = ScreenState.DASHBOARD
                    }
                )
            }

            ScreenState.DASHBOARD -> {
                DashboardScreen(
                    engineerName = userName,
                    userRole = userRole,
                    projects = projects,
                    activeProject = activeProject,
                    activeItems = activeItems,
                    summary = summary,
                    onSelectProject = { viewModel.selectProject(it) },
                    onCreateProject = { title, client, location, notes, currency, cement, sand, agg, steel, brick ->
                        viewModel.createProject(title, client, location, notes, currency, cement, sand, agg, steel, brick)
                    },
                    onUpdateRates = { cement, sand, agg, steel, brick, currency ->
                        viewModel.updateProjectRates(cement, sand, agg, steel, brick, currency)
                    },
                    onSaveEstimateItem = { category, name, specs, cement, sand, agg, steelTotal, steelBreakdown, bricks, customQty, customUnit, customRate ->
                        viewModel.saveEstimateItem(
                            category = category,
                            name = name,
                            specs = specs,
                            cementBags = cement,
                            sandCft = sand,
                            aggregateCft = agg,
                            steelTotalKg = steelTotal,
                            steelBreakdown = steelBreakdown,
                            bricksCount = bricks,
                            customQty = customQty,
                            customUnit = customUnit,
                            customRate = customRate
                        )
                    },
                    onDeleteItem = { viewModel.deleteEstimateItem(it) },
                    onNavigateToSummary = { currentScreen = ScreenState.SUMMARY },
                    onLogout = {
                        viewModel.logout()
                        currentScreen = ScreenState.LOGIN
                    }
                )
            }

            ScreenState.SUMMARY -> {
                ProjectSummaryScreen(
                    summary = summary,
                    engineerName = userName,
                    onBack = { currentScreen = ScreenState.DASHBOARD },
                    onDeleteItem = { viewModel.deleteEstimateItem(it) }
                )
            }
        }
    }
}

