package fi.muni.android.habyte.ui.list.habyte

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import fi.muni.android.habyte.HabyteApplication
import fi.muni.android.habyte.databinding.FragmentHabyteListBinding

class HabyteList : Fragment() {

    private lateinit var binding: FragmentHabyteListBinding

    private val viewModel: HabyteListViewModel by viewModels {
        val db = (activity?.application as HabyteApplication).db
        HabyteListViewModelFactory(db.habyteDao())
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
                findNavController().navigate(
                    HabyteListDirections.actionHabyteListFragmentToHabyteDetailFragment(
                        it.id.toString()
                    )
                )
            },
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.getHabytes().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}