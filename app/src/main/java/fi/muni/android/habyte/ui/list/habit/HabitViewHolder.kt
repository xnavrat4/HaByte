package fi.muni.android.habyte.ui.list.habit

import androidx.recyclerview.widget.RecyclerView
import fi.muni.android.habyte.data.HabitPresentableListItem
import fi.muni.android.habyte.databinding.FragmentHabitListItemBinding

class HabitViewHolder(private val binding: FragmentHabitListItemBinding)
    : RecyclerView.ViewHolder(binding.root) {

        fun bind(listItem: HabitPresentableListItem) {
            binding.habitName.text = listItem.name
            binding.timeLabel.text = listItem.time.toString()
        }
}