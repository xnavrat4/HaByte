package fi.muni.android.habyte.ui.list.habyte

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fi.muni.android.habyte.data.HabytePresentableListItem
import fi.muni.android.habyte.databinding.FragmentHabyteListItemBinding

class HabyteAdapter(
    private val onItemClick: (HabytePresentableListItem) -> Unit
): RecyclerView.Adapter<HabyteViewHolder>() {

    private var listItems: MutableList<HabytePresentableListItem> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabyteViewHolder {
        val binding = FragmentHabyteListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return  HabyteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabyteViewHolder, position: Int) {
        holder.bind(listItems[position], onItemClick)
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    fun submitList(newList: List<HabytePresentableListItem>) {
        listItems = newList.toMutableList()
        notifyDataSetChanged()
    }
}