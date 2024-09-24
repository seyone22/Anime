package com.seyone22.anime.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class AiringSchedule(
    val timeUntilAiring: Long,
    val airingAt: Long,
    val episode: Int,
    val media: Media
)
