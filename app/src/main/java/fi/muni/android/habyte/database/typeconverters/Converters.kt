package fi.muni.android.habyte.database.typeconverters

import androidx.room.TypeConverter
import ca.antonious.materialdaypicker.MaterialDayPicker
import java.text.DecimalFormat
import java.time.*
import java.time.format.DateTimeFormatter

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
    fun timestampFromLdt(value: LocalDateTime):Long {
        return value.atZone(ZoneId.systemDefault()).toEpochSecond()
    }

    @TypeConverter
    fun timestampFromLdt(value: LocalDate):Long? {
        return value.atStartOfDay()?.atZone(ZoneId.systemDefault())?.toEpochSecond()
    }

    @TypeConverter
    fun stringFromLt(value: LocalTime):String {
        val f = DecimalFormat("00")
        return "${f.format(value.hour)}:${f.format(value.minute)}"
    }

    @TypeConverter
    fun ltFromString(value: String): LocalTime {
        return LocalTime.parse(value, DateTimeFormatter.ISO_LOCAL_TIME)
    }

    @TypeConverter
    fun weekdayFromString(value: String?): Set<MaterialDayPicker.Weekday>? {
        return value?.split(',')?.map { MaterialDayPicker.Weekday.valueOf(it) }?.toSet()
    }

    @TypeConverter
    fun stringFromWeekday(value: Set<MaterialDayPicker.Weekday>?): String? {
        return value?.let {
            value.map { it.toString() }.reduce{first, second -> "$first,$second"}
        }
    }
}