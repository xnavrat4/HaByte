package fi.muni.android.habyte.ui.list.habit.sheet

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import fi.muni.android.habyte.HabyteApplication
import fi.muni.android.habyte.R
import fi.muni.android.habyte.databinding.BottomSheetHabitMenuBinding
import java.io.File
import java.time.LocalDateTime

const val HABIT_ID = "habit_id"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    HabiteListDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */
class HabitListDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetHabitMenuBinding

    private lateinit var viewModel: BottomSheetViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetHabitMenuBinding.inflate(inflater, container, false)

        val id = HabitListDialogFragmentArgs.fromBundle(requireArguments()).habitId

        val db = (activity?.application as HabyteApplication).db

        this.viewModel = ViewModelProvider(
            this,
            BottomSheetViewModelFactory(id, db.habitDao())
        ).get(BottomSheetViewModel::class.java)

        val takePictureResult = registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) {
            it?.let {
                if (it) {
                    viewModel.savePhoto()
                } else {
                    Toast.makeText(requireContext(), "NOPE", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.fromCameraMenuLayout.setOnClickListener {
            if (takePicture(id, takePictureResult)) return@setOnClickListener
        }

        binding.applyTextButton.setOnClickListener {
            if (binding.descriptionTextView.text?.toString()?.isNotBlank() == true) {
                viewModel.updateHabyte(binding.descriptionTextView.text.toString(), null)
                binding.applyTextButton.setTextColor(resources.getColor(R.color.mint_green))
            }
        }
        return binding.root
    }

    private fun takePicture(
        id: Int,
        takePictureResult: ActivityResultLauncher<Uri>
    ): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 1)
        }
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission(Manifest.permission.CAMERA, 1)
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                requireContext(),
                "Cannot take pictures without necessary permissions",
                Toast.LENGTH_SHORT
            ).show()
            return true
        }

        activity?.let {
            val dir = it.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
            val fileUri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.android.fileprovider",
                File(dir, "${id}_${LocalDateTime.now()}}.jpg")
            )
            viewModel.path = fileUri
            takePictureResult.launch(fileUri)
        }
        return false
    }

    private fun requestPermission(permission: String, requestCode: Int = 0) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                permission
            )
        ) {
            AlertDialog.Builder(requireContext())
                .setTitle("Permission needed")
                .setMessage("This permission is needed to create calendar events")
                .setPositiveButton(
                    "ok"
                ) { _, _ ->

                    ActivityCompat.requestPermissions(
                        requireActivity(), arrayOf(
                            permission
                        ), requestCode
                    )
                }
                .setNegativeButton(
                    "cancel"
                ) { dialog, _ -> dialog.dismiss() }
                .create().show()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(permission),
                requestCode
            )
        }
    }

    companion object {
        const val TAG = "HabitExtraMenu"

        fun newInstance(habitId: Int): HabitListDialogFragment =
            HabitListDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(HABIT_ID, habitId)
                }
            }

    }
}