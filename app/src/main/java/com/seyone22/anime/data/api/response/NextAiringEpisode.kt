package com.seyone22.anime.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class NextAiringEpisode (
    val episode: Int,
    val timeUntilAiring: Int
)