package fi.muni.android.habyte.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import fi.muni.android.habyte.util.NotificationHelper

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("id", 0)
        val name = intent.getStringExtra("name").orEmpty()
        NotificationHelper.createNotificationForHabit(context, notificationId, name)
    }
}