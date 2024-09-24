package com.seyone22.anime.presentation

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.seyone22.anime.data.GraphQLRequest
import com.seyone22.anime.data.api.ApiClient.api
import com.seyone22.anime.data.api.response.MediaList
import com.seyone22.anime.data.api.response.ResponseData
import com.seyone22.anime.helpers.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(
    application: Application
) : AndroidViewModel(application),
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    private val context: Context = application.applicationContext
    private val notificationHelper = NotificationHelper(context)

    // LiveData to hold the media list
    val mediaList = MutableLiveData<List<MediaList>>()

    init {
        notificationHelper.createNotificationChannel()
    }

    override fun onDataChanged(dataEventBuffer: DataEventBuffer) {
        // Handle data changes
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        // Handle received messages
    }

    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        // Handle capability changes
    }

    // Fetch the list of currently watching anime
    fun fetchWatchingMedia() {
        val query = """
            query (${"$"}page: Int) {
              Page (page: ${"$"}page, perPage: 50) {
                mediaList(userId: 148802, status: CURRENT, type: ANIME, sort: UPDATED_TIME_DESC) {
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
            override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                if (response.isSuccessful) {
                    val mediaIds = response.body()?.data?.Page?.mediaList ?: emptyList()
                    mediaList.value = mediaIds
                    notifyUpcomingEpisodes(mediaIds)
                } else {
                    Log.e("MainViewModel", "Error fetching media IDs: ${Exception(response.errorBody()?.string())}")
                }
            }

            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                Log.e("MainViewModel", "Error fetching media IDs: $t")
            }
        })
    }

    // Notify about upcoming episodes for the fetched media
    private fun notifyUpcomingEpisodes(mediaItems: List<MediaList>) {
        CoroutineScope(Dispatchers.IO).launch {
            mediaItems.forEach { item ->
                item.media?.nextAiringEpisode?.let { airingEpisode ->
                    val timeUntilAiring = airingEpisode.timeUntilAiring
                    if (timeUntilAiring <= 60 * 60 * 48) { // 2 days or less
                        val animeTitle = item.media.title.romaji
                        withContext(Dispatchers.Main) {
                            notificationHelper.sendNotification(animeTitle, timeUntilAiring / 60) // Send notification in minutes
                        }
                    }
                }
            }
        }
    }
}