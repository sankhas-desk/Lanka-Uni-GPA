package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: GpaViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val gpaStats by viewModel.gpaStatistics.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(1) } // Default to GPA calculator (Tab index 1)

    // Handle edge-to-edge full bleed
    val navigationBarHeight = WindowInsets.navigationBars

    if (userProfile == null) {
        // Welcome & Onboarding view
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Ayubowan! 🇱🇰",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        ) { innerPadding ->
            UserProfileScreen(
                userProfile = null,
                onSaveProfile = { name, dp, uni, deg, scale, darkPref ->
                    viewModel.updateProfile(name, dp, uni, deg, scale, darkPref)
                },
                onSaveCustomPhoto = { uriStr ->
                    viewModel.saveCustomGpPhoto(uriStr)
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
    } else {
        // Main Dashboard
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SriLankanAvatar(
                                dpIndicator = userProfile?.dpIndicator ?: "avatar_lion",
                                size = 32,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Column {
                                Text(
                                    text = userProfile?.name ?: "Student",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Text(
                                    text = userProfile?.university?.replace(" (.*)".toRegex(), "") ?: "",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 0) Icons.Default.Person else Icons.Outlined.Person,
                                contentDescription = "Profile"
                            )
                        },
                        label = { Text("Profile") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_profile_tab")
                    )

                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 1) Icons.Default.Calculate else Icons.Outlined.Calculate,
                                contentDescription = "Calculator"
                            )
                        },
                        label = { Text("Calculator") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_calc_tab")
                    )

                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 2) Icons.Default.Analytics else Icons.Outlined.Analytics,
                                contentDescription = "Visualizer"
                            )
                        },
                        label = { Text("Analytics") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_analytics_tab")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    0 -> {
                        UserProfileScreen(
                            userProfile = userProfile,
                            onSaveProfile = { name, dp, uni, deg, scale, darkPref ->
                                viewModel.updateProfile(name, dp, uni, deg, scale, darkPref)
                            },
                            onSaveCustomPhoto = { uriStr ->
                                viewModel.saveCustomGpPhoto(uriStr)
                            }
                        )
                    }
                    1 -> {
                        GpaCalculatorScreen(
                            semesterWiseStats = gpaStats.semesterWiseStats,
                            onAddCourse = { sem, code, name, creds, grade ->
                                viewModel.addCourse(sem, code, name, creds, grade)
                            },
                            onUpdateCourse = { course ->
                                viewModel.updateCourse(course)
                            },
                            onDeleteCourse = { course ->
                                viewModel.deleteCourse(course)
                            },
                            onClearSemester = { sem ->
                                viewModel.clearSemester(sem)
                            }
                        )
                    }
                    2 -> {
                        CumulativeVisualizerScreen(
                            userProfile = userProfile,
                            gpaStats = gpaStats
                        )
                    }
                }
            }
        }
    }
}
