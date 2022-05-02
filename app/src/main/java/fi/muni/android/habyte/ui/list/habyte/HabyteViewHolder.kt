package fi.muni.android.habyte.ui.list.habyte

import androidx.recyclerview.widget.RecyclerView
import fi.muni.android.habyte.data.HabytePresentableListItem
import fi.muni.android.habyte.databinding.FragmentHabyteListBinding
import fi.muni.android.habyte.databinding.FragmentHabyteListItemBinding
import fi.muni.android.habyte.util.daysUntil
import fi.muni.android.habyte.util.progressAsString

class HabyteViewHolder(private val binding: FragmentHabyteListItemBinding)
    : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            listItem: HabytePresentableListItem,
            onItemClick: (HabytePresentableListItem) -> Unit
        ) {
            binding.habitName.text = listItem.name
            binding.startDateText.text = listItem.startDate.toString()
            binding.endDateText.text = listItem.endDate.toString()

            val totalDays = listItem.startDate.daysUntil(listItem.endDate)
            binding.bar.max = totalDays
            binding.bar.progress = listItem.progress
            binding.progressLabel.text = listItem.progress.progressAsString(totalDays)

            binding.cardContainer.setOnClickListener {
                onItemClick(listItem)
            }
        }
}