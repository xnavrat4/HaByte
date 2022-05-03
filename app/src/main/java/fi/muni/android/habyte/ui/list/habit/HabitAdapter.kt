package fi.muni.android.habyte.ui.list.habit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fi.muni.android.habyte.data.HabitPresentableListItem
import fi.muni.android.habyte.databinding.FragmentHabitListItemBinding

class HabitAdapter : RecyclerView.Adapter<HabitViewHolder>() {

    private var listItems: MutableList<HabitPresentableListItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = FragmentHabitListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return  HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(listItems[position])
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    fun submitList(newList: List<HabitPresentableListItem>) {
        listItems = newList.toMutableList()
        notifyDataSetChanged()
    }
}