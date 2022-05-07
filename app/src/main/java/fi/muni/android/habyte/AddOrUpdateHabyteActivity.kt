package fi.muni.android.habyte

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import ca.antonious.materialdaypicker.DefaultSelectionMode
import ca.antonious.materialdaypicker.MaterialDayPicker
import ca.antonious.materialdaypicker.SingleSelectionMode
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import fi.muni.android.habyte.database.typeconverters.Converters
import fi.muni.android.habyte.databinding.ActivityAddOrUpdateHabyteBinding
import fi.muni.android.habyte.util.toDOW
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.TemporalAdjusters
import java.util.*

class AddOrUpdateHabyteActivity : AppCompatActivity() {

    private lateinit var datePicker: MaterialDatePicker<Long>

    private lateinit var timePicker: MaterialTimePicker

    private lateinit var viewModel: AddOrUpdateHabyteViewModel

    private lateinit var binding: ActivityAddOrUpdateHabyteBinding

    private val converters: Converters by lazy {
        Converters()
    }

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

        val items = listOf("Daily", "Weekly", "Custom")
        val adapter = ArrayAdapter(this, R.layout.enum_list_item, items)

        binding.recurrenceEnumTextview.setAdapter(adapter)

        binding.endDateText.inputType = InputType.TYPE_NULL
        binding.recurrenceStartTimeText.inputType = InputType.TYPE_NULL

        val dateConstraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
            .build()

        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select end date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(dateConstraints)
            .build()

        datePicker.addOnPositiveButtonClickListener {
            val formattedDate = timestampToFormattedTime(it)
            binding.endDateText.setText(formattedDate)
            validateForm()
        }

        binding.habyteNameLayout.editText?.doOnTextChanged { _, _, _, _ -> validateForm() }

        timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Routing start")
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
            validateForm()
        }

        binding.endDateText.setOnClickListener {
            datePicker.show(supportFragmentManager, "end-date")
            validateForm()
        }

        binding.recurrenceStartTimeText.setOnClickListener {
            timePicker.show(supportFragmentManager, "start-time")
            validateForm()
        }

        if (viewModel.supportsUpdate()) {
            binding.submitHabyteButton.text = getString(R.string.update_habyte_button_title)
        } else {
            binding.submitHabyteButton.text = getString(R.string.add_habyte_button_title)
        }


        binding.submitHabyteButton.setOnClickListener {
            val title = binding.habyteNameText.text.toString()
            val description = binding.habyteDescriptionText.text.toString()
            val startTime = binding.recurrenceStartTimeText.text.toString()
            val selectedDays = binding.weekDaySelection.selectedDays.toSet()
            val endDate = binding.endDateText.text.toString()

            //PARSE dates/times
            val startLocalTime = converters.ltFromString(startTime)
            val endDateLD: LocalDate =
                LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))

            if (viewModel.supportsUpdate()) {
                viewModel.updateHabyte(title, description, startLocalTime, endDateLD, selectedDays)
            } else {
                viewModel.createHabyte(title, description, startLocalTime, endDateLD, selectedDays)
            }
        }

        viewModel.queryResult.observe(this) { result ->
            result.onSuccess {
                if (viewModel.supportsUpdate()) {
                    Toast.makeText(applicationContext, "Habyte updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Habyte saved", Toast.LENGTH_SHORT).show()
                }
                finish()
            }
            result.onFailure {
                Toast.makeText(applicationContext, "Error: ${it}}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.recurrenceEnumLabel.editText?.doOnTextChanged { enumText, _, _, _ ->
            when (enumText.toString()) {
                "Weekly" -> {
                    binding.weekDaySelection.clearSelection()
                    binding.weekDaySelection.selectionMode = SingleSelectionMode()
                    binding.weekDaySelection.visibility = View.VISIBLE
                }
                "Daily" -> {
                    binding.weekDaySelection.clearSelection()
                    binding.weekDaySelection.selectionMode = DefaultSelectionMode()
                    binding.weekDaySelection.visibility = View.GONE
                    binding.weekDaySelection.setSelectedDays(MaterialDayPicker.Weekday.allDays)
                }
                "Custom" -> {
                    binding.weekDaySelection.clearSelection()
                    binding.weekDaySelection.selectionMode = DefaultSelectionMode()
                    binding.weekDaySelection.visibility = View.VISIBLE
                }
            }
            validateForm()
        }

        binding.weekDaySelection.setDaySelectionChangedListener {
            validateForm()
        }

        if (viewModel.supportsUpdate()) {
            viewModel.habyte.observe(this) {
                binding.habyteNameText.setText(it.name)
                binding.habyteDescriptionText.setText(it.description)
                binding.endDateText.setText(it.endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                binding.recurrenceStartTimeText.setText(converters.stringFromLt(it.selectedTime))
                when (it.selectedDays.size) {
                    1 -> {
                        binding.recurrenceEnumTextview.setText("Weekly", false)
                    }
                    in 2..6 -> {
                        binding.recurrenceEnumTextview.setText("Custom", false)
                    }
                    7 -> {
                        binding.recurrenceEnumTextview.setText("Daily", false)
                    }
                }
                binding.weekDaySelection.setSelectedDays(it.selectedDays.toList())
            }
        }
    }

    private fun timestampToFormattedTime(it: Long): String? {
        val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.timeInMillis = it
        val format = SimpleDateFormat("dd/MM/yyyy")
        return format.format(calendar.time)
    }

    private fun validateForm(): Boolean {
        val isValid = validateName()
                && validateEndDate()
                && validateRecurrence()
                && validateStartTime()
                && validateDays()
        binding.submitHabyteButton.isEnabled = isValid
        return isValid
    }

    private fun validateDays(): Boolean {
        val selectedDays = binding.weekDaySelection.selectedDays.toSet()
        val endDate = binding.endDateText.text
        var isValid = true
        if (selectedDays.isEmpty()) {
            isValid = false
        } else {
            val now = LocalDate.now()
            if (endDate != null) {
                try {
                    val endDateLD = LocalDate.parse(
                        endDate.toString(),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    )
                    if (selectedDays.map { now.with(TemporalAdjusters.nextOrSame(it.toDOW())) }
                            .all { it > endDateLD }) {
                        // USER FILLED OUT WEEKDAYS THAT WOUlD ALL BE IN THE FUTURE
                        isValid = false
                    }

                } catch (e: DateTimeParseException) {
                    // already validated
                }
            }
        }
        return isValid
    }

    private fun validateStartTime(
    ): Boolean {
        val startTime = binding.recurrenceStartTimeText.text
        val endDate = binding.endDateText.text
        var isValid = true
        if (startTime == null || startTime.isEmpty()) {
            binding.recurrenceStartTimeLayout.error = getString(R.string.input_required)
            isValid = false
        } else {
            if (endDate != null) {
                try {
                    val startLT = converters.ltFromString(startTime.toString())
                    val endDateLD = LocalDate.parse(
                        endDate.toString(),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    )
                    if (endDateLD.atTime(startLT) < LocalDateTime.now()) {
                        binding.recurrenceStartTimeLayout.error =
                            "Has to be in future"
                        isValid = false
                    }
                } catch (e: DateTimeParseException) {
                }
            }
        }
        if (isValid) {
            binding.recurrenceStartTimeLayout.error = null
        }
        return isValid
    }

    private fun validateRecurrence(): Boolean {
        val recurrence = binding.recurrenceEnumTextview.text
        var isValid = true
        if (recurrence == null || recurrence.isEmpty()) {
            binding.recurrenceEnumLabel.error = getString(R.string.input_required)
            isValid = false
        }
        if (isValid) {
            binding.recurrenceEnumLabel.error = null
        }
        return isValid
    }

    private fun validateEndDate(): Boolean {
        val endDate = binding.endDateText.text
        var isValid = true
        if (endDate != null) {
            try {
                val endDateLD =
                    LocalDate.parse(endDate.toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                if (endDateLD < LocalDate.now()) {
                    binding.endDateLabel.error = getString(R.string.in_future_validation)
                    isValid = false
                }
            } catch (e: DateTimeParseException) {
                binding.endDateLabel.error = getString(R.string.input_required)
                isValid = false
            }
        } else {
            binding.endDateLabel.error = getString(R.string.input_required)
            isValid = false
        }
        if (isValid) {
            binding.endDateLabel.error = null
        }
        return isValid
    }

    private fun validateName(): Boolean {
        val title = binding.habyteNameText.text
        var isValid = true
        if (title != null && title.isEmpty()) {
            binding.habyteNameLayout.error = getString(R.string.input_required)
            isValid = false
        }
        if (isValid) {
            binding.habyteNameLayout.error = null
        }
        return isValid
    }

}