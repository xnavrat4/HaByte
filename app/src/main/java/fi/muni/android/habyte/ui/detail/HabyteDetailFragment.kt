package fi.muni.android.habyte.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import fi.muni.android.habyte.R
import fi.muni.android.habyte.databinding.FragmentHabyteDetailBinding
import fi.muni.android.habyte.repository.HabyteRepository
import fi.muni.android.habyte.util.daysUntil
import fi.muni.android.habyte.util.progressAsString


class HabyteDetailFragment : Fragment() {

    private lateinit var binding: FragmentHabyteDetailBinding

    private val habyteRepository: HabyteRepository by lazy {
        HabyteRepository(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHabyteDetailBinding.inflate(
            LayoutInflater.from(context), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val habyteId = HabyteDetailFragmentArgs.fromBundle(requireArguments()).id
        val listItem = habyteRepository.getById(habyteId.toLong())

        binding.habitName.text = listItem.name
        binding.startDateText.text = listItem.startDate.toString()
        binding.endDateText.text = listItem.endDate.toString()

        val totalDays = listItem.startDate.daysUntil(listItem.endDate)
        binding.bar.max = totalDays
        binding.bar.progress = listItem.progress
        binding.progressLabel.text = listItem.progress.progressAsString(totalDays)
    }
}