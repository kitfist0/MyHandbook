package my.handbook.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import my.handbook.R

@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    val sku: String,
    var title: String = "",
    var originalJson: String = "",
    var priceAmountMicros: Long = 0L,
    var purchaseTime: Long = 0L,
    var purchaseToken: String = "",
) {
    companion object {
        fun Product.isPurchased() = purchaseToken.isNotEmpty()

        fun Product.getProductDrawable() = if (isPurchased()) {
            R.drawable.ic_product_purchased
        } else {
            R.drawable.ic_product_default
        }
    }
}
