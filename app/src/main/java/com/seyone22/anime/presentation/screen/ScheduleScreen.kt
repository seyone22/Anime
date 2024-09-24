package com.seyone22.anime.presentation.screen

import UserPreferences
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.CardDefaults
import androidx.wear.compose.material3.CompactButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TitleCard
import coil.compose.rememberAsyncImagePainter
import com.seyone22.anime.convertSeconds
import com.seyone22.anime.presentation.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun ScheduleScreen(
    navController: NavController, // NavController for navigation
    mainViewModel: MainViewModel, // MainViewModel passed from the activity
    userPreferences: UserPreferences // Pass userPreferences from the activity or parent composable
) {
    // Create the ScheduleViewModel, passing userPreferences
    val viewModel = remember {
        ScheduleViewModel(userPreferences)
    }

    // Observe schedules from the ViewModel
    val schedules by viewModel.mediaList.collectAsState(emptyList())
    var isLoading by remember { mutableStateOf(true) }

    // List state for ScalingLazyColumn
    val listState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Fetch the media when the screen is loaded
    LaunchedEffect(Unit) {
        try {
            viewModel.fetchWatchingMedia()
        } finally {
            isLoading = false
        }
    }

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        state = listState // Pass listState to ScalingLazyColumn
    ) {
        if (!isLoading || schedules.isNotEmpty()) {
            item {
                Text(
                    text = "Watching",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Display the media list
            schedules.forEach { media ->
                item {
                    TitleCard(
                        modifier = Modifier.fillMaxSize(),
                        onClick = { /* Handle click event */ },
                        title = {
                            Text(
                                text = media.media?.title?.romaji ?: "NO TITLE",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        subtitle = {
                            if (media.media?.nextAiringEpisode != null) {
                                val stringval =
                                    convertSeconds(media.media.nextAiringEpisode.timeUntilAiring)
                                Text(text = "${media.progress}/${media.media.episodes} • $stringval • Ep.${media.media.nextAiringEpisode.episode}")
                            } else {
                                Text(text = "${media.progress}/${media.media?.episodes} • Finished")
                            }
                        },
                        colors = CardDefaults.imageCardColors(
                            containerPainter = CardDefaults.imageWithScrimBackgroundPainter(
                                backgroundImagePainter = rememberAsyncImagePainter(
                                    model = media.media?.bannerImage,
                                    contentScale = ContentScale.Crop
                                )
                            ),
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            titleColor = MaterialTheme.colorScheme.onSurface
                        ),
                    )
                }
            }

            item {
                Row {
                    CompactButton(
                        modifier = Modifier.padding(end = 12.dp),
                        onClick = {
                            isLoading = true
                            coroutineScope.launch {
                                try {
                                    viewModel.fetchWatchingMedia()
                                } finally {
                                    isLoading = false
                                    listState.scrollToItem(0) // Scroll to top after reload
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Refresh, contentDescription = "Refresh"
                            )
                        },
                    )
                    CompactButton(
                        onClick = {
                            navController.navigate("settings")
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        },
                    )
                }
            }
        } else {
            // Show loading state while fetching
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Downloading, contentDescription = "Loading"
                    )
                    Text(text = "Loading...", modifier = Modifier.padding(4.dp, 0.dp))
                }
            }
        }
    }
}
