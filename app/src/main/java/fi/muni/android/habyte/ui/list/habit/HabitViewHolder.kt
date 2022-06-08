package fi.muni.android.habyte.ui.list.habit

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import fi.muni.android.habyte.databinding.FragmentHabitListItemBinding
import fi.muni.android.habyte.model.Habit
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HabitViewHolder(private val binding: FragmentHabitListItemBinding)
    : RecyclerView.ViewHolder(binding.root) {

        fun bind(listItem: Habit,
                 onDoneClick: (Habit) -> Unit,
                 onExpandMenu: (Habit) -> Unit
        ) {
            binding.habitName.text = listItem.name
            binding.timeLabel.text =  listItem.start.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" + listItem.start.format(DateTimeFormatter.ofPattern("HH:mm"))
            if (listItem.start.dayOfMonth < LocalDate.now().dayOfMonth && !listItem.done){
                binding.cardContainer.strokeColor = (Color.parseColor("#ff0000"))
            }else if (listItem.done){
                binding.cardContainer.strokeColor = Color.parseColor("#008000")
            }
            binding.photoPresentPhoto.visibility = View.INVISIBLE
            binding.descriptionPresentPhoto.visibility = View.INVISIBLE

            listItem.photoPath?.let {
                binding.photoPresentPhoto.visibility = View.VISIBLE
            }

            listItem.additionalText?.let {
                binding.descriptionPresentPhoto.visibility = View.VISIBLE
            }

            if (listItem.done) {
                binding.doneButton.text = "Finished"
                binding.doneButton.isEnabled = false
            } else {
                binding.doneButton.text = "DONE"
                binding.doneButton.isEnabled = true
                binding.doneButton.setOnClickListener {
                    onDoneClick(listItem)
                }
            }

            binding.expandableButton.setOnClickListener {
                onExpandMenu(listItem)
            }
        }
}
