package my.handbook.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import my.handbook.data.db.entity.Article
import my.handbook.databinding.ItemArticleBinding

class ArticleAdapter(
    private val listener: ArticleAdapterListener
) : ListAdapter<Article, ArticleViewHolder>(MyItemDiffCallback) {

    companion object {
        object MyItemDiffCallback : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article) =
                oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Article, newItem: Article) =
                oldItem == newItem
        }
    }

    interface ArticleAdapterListener {
        fun onArticleClicked(cardView: View, file: String)
        fun onFavoriteChanged(item: Article)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            ItemArticleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
