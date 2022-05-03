package fi.muni.android.habyte

import android.app.Application
import fi.muni.android.habyte.database.HabyteRoomDatabase

class HabyteApplication : Application() {
    val db by lazy { HabyteRoomDatabase.getDatabase(this) }
}