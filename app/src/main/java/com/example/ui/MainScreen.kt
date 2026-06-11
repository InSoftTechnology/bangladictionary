package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ads.BannerAd
import com.example.ads.FullScreenAdShowcase

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainScreen(
    viewModel: DictionaryViewModel,
    modifier: Modifier = Modifier
) {
    var activeTabIdx by remember { mutableStateOf(0) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("app_main_scaffold"),
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Persistent Admob Banner Ad at the absolute bottom
                BannerAd(modifier = Modifier.fillMaxWidth())

                // Navigation Bar for switching states
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bottom_nav_bar"),
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = activeTabIdx == 0,
                        onClick = {
                            activeTabIdx = 0
                            viewModel.clearSelectedWord()
                        },
                        icon = { Icon(Icons.Default.Translate, contentDescription = "Search & Translate") },
                        label = { Text("Search") },
                        modifier = Modifier.testTag("nav_search_tab")
                    )

                    NavigationBarItem(
                        selected = activeTabIdx == 1,
                        onClick = {
                            activeTabIdx = 1
                            viewModel.clearSelectedWord()
                        },
                        icon = { Icon(Icons.Default.MenuBook, contentDescription = "A-Z Index Explore") },
                        label = { Text("A-Z Index") },
                        modifier = Modifier.testTag("nav_explore_tab")
                    )

                    NavigationBarItem(
                        selected = activeTabIdx == 2,
                        onClick = {
                            activeTabIdx = 2
                            viewModel.clearSelectedWord()
                        },
                        icon = { Icon(Icons.Default.Favorite, contentDescription = "Bookmarks & history Log") },
                        label = { Text("Saved") },
                        modifier = Modifier.testTag("nav_bookmarks_tab")
                    )

                    NavigationBarItem(
                        selected = activeTabIdx == 3,
                        onClick = {
                            activeTabIdx = 3
                            viewModel.clearSelectedWord()
                        },
                        icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Local Admin Panel") },
                        label = { Text("Admin") },
                        modifier = Modifier.testTag("nav_admin_tab")
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing // Properly respect camera notch status bars
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen contents
            when (activeTabIdx) {
                0 -> SearchScreen(viewModel = viewModel)
                1 -> ExploreScreen(viewModel = viewModel)
                2 -> HistoryBookmarksScreen(viewModel = viewModel)
                3 -> AdminScreen(viewModel = viewModel)
            }

            // Visual overlay for simulated interstitial or video popup advertisements
            FullScreenAdShowcase()
        }
    }
}
