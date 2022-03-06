package com.psg.programmersflo.data.repository

import com.psg.programmersflo.data.api.MusicAPI
import com.psg.programmersflo.data.model.Music
import retrofit2.Response

class AppRepository constructor(private val api: MusicAPI) {

    suspend fun getMusic(): Response<Music> = api.getMusic()

}