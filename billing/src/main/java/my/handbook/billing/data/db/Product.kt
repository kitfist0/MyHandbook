package my.handbook.billing.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    val sku: String,
    var title: String = "",
    var originalJson: String = "",
    var priceAmountMicros: Long = 0L,
    var purchaseTime: Long = 0L,
    var purchaseToken: String = ""
)
