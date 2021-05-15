package com.ahmettekin.parsechat

import android.app.Application
import com.ahmettekin.parsechat.model.Message
import com.ahmettekin.parsechat.model.Room
import com.parse.Parse
import com.parse.ParseObject

class StarterApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Parse.setLogLevel(Parse.LOG_LEVEL_ERROR)
        Parse.initialize(Parse.Configuration.Builder(this)
            .applicationId("6WTfZ2hDtWDnfFZJMzrNNVhp0wUtMjHbPA17OBA9")
            .clientKey("kngySAkXQTWtq8YgxjSDHQPP3fbVZauesoy0qazP")
            .server("https://parsechat2.b4a.io/")
            .build()
        )
    }

}