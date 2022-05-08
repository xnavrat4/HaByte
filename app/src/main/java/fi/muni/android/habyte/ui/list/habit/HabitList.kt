package fi.muni.android.habyte.ui.list.habit

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import fi.muni.android.habyte.HabyteApplication
import fi.muni.android.habyte.R
import fi.muni.android.habyte.databinding.FragmentHabitListBinding
import fi.muni.android.habyte.model.Habit
import fi.muni.android.habyte.util.NotificationHelper
import fi.muni.android.habyte.util.toIdsString
import java.time.LocalDate


class HabitList : Fragment() {

    private lateinit var binding: FragmentHabitListBinding

    private val viewModel: HabitListViewModel by viewModels {
        val db = (activity?.application as HabyteApplication).db
        HabitListViewModelFactory(db.habitDao(), db.habyteDao())
    }

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

        val intentPref = requireContext().getSharedPreferences(
            getString(R.string.latest_intents_update_date), Context.MODE_PRIVATE)

        viewModel.getHabitsForToday().observe(viewLifecycleOwner) {
            adapter.submitList(it)

            val savedIntentIds = intentPref.getString(getString(R.string.saved_intents), "")!!
            if (it.toIdsString() != savedIntentIds) {
                updateIntents(
                    intentPref = intentPref,
                    updatedHabits = it,
                    savedIntentIds = savedIntentIds)
            }
        }
    }

    private fun updateIntents(intentPref: SharedPreferences, updatedHabits: List<Habit>, savedIntentIds: String) {

        var toUnschedule: List<Int>? = null

        if (savedIntentIds.isNotEmpty()) {
            toUnschedule = savedIntentIds.split(",").map { s -> s.toInt() }
        }

        NotificationHelper.updateNotificationsSchedulesForToday(
            context = requireContext(),
            habitsToSchedule = updatedHabits,
            habitsToUnschedule = toUnschedule
        )

        with(intentPref.edit()) {
            val toSave = updatedHabits.toIdsString()
            putString(getString(R.string.saved_intents), toSave)
            putString(
                getString(R.string.latest_intents_update_date),
                LocalDate.now().toString()
            )
            apply()
        }
    }
}