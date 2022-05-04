package fi.muni.android.habyte.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import fi.muni.android.habyte.AddOrUpdateHabyteActivity
import fi.muni.android.habyte.HabyteApplication
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