package fi.muni.android.habyte.ui.list.habit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import fi.muni.android.habyte.databinding.FragmentHabitListItemBinding
import fi.muni.android.habyte.model.Habit

class HabitAdapter(
    private val onDoneClick: (Habit) -> Unit
) : ListAdapter<Habit, HabitViewHolder>(HabitComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = FragmentHabitListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, onDoneClick)
    }

    class HabitComparator: DiffUtil.ItemCallback<Habit>() {
        override fun areItemsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem == newItem
        }
    }
}