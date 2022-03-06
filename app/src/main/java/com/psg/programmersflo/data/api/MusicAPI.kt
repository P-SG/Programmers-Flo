package com.psg.programmersflo.data.api

import com.psg.programmersflo.data.model.Music
import retrofit2.Response
import retrofit2.http.GET

interface MusicAPI {

    @GET("/2020-flo/song.json")
    suspend fun getMusic(): Response<Music>

}