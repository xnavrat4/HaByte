package fi.muni.android.habyte.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fi.muni.android.habyte.database.dao.HabitDao
import fi.muni.android.habyte.database.dao.HabyteDao
import fi.muni.android.habyte.database.typeconverters.Converters
import fi.muni.android.habyte.model.Habit
import fi.muni.android.habyte.model.Habyte

@Database(entities = [Habyte::class, Habit::class], version = 1)
@TypeConverters(Converters::class)
public abstract class HabyteRoomDatabase: RoomDatabase() {

    abstract fun habyteDao(): HabyteDao

    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile
        private var INSTANCE: HabyteRoomDatabase? = null

        fun getDatabase(context: Context): HabyteRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabyteRoomDatabase::class.java,
                    "habyte_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}