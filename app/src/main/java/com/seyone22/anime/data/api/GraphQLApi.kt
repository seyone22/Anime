package com.seyone22.anime.data.api

import com.seyone22.anime.data.api.response.ResponseData
import com.seyone22.anime.data.GraphQLRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface GraphQLApi {
    @POST("/")
    fun getAiringSchedules(@Body request: GraphQLRequest): Call<ResponseData>
    @POST("/")
    fun getMediaIds(@Body request: GraphQLRequest): Call<ResponseData>
}