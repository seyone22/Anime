package com.seyone22.anime.data.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import kotlinx.serialization.json.Json
import retrofit2.Retrofit

object ApiClient {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://graphql.anilist.co")
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()

    val api = retrofit.create(GraphQLApi::class.java)
}