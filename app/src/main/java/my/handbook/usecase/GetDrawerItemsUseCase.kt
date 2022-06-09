package my.handbook.usecase

import kotlinx.coroutines.flow.*
import my.handbook.BuildConfig
import my.handbook.R
import my.handbook.data.dao.SectionDao
import my.handbook.ui.drawer.DrawerItem
import my.handbook.data.remote.PlayBillingDataSource
import my.handbook.data.remote.onSuccess
import javax.inject.Inject

class GetDrawerItemsUseCase @Inject constructor(
    private val playBillingDataSource: PlayBillingDataSource,
    private val sectionDao: SectionDao,
) {

    private val immutableItems = listOf(
        DrawerItem.DividerItem(R.string.about),
        DrawerItem.LinkItem(
            link = "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}",
            titleRes = R.string.rate_app,
            iconRes = R.drawable.ic_twotone_grade
        ),
        DrawerItem.LinkItem(
            link = BuildConfig.LICENSE,
            titleRes = R.string.license,
            iconRes = R.drawable.ic_twotone_copyright
        ),
        DrawerItem.LinkItem(
            link = BuildConfig.GITHUB,
            titleRes = R.string.github,
            iconRes = R.drawable.ic_twotone_github
        ),
    )

    fun execute(): Flow<UseCaseResult<List<DrawerItem>>> {
        return sectionDao.getSections()
            .onStart { UseCaseResult.Loading }
            .combine(getCoffeeItems()) { sections, coffeeItems ->
                val sectionItems = sections.asSequence()
                    .map { DrawerItem.SectionItem(it) }
                UseCaseResult.Success(
                    sectionItems
                        .plus(immutableItems)
                        .plus(coffeeItems)
                        .toList()
                )
            }
    }

    private fun getCoffeeItems(): Flow<List<DrawerItem>> = flow {
        emit(emptyList())
        val coffeeItems = mutableListOf<DrawerItem>()
        playBillingDataSource.getProductsInfo().onSuccess { productsInfo ->
            playBillingDataSource.getIdsOfPurchasedProducts().onSuccess { purchasedIds ->
                productsInfo.onEach { productInfo ->
                    coffeeItems.add(
                        DrawerItem.CoffeeItem(
                            id = productInfo.id,
                            name = productInfo.name,
                            isPurchased = purchasedIds.contains(productInfo.id),
                        )
                    )
                }
            }
        }
        if (coffeeItems.isNotEmpty()) {
            val dividerItemCoffee = DrawerItem.DividerItem(R.string.coffee_for_developers)
            emit(listOf(dividerItemCoffee).plus(coffeeItems))
        } else {
            emit(coffeeItems)
        }
    }
}
