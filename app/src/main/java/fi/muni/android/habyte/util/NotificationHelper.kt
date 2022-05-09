package fi.muni.android.habyte.util

import android.app.*
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import fi.muni.android.habyte.MainActivity
import fi.muni.android.habyte.R
import fi.muni.android.habyte.database.HabyteRoomDatabase
import fi.muni.android.habyte.model.Habit
import fi.muni.android.habyte.receiver.NotificationCreatorReceiver
import fi.muni.android.habyte.receiver.NotificationReceiver
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

object NotificationHelper {

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = context.getString(R.string.notification_channel)
            val name = "Habyte Channel"
            val descriptionText = "Habyte notification channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // sets up an alarm for every midnight, then calls scheduleNotificationsForToday
    fun setupDailyNotificationCreation(context: Context) {
        val dailyIntent = Intent(context, NotificationCreatorReceiver::class.java)

        val dailyPendingIntent = PendingIntent.getBroadcast(context, 0, dailyIntent,
            PendingIntent.FLAG_IMMUTABLE)

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            dailyPendingIntent
        )
    }

    // schedules notifications for today
    fun scheduleNotificationsForToday(context: Context) = runBlocking {
        val intentPref = context.getSharedPreferences(
            context.getString(R.string.latest_intents_update_date), Context.MODE_PRIVATE)

        launch {
            try {
                val habitsForToday = HabyteRoomDatabase.getDatabase(context).habitDao()
                    .findHabitsOnDate(LocalDate.now(), false)
                    .first()

                // get ids of intents scheduled for today
                val scheduledString = intentPref.getString(context.getString(R.string.saved_intents), "")!!

                // habits for today did not change
                if (scheduledString == habitsForToday.toIdsString()) {
                    return@launch
                }

                if (scheduledString.isNotEmpty()) {
                    // cancel all scheduled intends
                    val scheduledIntentIds = scheduledString.split(",").map { s -> s.toInt() }
                    scheduledIntentIds
                        .map { cancelAlarm(context, it) }
                }

                for (habit in habitsForToday) {
                    scheduleAlarmForHabit(context, habit)
                }

                with(intentPref.edit()) {
                    putString(context.getString(R.string.saved_intents), habitsForToday.toIdsString())
                    putString(
                        context.getString(R.string.latest_intents_update_date),
                        LocalDate.now().toString()
                    )
                    apply()
                }

                Toast.makeText(context, "Notifications updated", Toast.LENGTH_SHORT).show()
            } catch (e: NoSuchElementException) {
                Toast.makeText(context, "Error retrieving data from database",
                    Toast.LENGTH_SHORT).show();
            }
        }
    }

    fun createNotificationForHabit(context: Context, notificationId: Int, habitName: String) {
        val mainActivityIntent = Intent(context, MainActivity::class.java)

        val mainActivityPendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(mainActivityIntent)
            getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val channelId = context.getString(R.string.notification_channel)
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.h_box)
            .setContentTitle("Habyte: $habitName")
            .setContentText("Don't forget your habit for today!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(mainActivityPendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

    private fun scheduleAlarmForHabit(context: Context, habit : Habit) {
        val alarmIntent = Intent(context, NotificationReceiver::class.java)
        alarmIntent.putExtra("name", habit.name)
        alarmIntent.putExtra("id", habit.id)

        val pendingIntent = PendingIntent.getBroadcast(context, habit.id, alarmIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, habit.start.hour)
            set(Calendar.MINUTE, habit.start.minute)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    private fun cancelAlarm(context: Context, id: Int) {
        val alarmIntent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, id, alarmIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE)

        if (pendingIntent != null) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        }
    }
}