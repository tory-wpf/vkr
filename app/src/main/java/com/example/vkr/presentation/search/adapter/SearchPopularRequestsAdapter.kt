package com.example.vkr.presentation.search.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vkr.databinding.ItemSearchSnippetBinding

class SearchPopularRequestsAdapter(
    private val list: MutableList<String>,
    private val listener: SeacrhRequestsClickListener
) : RecyclerView.Adapter<SearchPopularRequestsAdapter.SearcnPopularRequestViewHolder>() {

    inner class SearcnPopularRequestViewHolder(private val binding: ItemSearchSnippetBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = list[position]
            binding.run {
                searchSnippetTextView.text = item
            }

            itemView.setOnClickListener {
                listener.onSearchItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearcnPopularRequestViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchSnippetBinding.inflate(inflater, parent, false)
        return SearcnPopularRequestViewHolder(binding)
    }

    fun addItem(item: String) {
        if (!list.contains(item)){
            list.add(item)
            notifyItemInserted(itemCount - 1)
            notifyItemRangeChanged(0, itemCount)
        }
    }

    override fun onBindViewHolder(holder: SearcnPopularRequestViewHolder, position: Int) =
        holder.bind(position)

    override fun getItemCount(): Int = list.size

    interface SeacrhRequestsClickListener {

        fun onSearchItemClick(text: String)
    }
}