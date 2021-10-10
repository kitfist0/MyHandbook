package my.handbook.ui.search

import androidx.recyclerview.widget.RecyclerView
import my.handbook.data.entity.SearchResult
import my.handbook.databinding.ItemSearchResultBinding

class SearchResultViewHolder(
    private val binding: ItemSearchResultBinding,
    listener: SearchResultAdapter.SearchResultAdapterListener,
): RecyclerView.ViewHolder(binding.root) {

    init {
        binding.run {
            this.listener = listener
        }
    }

    fun bind(item: SearchResult) {
        binding.item = item
        binding.executePendingBindings()
    }
}
