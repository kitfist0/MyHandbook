package simple.billing.ui

import androidx.recyclerview.widget.RecyclerView
import simple.billing.data.db.Product
import simple.billing.databinding.ItemProductBinding

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
