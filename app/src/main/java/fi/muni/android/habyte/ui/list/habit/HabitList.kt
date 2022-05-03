package fi.muni.android.habyte.ui.list.habit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import fi.muni.android.habyte.databinding.FragmentHabitListBinding
import fi.muni.android.habyte.repository.HabitRepository

class HabitList : Fragment() {
    private lateinit var binding: FragmentHabitListBinding

    private val habitRepository: HabitRepository by lazy {
        HabitRepository(requireContext())
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
        val adapter = HabitAdapter()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        adapter.submitList(habitRepository.getMockedData())
    }
}