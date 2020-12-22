package simple.billing.data.repository

import android.content.Context
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import simple.billing.data.db.Product
import simple.billing.data.db.ProductDao
import simple.billing.data.db.ProductDatabase

class ProductRepository(
    private val productDao: ProductDao
) {

    val productsFlow = productDao.getProducts()

    suspend fun setupProducts(skuDetailsList: List<SkuDetails>) = skuDetailsList
        .map {
            val index = it.title.indexOf('(')
            Product(
                sku = it.sku,
                title = if (index > 0) it.title.substring(0, index).trim() else it.title,
                originalJson = it.originalJson,
                priceAmountMicros = it.priceAmountMicros
            )
        }
        .also { productDao.insert(it) }

    suspend fun onProductPurchased(purchase: Purchase) = productDao.onProductPurchased(
        purchase.sku,
        purchase.purchaseToken,
        purchase.purchaseTime
    )

    companion object {

        @Volatile private var instance: ProductRepository? = null

        fun getInstance(context: Context): ProductRepository {
            return instance ?: synchronized(this) {
                instance ?: ProductRepository(ProductDatabase.getInstance(context).productDao())
                    .also { instance = it }
            }
        }
    }
}
