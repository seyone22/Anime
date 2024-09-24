package com.seyone22.anime.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class MediaList(
    val progress: Int = 0,
    val mediaId: Int,
    val media: Media? = null
)
