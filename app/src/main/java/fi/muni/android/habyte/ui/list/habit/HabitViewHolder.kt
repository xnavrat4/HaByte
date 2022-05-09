package fi.muni.android.habyte.ui.list.habit

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import fi.muni.android.habyte.databinding.FragmentHabitListItemBinding
import fi.muni.android.habyte.model.Habit
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class HabitViewHolder(private val binding: FragmentHabitListItemBinding)
    : RecyclerView.ViewHolder(binding.root) {

        fun bind(listItem: Habit, onDoneClick: (Habit) -> Unit) {
            binding.habitName.text = listItem.name
            binding.timeLabel.text =  listItem.start.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +listItem.start.format(DateTimeFormatter.ofPattern("HH:mm"))
            if (listItem.start.dayOfMonth < LocalDate.now().dayOfMonth && !listItem.done){
                binding.cardContainer.setBackgroundColor(Color.parseColor("#FF0000"))
            }else if (listItem.start.dayOfMonth < LocalDate.now().dayOfMonth && listItem.done){
                binding.cardContainer.setBackgroundColor(Color.parseColor("#008000"))
            }
            binding.timeLabel.text = "${listItem.start.hour}:${listItem.start.minute}"

            binding.doneButton.setOnClickListener {
                onDoneClick(listItem)
            }
        }
}
