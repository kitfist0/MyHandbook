package my.handbook.billing.data.repository

import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import my.handbook.billing.data.db.Product
import my.handbook.billing.data.db.ProductDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
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

    suspend fun productPurchased(purchase: Purchase) = productDao.productPurchased(
        purchase.sku,
        purchase.purchaseToken,
        purchase.purchaseTime
    )
}
