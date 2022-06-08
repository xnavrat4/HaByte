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
import fi.muni.android.habyte.databinding.BottomSheetHabitExtrainfoBinding
import fi.muni.android.habyte.databinding.BottomSheetHabitMenuBinding
import java.io.File
import java.time.LocalDateTime

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    HabiteListDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */
class HabitExtraDataDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetHabitExtrainfoBinding

    private lateinit var viewModel: HabitExtraInfoViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetHabitExtrainfoBinding.inflate(inflater, container, false)

        val id = HabitListDialogFragmentArgs.fromBundle(requireArguments()).habitId

        val db = (activity?.application as HabyteApplication).db

        this.viewModel = ViewModelProvider(
            this,
            HabitExtraInfoViewModelFactory(id, db.habitDao())
        ).get(HabitExtraInfoViewModel::class.java)

        viewModel.habit.observe(this) { habit ->
            if (habit.photoPath == null && habit.additionalText == null) {
                binding.noContentCard.visibility = View.VISIBLE
                binding.extraPhotoCard.visibility = View.GONE
                binding.extraDescriptionCard.visibility = View.GONE
                return@observe
            }
            binding.noContentCard.visibility = View.GONE

            habit.additionalText?.let {
                binding.extraDescriptionText.text = it
                binding.extraDescriptionCard.visibility = View.VISIBLE
            }

            habit.photoPath?.let {
                binding.extraPhotoView.setImageURI(it)
                binding.extraPhotoCard.visibility = View.VISIBLE
            }
        }

        return binding.root
    }


    companion object {
        const val TAG = "HabitExtraStuffMenu"

        fun newInstance(habitId: Int): HabitExtraDataDialogFragment =
            HabitExtraDataDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(HABIT_ID, habitId)
                }
            }

    }
}