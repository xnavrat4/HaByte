package fi.muni.android.habyte.ui.list.habyte

import androidx.recyclerview.widget.RecyclerView
import fi.muni.android.habyte.R
import fi.muni.android.habyte.databinding.FragmentHabyteListItemBinding
import fi.muni.android.habyte.model.Habyte
import fi.muni.android.habyte.util.progressAsString
import java.util.*

class HabyteViewHolder(private val binding: FragmentHabyteListItemBinding)
    : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            listItem: Habyte,
            onItemClick: (Habyte) -> Unit
        ) {
            binding.habitName.text = listItem.name
            binding.startDateText.text = listItem.startDate.toString()
            binding.endDateText.text = listItem.endDate.toString()

            binding.bar.max = listItem.habitsToDo
            binding.bar.progress = listItem.habitsFinished
            binding.progressLabel.text = listItem.habitsFinished.progressAsString(listItem.habitsToDo)

            binding.startDateLabel.text = binding.root.resources.getString(R.string.start_date)
            binding.endDateLabel.text = binding.root.resources.getString(R.string.end_date)


            binding.cardContainer.setOnClickListener {
                onItemClick(listItem)
            }
        }
}