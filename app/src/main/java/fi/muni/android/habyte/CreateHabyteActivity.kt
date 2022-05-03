package fi.muni.android.habyte

import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import fi.muni.android.habyte.databinding.ActivityCreateHabyteBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class CreateHabyteActivity : AppCompatActivity() {

    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var timePicker: MaterialTimePicker

    private val viewModel: CreateActivityViewModel by viewModels {
        val db = (application as HabyteApplication).db
        CreateActivityViewModelFactory(db.habyteDao(), db.habitDao())
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityCreateHabyteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateHabyteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val items = listOf("Daily", "Weekly")
        val adapter = ArrayAdapter(this, R.layout.enum_list_item, items)
        binding.recurrenceEnumTextview.setAdapter(adapter)
        binding.endDateText.inputType = InputType.TYPE_NULL
        binding.recurrenceStartTimeText.inputType = InputType.TYPE_NULL

        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select end date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener {
            val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault())
            calendar.timeInMillis = it
            val format = SimpleDateFormat("dd/MM/yyyy")
            val formattedDate = format.format(calendar.time)
            binding.endDateText.setText(formattedDate)
        }

        timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select habit start time")
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val f = DecimalFormat("00")
            binding.recurrenceStartTimeText.setText(
                "${f.format(timePicker.hour)}:${
                    f.format(
                        timePicker.minute
                    )
                }"
            )
        }

        binding.endDateText.setOnClickListener {
            datePicker.show(supportFragmentManager, "end-date")
        }

        binding.recurrenceStartTimeText.setOnClickListener {
            timePicker.show(supportFragmentManager, "start-time")
        }

        binding.addHabyteButton.setOnClickListener {
            val title = binding.habyteNameText.text.toString()
            val description = binding.habyteDescriptionText.text.toString()
            val startTime = binding.recurrenceStartTimeText.text.toString()
            val recurrence = binding.recurrenceEnumTextview.text.toString()
            val endDateTime = binding.endDateText.text.toString()
            viewModel.populateHabyte(title, description, startTime, recurrence, endDateTime)
        }

        viewModel.queryResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(applicationContext, "Habyte saved", Toast.LENGTH_SHORT).show()
                finish()
            }
            result.onFailure {
                Toast.makeText(applicationContext, "Error: ${it}}", Toast.LENGTH_SHORT).show()
            }
        }

    }


}