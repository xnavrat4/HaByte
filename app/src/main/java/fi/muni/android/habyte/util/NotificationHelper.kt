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
import java.io.File
import java.time.LocalDate

object NotificationHelper {

    private const val channelId = "HabyteChannel"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "HabyteChannel"
            val descriptionText = "description for our channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

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

    fun scheduleNotificationsForToday(context: Context) = runBlocking {
        val savedIntentsFileName = "notifIntents"
        val savedIntentsFile = File(context.filesDir, savedIntentsFileName)
        val savedIds = savedIntentsFile.readText()

        launch {
            try {
                val habits = HabyteRoomDatabase.getDatabase(context).habitDao().findHabitsOnDate(
                    LocalDate.now(), false).first()

                if (savedIds != habits.map { h -> h.id }.toString()) {
                    Toast.makeText(context, "Creating notifications for today", Toast.LENGTH_SHORT).show()
                    for (habit in habits) {
                        println(habit.name)
                        scheduleAlarmForHabit(context, habit)
                    }
                    savedIntentsFile.writeText(habits.map { h -> h.id }.toString())
                }
            } catch (e: NoSuchElementException) {
                Toast.makeText(context, "Error retrieving data from database",
                    Toast.LENGTH_SHORT).show();
            }
        }
    }

    fun updateNotificationsSchedulesForToday(
        context: Context,
        habitsToSchedule: List<Habit>,
        habitsToUnschedule: List<Int>?)
    {
        Toast.makeText(context, "Updating notifications", Toast.LENGTH_SHORT).show()

        if (habitsToUnschedule != null) {
            for (habitId in habitsToUnschedule) {
                println(habitId)
                cancelAlarm(context, habitId)
            }
        }

        for (habit in habitsToSchedule) {
            scheduleAlarmForHabit(context, habit)
        }
    }

    fun createNotificationForHabit(context: Context, notificationId: Int, habitName: String) {
        val mainActivityIntent = Intent(context, MainActivity::class.java)

        val mainActivityPendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(mainActivityIntent)
            getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

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

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}