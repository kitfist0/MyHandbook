package my.handbook.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import my.handbook.data.entity.Product

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY priceAmountMicros")
    fun getProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE sku = :sku")
    suspend fun getProductWithSku(sku: String): Product?

    @Query("UPDATE products SET purchaseToken = :token, purchaseTime = :time WHERE sku = :sku")
    suspend fun onProductPurchased(sku: String, token: String, time: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(products: List<Product>)
}
