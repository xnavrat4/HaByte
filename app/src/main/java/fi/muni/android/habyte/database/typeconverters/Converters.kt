package fi.muni.android.habyte.database.typeconverters

import androidx.room.TypeConverter
import java.time.*

class Converters {
    @TypeConverter
    fun ldFromTimestamp(value: Long?): LocalDate? {
        return value?.let {
            LocalDateTime.ofInstant(
                Instant.ofEpochSecond(it),
                ZoneId.systemDefault()
            ).toLocalDate()
        }
    }
    @TypeConverter
    fun ldtFromTimestamp(value: Long?): LocalDateTime? {
        return value?.let {
            LocalDateTime.ofInstant(Instant.ofEpochSecond(it), ZoneId.systemDefault())
        }
    }

    @TypeConverter
    fun timestampFromLdt(value: LocalDateTime?):Long? {
        return value?.atZone(ZoneId.systemDefault())?.toEpochSecond()
    }

    @TypeConverter
    fun timestampFromLdt(value: LocalDate?):Long? {
        return value?.atStartOfDay()?.atZone(ZoneId.systemDefault())?.toEpochSecond()
    }
}