package fi.muni.android.habyte.ui.list.habyte

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import fi.muni.android.habyte.databinding.FragmentHabyteListItemBinding
import fi.muni.android.habyte.model.Habyte

class HabyteAdapter(
    private val onItemClick: (Habyte) -> Unit
): ListAdapter<Habyte, HabyteViewHolder>(HabyteComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabyteViewHolder {
        val binding = FragmentHabyteListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HabyteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabyteViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, onItemClick)
    }

    class HabyteComparator : DiffUtil.ItemCallback<Habyte>() {
        override fun areItemsTheSame(oldItem: Habyte, newItem: Habyte): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Habyte, newItem: Habyte): Boolean {
            return oldItem == newItem
        }
    }
}