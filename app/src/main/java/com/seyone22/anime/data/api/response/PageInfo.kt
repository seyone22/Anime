package com.seyone22.anime.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class PageInfo(
    val total: Int,
    val currentPage: Int,
    val lastPage: Int,
    val hasNextPage: Boolean,
    val perPage: Int
)