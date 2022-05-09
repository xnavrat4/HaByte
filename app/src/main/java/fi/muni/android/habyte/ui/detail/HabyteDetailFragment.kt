package fi.muni.android.habyte.ui.detail

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import fi.muni.android.habyte.AddOrUpdateHabyteActivity
import fi.muni.android.habyte.HabyteApplication
import fi.muni.android.habyte.MainActivity
import fi.muni.android.habyte.databinding.FragmentHabitListBinding
import fi.muni.android.habyte.databinding.FragmentHabyteDetailBinding
import fi.muni.android.habyte.model.Habit
import fi.muni.android.habyte.ui.list.habit.HabitAdapter
import fi.muni.android.habyte.ui.list.habit.HabitListViewModel
import fi.muni.android.habyte.ui.list.habit.HabitListViewModelFactory
import fi.muni.android.habyte.util.NotificationHelper
import fi.muni.android.habyte.util.progressAsString
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime


class HabyteDetailFragment : Fragment() {

    private lateinit var binding: FragmentHabyteDetailBinding

    private lateinit var viewModel: HabyteDetailViewModel

    private lateinit var habitViewModel: HabitListViewModel

    var CALLENDAR_PERMISSION_CODE = 0

    var habyteID = "";


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHabyteDetailBinding.inflate(
            LayoutInflater.from(context), container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val habyteId = HabyteDetailFragmentArgs.fromBundle(requireArguments()).id
        val db = (activity?.application as HabyteApplication).db
        this.viewModel = ViewModelProvider(
            this,
            HabyteDetailViewModelFactory(habyteId.toInt(), db.habyteDao(), db.habitDao())
        ).get(HabyteDetailViewModel::class.java)
        habyteID = habyteId;
        viewModel.observeHabyte().observe(viewLifecycleOwner) {
            it?.let {
                binding.habitName.text = it.name
                binding.startDateText.text = it.startDate.toString()
                binding.endDateText.text = it.endDate.toString()
                binding.bar.max = it.habitsToDo
                binding.bar.progress = it.habitsFinished
                binding.progressLabel.text = it.habitsFinished.progressAsString(it.habitsToDo)
            }
        }



        binding.exportButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    context as Activity,
                    Manifest.permission.WRITE_CALENDAR
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                createCalendarEvents();
            } else {
                requestCalendarPermission();
            }
        }
        binding.deleteButton.setOnClickListener {
            viewModel.deleteHabyte()
            NotificationHelper.scheduleNotificationsForToday(requireContext())
            findNavController().navigateUp()
        }

        binding.editButton.setOnClickListener {
            val int = Intent(requireContext(), AddOrUpdateHabyteActivity::class.java)
            int.putExtra("habyteId", habyteId.toInt())
            startActivity(int)
        }



        binding.calendar.setOnDateChangeListener { calendarView, year, month, day ->
            val db = (activity?.application as HabyteApplication).db
            this.habitViewModel = ViewModelProvider(this,
                HabitListViewModelFactory(db.habitDao(), db.habyteDao(), habyteID.toInt(), LocalDateTime.of(year,month+1, day,0,0)))
                .get(HabitListViewModel::class.java)
            val adapter = HabitAdapter(habitViewModel::confirmHabit)
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.adapter = adapter

            habitViewModel.getHabitesForDay().observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }

        }
    }
         private fun createCalendarEvents() {

             val habitviewModel: HabitListViewModel by viewModels {
                val db = (activity?.application as HabyteApplication).db
                HabitListViewModelFactory(db.habitDao(), db.habyteDao())
            }

            val data = runBlocking { habitviewModel.getHabitsOfHabyte(habyteID.toInt()); }

            data.forEach { i ->
                val cal: Calendar = Calendar.getInstance()
                val intent = Intent(Intent.ACTION_EDIT)
                intent.type = "vnd.android.cursor.item/event"
                intent.putExtra("beginTime", i.start)
                intent.putExtra("time", true)
                intent.putExtra("rule", "FREQ=YEARLY")
                var endTime = i.start.plusHours(1)
                intent.putExtra("endTime", endTime)
                intent.putExtra("title", binding.habitName.text.toString())
                startActivity(intent)
            }
        }

        private fun requestCalendarPermission() {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity as MainActivity,
                    Manifest.permission.WRITE_CALENDAR
                )
            ) {
                AlertDialog.Builder(activity)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to create calendar events")
                    .setPositiveButton("ok",
                        DialogInterface.OnClickListener { dialog, which ->

                            ActivityCompat.requestPermissions(
                                activity as MainActivity, arrayOf(
                                    Manifest.permission.WRITE_CALENDAR
                                ), CALLENDAR_PERMISSION_CODE
                            )
                        })
                    .setNegativeButton("cancel",
                        DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                    .create().show()
            } else {
                ActivityCompat.requestPermissions(
                    activity as MainActivity,
                    arrayOf(Manifest.permission.WRITE_CALENDAR),
                    CALLENDAR_PERMISSION_CODE
                )
            }
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String?>,
            grantResults: IntArray
        ) {
            if (requestCode == CALLENDAR_PERMISSION_CODE) {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createCalendarEvents()
                }
            }
        }
    }