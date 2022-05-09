package fi.muni.android.habyte.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import fi.muni.android.habyte.R
import fi.muni.android.habyte.util.NotificationHelper
import java.time.LocalDate

class NotificationCreatorReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val intentPref = context.getSharedPreferences(
            context.getString(R.string.latest_intents_update_date), Context.MODE_PRIVATE)
        val latestDateOfIntentsUpdate = intentPref.getString(
            context.getString(R.string.latest_intents_update_date), "")

        if (latestDateOfIntentsUpdate != LocalDate.now().toString()) {
            NotificationHelper.scheduleNotificationsForToday(context)
        }
    }
}