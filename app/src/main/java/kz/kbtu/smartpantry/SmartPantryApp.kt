package kz.kbtu.smartpantry

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kz.kbtu.smartpantry.notifications.ExpirationReminderWorker

@HiltAndroidApp
class SmartPantryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ExpirationReminderWorker.schedule(this)
    }
}
