package fi.muni.android.habyte.ui.detail

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.icu.util.TimeZone
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import fi.muni.android.habyte.AddOrUpdateHabyteActivity
import fi.muni.android.habyte.HabyteApplication
import fi.muni.android.habyte.MainActivity
import fi.muni.android.habyte.databinding.FragmentHabyteDetailBinding
import fi.muni.android.habyte.util.progressAsString


class HabyteDetailFragment : Fragment() {

    private lateinit var binding: FragmentHabyteDetailBinding

    private lateinit var viewModel: HabyteDetailViewModel

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
            var permissionStatus = false;
            try {
                val info: PackageInfo = context?.getPackageManager()!!
                    .getPackageInfo(requireContext().packageName, PackageManager.GET_PERMISSIONS)
                if (info.requestedPermissions != null) {
                    for (p in info.requestedPermissions) {
                        if (p == "WRITE_CALENDAR") {
                            permissionStatus = true
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (ContextCompat.checkSelfPermission(
                    context as Activity,
                    Manifest.permission.WRITE_CALENDAR
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val text = "Permission already granted!"
                val duration = Toast.LENGTH_SHORT

                val toast = Toast.makeText(context, text, duration)
                toast.show()
            } else {
                //ask for permission
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity,
                        Manifest.permission.WRITE_CALENDAR
                    )
                ) {

                } else {
                    ActivityCompat.requestPermissions(
                        context as Activity,
                        Array<String>(1) { android.Manifest.permission.WRITE_CALENDAR },
                        0
                    )
                }
            }

            if (!permissionStatus) {

            }
/*
            if (permissionStatus){
                val cr: ContentResolver = ctx.getContentResolver()
                val values = ContentValues()

                values.put(CalendarContract.Events.DTSTART, dtstart)
                values.put(CalendarContract.Events.TITLE, habyteId)
                values.put(CalendarContract.Events.DESCRIPTION, comment)

                val timeZone: TimeZone = TimeZone.getDefault()
                values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID())

// Default calendar

// Default calendar
                values.put(CalendarContract.Events.CALENDAR_ID, 1)

                values.put(
                    CalendarContract.Events.RRULE, "FREQ=DAILY;UNTIL="
                            + dtUntill
                )
// Set Period for 1 Hour
// Set Period for 1 Hour
                values.put(CalendarContract.Events.DURATION, "+P1H")

                values.put(CalendarContract.Events.HAS_ALARM, 1)

// Insert event to calendar

// Insert event to calendar
                val uri: Uri? = cr.insert(CalendarContract.Events.CONTENT_URI, values)
            }
        }

*/
        }
        binding.deleteButton.setOnClickListener {
            viewModel.deleteHabyte()
            findNavController().navigateUp()
        }

        binding.editButton.setOnClickListener {
            val int = Intent(requireContext(), AddOrUpdateHabyteActivity::class.java)
            int.putExtra("habyteId", habyteId.toInt())
            startActivity(int)
        }

    }
}