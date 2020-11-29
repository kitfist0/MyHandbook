package my.handbook.billing.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import my.handbook.billing.R
import my.handbook.billing.data.db.ProductDao
import my.handbook.billing.data.db.ProductDatabase
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object BillingModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): ProductDatabase {
        return Room.databaseBuilder(app, ProductDatabase::class.java, "billing.db")
            .fallbackToDestructiveMigration() // Data is cache, so it is OK to delete
            .build()
    }

    @Provides
    @Singleton
    fun provideProductDao(db: ProductDatabase): ProductDao {
        return db.productDao()
    }

    @Provides
    @Singleton
    @Named("skuList")
    fun provideSkuList(app: Application): List<String> {
        val array = app.resources.getStringArray(R.array.product_ids)
        return array.toList()
    }
}
