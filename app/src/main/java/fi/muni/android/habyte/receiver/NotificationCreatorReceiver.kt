package fi.muni.android.habyte.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import fi.muni.android.habyte.util.NotificationHelper

class NotificationCreatorReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NotificationHelper.scheduleNotificationsForToday(context)
    }
}