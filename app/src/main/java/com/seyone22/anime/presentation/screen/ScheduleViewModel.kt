package com.seyone22.anime.presentation.screen

import UserPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.seyone22.anime.data.GraphQLRequest
import com.seyone22.anime.data.api.GraphQLApi
import com.seyone22.anime.data.api.response.MediaList
import com.seyone22.anime.data.api.response.ResponseData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class ScheduleViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://graphql.anilist.co")
        .client(client) // Use the custom client with logging
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val api = retrofit.create(GraphQLApi::class.java)

    // LiveData to hold the list of schedules
    private val _mediaList = MutableStateFlow<List<MediaList>>(emptyList())
    val mediaList = _mediaList.asStateFlow()


    // Function to fetch the list of media based on the stored user ID
    fun fetchWatchingMedia() {
        viewModelScope.launch {
            try {
                // Get the user ID from DataStore
                val userId = userPreferences.userName.first()

                if (!userId.isNullOrEmpty()) {
                    // Proceed to fetch media using the retrieved user ID
                    val query = """
                        query (${"$"}page: Int) {
                          Page (page: ${"$"}page, perPage: 50) {
                            mediaList(userId: $userId, status: CURRENT, type: ANIME, sort: UPDATED_TIME_DESC) {
                                mediaId
                                progress
                                media {
                                    title {
                                        romaji
                                    }
                                    episodes
                                    bannerImage
                                    nextAiringEpisode {
                                        episode
                                        timeUntilAiring
                                    }
                                }
                            }
                          }
                        }
                    """.trimIndent()

                    val variables = mapOf("page" to 1)
                    val request = GraphQLRequest(query, variables)

                    api.getMediaIds(request).enqueue(object : Callback<ResponseData> {
                        override fun onResponse(
                            call: Call<ResponseData>,
                            response: Response<ResponseData>
                        ) {
                            if (response.isSuccessful) {
                                val mediaIds = response.body()?.data?.Page?.mediaList ?: emptyList()
                                _mediaList.value = mediaIds
                            } else {
                                Log.e("ScheduleViewModel", "Error fetching media: ${response.errorBody()?.string()}")
                            }
                        }

                        override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                            Log.e("ScheduleViewModel", "Error fetching media: $t")
                        }
                    })
                } else {
                    Log.e("ScheduleViewModel", "User ID is null or empty")
                }
            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "Error fetching user ID: $e")
            }
        }
    }
}