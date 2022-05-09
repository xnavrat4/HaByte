package fi.muni.android.habyte.ui.list.habyte

import androidx.recyclerview.widget.RecyclerView
import fi.muni.android.habyte.databinding.FragmentHabyteListItemBinding
import fi.muni.android.habyte.model.Habyte
import fi.muni.android.habyte.util.progressAsString
import java.time.format.DateTimeFormatter

class HabyteViewHolder(private val binding: FragmentHabyteListItemBinding)
    : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            listItem: Habyte,
            onItemClick: (Habyte) -> Unit
        ) {
            binding.habitName.text = listItem.name
            binding.startDateText.text = listItem.startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            binding.endDateText.text = listItem.endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

            binding.bar.max = listItem.habitsToDo
            binding.bar.progress = listItem.habitsFinished
            binding.progressLabel.text = listItem.habitsFinished.progressAsString(listItem.habitsToDo)

            binding.cardContainer.setOnClickListener {
                onItemClick(listItem)
            }
        }
}