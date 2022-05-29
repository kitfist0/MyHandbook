package my.handbook.data

import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import kotlinx.coroutines.flow.Flow
import my.handbook.data.dao.ProductDao
import my.handbook.data.entity.Product
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao,
) {

    val productsFlow: Flow<List<Product>> = productDao.getProducts()

    suspend fun setupProducts(skuDetailsList: List<SkuDetails>) = skuDetailsList
        .map {
            val index = it.title.indexOf('(')
            Product(
                sku = it.sku,
                title = if (index > 0) it.title.substring(0, index).trim() else it.title,
                originalJson = it.originalJson,
                priceAmountMicros = it.priceAmountMicros,
            )
        }
        .also { productDao.insert(it) }

    suspend fun onProductPurchased(purchase: Purchase) = productDao.onProductPurchased(
        purchase.skus[0],
        purchase.purchaseToken,
        purchase.purchaseTime,
    )
}
