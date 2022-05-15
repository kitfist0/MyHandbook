package simple.billing.ui

import android.view.View
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import simple.billing.R
import simple.billing.data.db.Product
import simple.billing.data.db.Product.Companion.getProductDrawable

class SimpleProductViewHolder(
    itemView: View,
    listener: SimpleProductAdapter.ProductAdapterListener,
) : RecyclerView.ViewHolder(itemView) {

    private var product: Product? = null

    private val productTitle = itemView.findViewById<TextView>(R.id.product_text_title).apply {
        setOnClickListener { product?.let { listener.onProductClicked(it) } }
    }

    fun bind(item: Product) {
        product = item
        productTitle.apply {
            text = item.title
            setCompoundDrawablesWithIntrinsicBounds(
                AppCompatResources.getDrawable(context, item.getProductDrawable()),
                null, null, null
            )
        }
    }
}
