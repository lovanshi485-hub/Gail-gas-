package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CustomerEntity
import com.example.ui.screens.CustomerDetailScreen
import com.example.ui.screens.CustomerFormScreen
import com.example.ui.screens.CustomerListScreen
import com.example.ui.screens.DailyVisitTrackerScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DprEditorScreen
import com.example.ui.screens.FeasibilityScreen
import com.example.ui.screens.GasInTrackingScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ReportsAndAnalyticsScreen
import com.example.ui.screens.SettingsAndProfileScreen
import com.example.ui.screens.TnfAndRegistrationScreen
import com.example.ui.theme.GailGasTheme
import com.example.ui.theme.GailNavy
import com.example.ui.theme.GailOrange
import com.example.viewmodel.GailGasViewModel

enum class AppScreen {
    DASHBOARD,
    CUSTOMERS,
    FEASIBILITY,
    TNF_REGISTRATION,
    GAS_IN,
    VISIT_TRACKER,
    DPR_EDITOR,
    REPORTS,
    SETTINGS,
    CUSTOMER_DETAIL,
    CUSTOMER_FORM
}

class MainActivity : ComponentActivity() {

    private val viewModel: GailGasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val isLoggedIn by viewModel.isLoggedIn.collectAsState()

            GailGasTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!isLoggedIn) {
                        LoginScreen(
                            viewModel = viewModel,
                            onLoginSuccess = { /* state triggers recomposition */ }
                        )
                    } else {
                        MainAppContent(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: GailGasViewModel) {
    var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }
    var selectedCustomerForDetail by remember { mutableStateOf<CustomerEntity?>(null) }
    var customerToEdit by remember { mutableStateOf<CustomerEntity?>(null) }

    // Android back-handling
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
        if (currentScreen == AppScreen.CUSTOMER_FORM && selectedCustomerForDetail != null) {
            currentScreen = AppScreen.CUSTOMER_DETAIL
        } else if (currentScreen == AppScreen.CUSTOMER_DETAIL) {
            currentScreen = AppScreen.CUSTOMERS
        } else {
            currentScreen = AppScreen.DASHBOARD
        }
    }

    val navItems = listOf(
        NavigationItem("Dashboard", Icons.Default.Dashboard, AppScreen.DASHBOARD),
        NavigationItem("Customers", Icons.Default.People, AppScreen.CUSTOMERS),
        NavigationItem("Feasibility", Icons.Default.Assignment, AppScreen.FEASIBILITY),
        NavigationItem("Gas-in", Icons.Default.LocalGasStation, AppScreen.GAS_IN),
        NavigationItem("DPR", Icons.Default.Description, AppScreen.DPR_EDITOR),
        NavigationItem("Settings", Icons.Default.Settings, AppScreen.SETTINGS)
    )

    Scaffold(
        bottomBar = {
            // Standard M3 Navigation Bar with proper system navigation insets
            NavigationBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                containerColor = GailNavy,
                contentColor = Color.White
            ) {
                navItems.forEach { item ->
                    val selected = currentScreen == item.screen ||
                            (item.screen == AppScreen.CUSTOMERS && (currentScreen == AppScreen.CUSTOMER_DETAIL || currentScreen == AppScreen.CUSTOMER_FORM))

                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentScreen = item.screen },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = { Text(item.label, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = GailOrange,
                            indicatorColor = GailOrange,
                            unselectedIconColor = Color(0xFF94A3B8),
                            unselectedTextColor = Color(0xFF94A3B8)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToCustomers = { currentScreen = AppScreen.CUSTOMERS },
                    onNavigateToFeasibility = { currentScreen = AppScreen.FEASIBILITY },
                    onNavigateToTnf = { currentScreen = AppScreen.TNF_REGISTRATION },
                    onNavigateToGasIn = { currentScreen = AppScreen.GAS_IN },
                    onNavigateToVisitTracker = { currentScreen = AppScreen.VISIT_TRACKER },
                    onNavigateToDpr = { currentScreen = AppScreen.DPR_EDITOR },
                    onCustomerClick = {
                        selectedCustomerForDetail = it
                        currentScreen = AppScreen.CUSTOMER_DETAIL
                    }
                )

                AppScreen.CUSTOMERS -> CustomerListScreen(
                    viewModel = viewModel,
                    onAddCustomer = {
                        customerToEdit = null
                        currentScreen = AppScreen.CUSTOMER_FORM
                    },
                    onEditCustomer = {
                        customerToEdit = it
                        currentScreen = AppScreen.CUSTOMER_FORM
                    },
                    onCustomerDetail = {
                        selectedCustomerForDetail = it
                        currentScreen = AppScreen.CUSTOMER_DETAIL
                    }
                )

                AppScreen.CUSTOMER_DETAIL -> {
                    selectedCustomerForDetail?.let { cust ->
                        CustomerDetailScreen(
                            customer = cust,
                            viewModel = viewModel,
                            onNavigateBack = { currentScreen = AppScreen.CUSTOMERS },
                            onEditCustomer = {
                                customerToEdit = it
                                currentScreen = AppScreen.CUSTOMER_FORM
                            }
                        )
                    } ?: run {
                        currentScreen = AppScreen.CUSTOMERS
                    }
                }

                AppScreen.CUSTOMER_FORM -> CustomerFormScreen(
                    viewModel = viewModel,
                    existingCustomer = customerToEdit,
                    onNavigateBack = {
                        if (selectedCustomerForDetail != null) {
                            currentScreen = AppScreen.CUSTOMER_DETAIL
                        } else {
                            currentScreen = AppScreen.CUSTOMERS
                        }
                    }
                )

                AppScreen.FEASIBILITY -> FeasibilityScreen(viewModel = viewModel)

                AppScreen.TNF_REGISTRATION -> TnfAndRegistrationScreen(viewModel = viewModel)

                AppScreen.GAS_IN -> GasInTrackingScreen(viewModel = viewModel)

                AppScreen.VISIT_TRACKER -> DailyVisitTrackerScreen(viewModel = viewModel)

                AppScreen.DPR_EDITOR -> DprEditorScreen(viewModel = viewModel)

                AppScreen.REPORTS -> ReportsAndAnalyticsScreen(viewModel = viewModel)

                AppScreen.SETTINGS -> SettingsAndProfileScreen(
                    viewModel = viewModel,
                    onLogout = { /* triggers login screen */ }
                )
            }
        }
    }
}

private data class NavigationItem(
    val label: String,
    val icon: ImageVector,
    val screen: AppScreen
)
