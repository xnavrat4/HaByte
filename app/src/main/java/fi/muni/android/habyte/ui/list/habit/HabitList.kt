package fi.muni.android.habyte.ui.list.habit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import fi.muni.android.habyte.HabyteApplication
import fi.muni.android.habyte.databinding.FragmentHabitListBinding
import fi.muni.android.habyte.model.Habit
import fi.muni.android.habyte.util.NotificationHelper
import java.io.File

class HabitList : Fragment() {

    private lateinit var binding: FragmentHabitListBinding

    private val viewModel: HabitListViewModel by viewModels {
        val db = (activity?.application as HabyteApplication).db
        HabitListViewModelFactory(db.habitDao(), db.habyteDao())
    }

    private var savedIntents = ""
    private val savedIntentsFileName = "notifIntents"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHabitListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = HabitAdapter(viewModel::confirmHabit)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.getHabitsForToday().observe(viewLifecycleOwner) {
            adapter.submitList(it)

            if (savedIntents.isEmpty()) {
                loadSavedIntents()
            }

            if (it.map { h -> h.id }.toString() != savedIntents) {
                updateIntents(it)
            }
        }
    }

    private fun loadSavedIntents() {
        val savedIntentsFile = File(requireContext().filesDir, savedIntentsFileName)
        if (!savedIntentsFile.exists()) {
            savedIntentsFile.createNewFile()
        }
        savedIntents = savedIntentsFile.readText()
    }

    private fun updateIntents(habits: List<Habit>) {
        val savedIntentsFile = File(requireContext().filesDir, savedIntentsFileName)
        var toUnschedule: List<Int>? = null

        val savedInFile = savedIntentsFile.readText().removeSurrounding("[", "]")
        if (savedInFile.isNotEmpty()) {
            toUnschedule = savedInFile.replace(" ", "")
                .split(",").map { s -> s.toInt() }
        }

        NotificationHelper.updateNotificationsSchedulesForToday(
            context = requireContext(),
            habitsToSchedule = habits,
            habitsToUnschedule = toUnschedule
        )
        savedIntents = habits.map { it.id }.toString()
        savedIntentsFile.writeText(savedIntents)
    }
}