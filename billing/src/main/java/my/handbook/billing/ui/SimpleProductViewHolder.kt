package my.handbook.billing.ui

import androidx.recyclerview.widget.RecyclerView
import my.handbook.billing.data.db.Product
import my.handbook.billing.databinding.ItemProductBinding

class SimpleProductViewHolder(
    private val binding: ItemProductBinding,
    listener: SimpleProductAdapter.ProductAdapterListener
): RecyclerView.ViewHolder(binding.root) {

    init {
        binding.run {
            this.listener = listener
        }
    }

    fun bind(item: Product) {
        binding.item = item
        binding.executePendingBindings()
    }
}
