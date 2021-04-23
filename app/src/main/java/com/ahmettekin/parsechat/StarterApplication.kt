package com.ahmettekin.parsechat

import android.app.Application
import com.parse.Parse

class StarterApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Parse.setLogLevel(Parse.LOG_LEVEL_ERROR)

        Parse.initialize(Parse.Configuration.Builder(this)
            .applicationId("rwQqatmbqD6dvhi1yn5AkL3vAkNusqMmmKsyt9d9")
            .clientKey("8fm9tP72T9q1IP3WwcuTZYC4t0zDnGCJITlZ6JQH")
            .server("https://parseapi.back4app.com/")
            .build()
        )
    }
}