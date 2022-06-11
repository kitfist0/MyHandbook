package my.handbook.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import my.handbook.data.local.model.SearchResult
import my.handbook.databinding.ItemSearchResultBinding

class SearchResultAdapter(
    private val listener: SearchResultAdapterListener,
) : ListAdapter<SearchResult, SearchResultViewHolder>(ParagraphDiffCallback) {

    companion object {
        object ParagraphDiffCallback : DiffUtil.ItemCallback<SearchResult>() {
            override fun areItemsTheSame(oldItem: SearchResult, newItem: SearchResult) =
                oldItem.text == newItem.text
            override fun areContentsTheSame(oldItem: SearchResult, newItem: SearchResult) =
                oldItem == newItem
        }
    }

    interface SearchResultAdapterListener {
        fun onSearchResultClicked(file: String, text: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        return SearchResultViewHolder(
            ItemSearchResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
