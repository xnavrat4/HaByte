package fi.muni.android.habyte

import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import fi.muni.android.habyte.databinding.ActivityAddOrUpdateHabyteBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class AddOrUpdateHabyteActivity : AppCompatActivity() {

    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var timePicker: MaterialTimePicker

    private lateinit var viewModel: AddOrUpdateHabyteViewModel

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityAddOrUpdateHabyteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val habyteToUpdate = intent.getIntExtra("habyteId", -1)

        binding = ActivityAddOrUpdateHabyteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val db = (application as HabyteApplication).db
        this.viewModel = ViewModelProvider(
            this,
            AddOrUpdateHabyteViewModelFactory(db.habyteDao(), db.habitDao(), habyteToUpdate)
        ).get(AddOrUpdateHabyteViewModel::class.java)
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
            val formattedDate = timestampToFormattedTime(it)
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

        if (viewModel.supportsUpdate()) {
            binding.submitHabyteButton.text = "Update Habyte"
        } else {
            binding.submitHabyteButton.text = "Add Habyte"
        }

        binding.submitHabyteButton.setOnClickListener {
            val title = binding.habyteNameText.text.toString()
            val description = binding.habyteDescriptionText.text.toString()
            val startTime = binding.recurrenceStartTimeText.text.toString()
            val recurrence = binding.recurrenceEnumTextview.text.toString()
            val endDateTime = binding.endDateText.text.toString()
            if (viewModel.supportsUpdate()) {
                viewModel.updateHabyte(title, description, startTime, recurrence, endDateTime)
            } else {
                viewModel.createHabyte(title, description, startTime, recurrence, endDateTime)
            }
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

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        if (viewModel.supportsUpdate()) {
            viewModel.habyte.observe(this) {
                binding.habyteNameText.setText(it.name)
                binding.habyteDescriptionText.setText(it.description)
                binding.endDateText.setText(it.endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
            }
        }
    }

    private fun timestampToFormattedTime(it: Long): String? {
        val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.timeInMillis = it
        val format = SimpleDateFormat("dd/MM/yyyy")
        val formattedDate = format.format(calendar.time)
        return formattedDate
    }


}