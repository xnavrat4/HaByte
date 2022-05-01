package fi.muni.android.habyte.ui.list.habyte

import androidx.recyclerview.widget.RecyclerView
import fi.muni.android.habyte.data.HabytePresentableListItem
import fi.muni.android.habyte.databinding.FragmentHabyteListBinding
import fi.muni.android.habyte.databinding.FragmentHabyteListItemBinding

class HabyteViewHolder(private val binding: FragmentHabyteListItemBinding)
    : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            listItem: HabytePresentableListItem,
            onItemClick: (HabytePresentableListItem) -> Unit
        ) {
            binding.habitName.text = listItem.name
            binding.cardContainer.setOnClickListener {
                onItemClick(listItem)
            }
        }
}