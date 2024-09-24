package com.seyone22.anime.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class Page(
    val pageInfo: PageInfo? = null,
    val airingSchedules: List<AiringSchedule>? = null,
    val mediaList: List<MediaList>? = null
)