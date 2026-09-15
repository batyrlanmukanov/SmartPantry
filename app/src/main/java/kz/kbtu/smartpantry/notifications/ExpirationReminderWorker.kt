package kz.kbtu.smartpantry.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.first
import kz.kbtu.smartpantry.core.data.PreferenceKeys
import kz.kbtu.smartpantry.core.data.smartPantryDataStore
import kz.kbtu.smartpantry.core.database.SmartPantryDatabase
import kz.kbtu.smartpantry.core.model.AppLanguage

class ExpirationReminderWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        val preferences = applicationContext.smartPantryDataStore.data.first()
        val language = preferences[PreferenceKeys.appLanguage]
            ?.let(AppLanguage::valueOf)
            ?: AppLanguage.ENGLISH
        val reminderHour = (preferences[PreferenceKeys.reminderHour] ?: 19).coerceIn(0, 23)

        if (LocalTime.now().hour != reminderHour) {
            return Result.success()
        }

        val database = Room.databaseBuilder(
            applicationContext,
            SmartPantryDatabase::class.java,
            "smart_pantry.db",
        ).build()

        val items = database.foodDao().observeAll().first()
        val today = LocalDate.now().toEpochDay()
        val expiringSoon = items.filter {
            it.status == "ACTIVE" && (it.expirationEpochDay - today) in 0..2
        }

        if (expiringSoon.isNotEmpty()) {
            val manager = applicationContext
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            createChannel(manager, language)

            val title = if (language == AppLanguage.RUSSIAN) {
                "Срок годности истекает через 1-2 дня"
            } else {
                "Items expire in 1-2 days"
            }

            val body = if (language == AppLanguage.RUSSIAN) {
                "Проверь продукты: ${expiringSoon.take(3).joinToString { it.name }}"
            } else {
                "Check these items: ${expiringSoon.take(3).joinToString { it.name }}"
            }

            val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            manager.notify(1001, notification)
        }

        database.close()
        return Result.success()
    }

    private fun createChannel(
        manager: NotificationManager,
        language: AppLanguage,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                if (language == AppLanguage.RUSSIAN) {
                    "Напоминания о продуктах"
                } else {
                    "Food reminders"
                },
                NotificationManager.IMPORTANCE_HIGH,
            )
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "smart_pantry_expiration"
        private const val UNIQUE_WORK_NAME = "smart_pantry_expiration_reminders"
        private const val TEST_WORK_NAME = "smart_pantry_expiration_reminders_test"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<ExpirationReminderWorker>(
                1,
                TimeUnit.HOURS,
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request,
            )
        }

        fun runTestNow(context: Context) {
            val request = OneTimeWorkRequestBuilder<ExpirationReminderWorker>().build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                TEST_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request,
            )
        }
    }
}
