package com.seyone22.anime.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class Media(
    val title: MediaTitle,
    val episodes: Int = 0,
    val bannerImage: String = "",
    val nextAiringEpisode: NextAiringEpisode? = null
)