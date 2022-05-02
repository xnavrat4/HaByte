package fi.muni.android.habyte.ui.list.habyte

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import fi.muni.android.habyte.R
import fi.muni.android.habyte.databinding.FragmentHabyteListBinding
import fi.muni.android.habyte.repository.HabyteRepository

class HabyteList : Fragment() {

    private lateinit var binding: FragmentHabyteListBinding

    private val habyteRepository: HabyteRepository by lazy {
        HabyteRepository(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHabyteListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = HabyteAdapter(
            onItemClick = {
                findNavController().navigate(HabyteListDirections.actionHabyteListFragmentToHabyteDetailFragment(it.id.toString()))
            },
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        adapter.submitList(habyteRepository.getMockedData())
    }
}