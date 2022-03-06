package com.psg.programmersflo.data.di

import com.psg.programmersflo.data.api.MusicAPI
import com.psg.programmersflo.data.repository.AppRepository
import com.psg.programmersflo.view.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single {
        Retrofit.Builder()
            .baseUrl("https://grepp-programmers-challenges.s3.ap-northeast-2.amazonaws.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MusicAPI::class.java)
    }
}

val viewModelModule = module {
    viewModel { MainViewModel(get()) }
}

val repositoryModule = module {
    single { AppRepository(get()) }
}