package simple.billing.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import simple.billing.R
import simple.billing.data.db.Product
import simple.billing.data.db.Product.Companion.isPurchased

class SimpleProductAdapter(
    private val listener: ProductAdapterListener,
) : androidx.recyclerview.widget.ListAdapter<Product, SimpleProductViewHolder>(MyItemDiffCallback) {

    companion object {
        object MyItemDiffCallback : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product) =
                oldItem.sku == newItem.sku
            override fun areContentsTheSame(oldItem: Product, newItem: Product) =
                oldItem.isPurchased() == newItem.isPurchased()
        }
    }

    interface ProductAdapterListener {
        fun onProductClicked(product: Product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleProductViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return SimpleProductViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: SimpleProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
