package com.seyone22.anime.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class GraphQLRequest(
    val query: String,
    val variables: Map<String, Int>
)