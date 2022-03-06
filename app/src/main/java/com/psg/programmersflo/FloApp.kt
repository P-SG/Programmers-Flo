package com.psg.programmersflo

import android.app.Application
import com.psg.programmersflo.data.di.appModule
import com.psg.programmersflo.data.di.repositoryModule
import com.psg.programmersflo.data.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class FloApp: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@FloApp)
            modules(listOf(appModule, viewModelModule, repositoryModule))
        }
    }
}
