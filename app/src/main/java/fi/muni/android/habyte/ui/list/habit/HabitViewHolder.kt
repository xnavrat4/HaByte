package fi.muni.android.habyte.ui.list.habit

import androidx.recyclerview.widget.RecyclerView
import fi.muni.android.habyte.databinding.FragmentHabitListItemBinding
import fi.muni.android.habyte.model.Habit

class HabitViewHolder(private val binding: FragmentHabitListItemBinding)
    : RecyclerView.ViewHolder(binding.root) {

        fun bind(listItem: Habit, onDoneClick: (Habit) -> Unit) {
            binding.habitName.text = listItem.name
            binding.timeLabel.text = listItem.start.toString()
            binding.doneButton.setOnClickListener {
                onDoneClick(listItem)
            }
        }
    }