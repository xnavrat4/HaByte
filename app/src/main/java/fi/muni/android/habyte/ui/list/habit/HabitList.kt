package fi.muni.android.habyte.ui.list.habit

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import fi.muni.android.habyte.HabyteApplication
import fi.muni.android.habyte.databinding.FragmentHabitListBinding


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

        viewModel.getHabitsForToday().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}