package fi.muni.android.habyte.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.util.*

@Entity(
    tableName = "habit",
    foreignKeys = [ForeignKey(
        entity = Habyte::class,
        parentColumns = ["id"],
        childColumns = ["habyteId"],
        onDelete = CASCADE
    )]
)
@Parcelize
data class Habit(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "start")
    val start: LocalDateTime,

    @ColumnInfo(name = "additionalText")
    val additionalText: String?,

    @ColumnInfo(name = "photoPath")
    val photoPath: String?,

    @ColumnInfo(name = "done", defaultValue = "false")
    val done: Boolean,

    @ColumnInfo(name = "habyteId")
    val habyteId: Int
) : Parcelable