package com.seyone22.anime.presentation.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material3.CardDefaults
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.CircularProgressIndicatorDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TitleCard
import coil.compose.rememberAsyncImagePainter
import com.seyone22.anime.convertSeconds
import com.seyone22.anime.presentation.MainViewModel

@Composable
fun ScheduleScreen(
    mainViewModel: MainViewModel,
    viewModel: ScheduleViewModel = ScheduleViewModel()
) {
    // Call the fetch function
    LaunchedEffect(Unit) {
        viewModel.fetchWatchingMedia()
    }
    // Observe schedules LiveData
    val schedules by viewModel.mediaList.collectAsState(emptyList())


    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Display the schedules
        if (schedules.isNotEmpty()) {
            item {
                Text(text = "Watching", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 12.dp))
            }

            schedules.forEach { media ->
                item {
                    TitleCard(
                        modifier = Modifier.fillMaxSize(),
                        onClick = { /*TODO*/ },
                        title = {
                            Text(
                                text = media.media?.title?.romaji ?: "NO TITLE",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        subtitle = {
                            if (media.media?.nextAiringEpisode != null) {
                                val stringval = convertSeconds(media.media.nextAiringEpisode.timeUntilAiring)
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
        } else {
            item {
                Row(verticalAlignment = Alignment.CenterVertically){
                    CircularProgressIndicator(modifier = Modifier.padding(
                        CircularProgressIndicatorDefaults.FullScreenPadding))
                    Text(text = "Loading...", modifier = Modifier.padding(4.dp, 0.dp))
                }
            }
        }
    }
}
