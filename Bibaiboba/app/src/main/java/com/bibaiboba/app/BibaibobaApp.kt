package com.bibaiboba.app

import android.app.Application
import com.bibaiboba.app.data.repository.ChatRepository
import com.bibaiboba.app.data.repository.SettingsRepository

class BibaibobaApp : Application() {
    lateinit var settingsRepository: SettingsRepository
        private set
    lateinit var chatRepository: ChatRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        settingsRepository = SettingsRepository(this)
        chatRepository = ChatRepository()
    }

    companion object {
        lateinit var instance: BibaibobaApp
            private set
    }
}
