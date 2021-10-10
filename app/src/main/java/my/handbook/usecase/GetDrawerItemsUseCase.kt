package my.handbook.usecase

import kotlinx.coroutines.flow.*
import my.handbook.BuildConfig
import my.handbook.R
import my.handbook.data.dao.SectionDao
import my.handbook.ui.drawer.DrawerItem
import simple.billing.core.BillingHandler
import javax.inject.Inject

class GetDrawerItemsUseCase @Inject constructor(
    private val billingHandler: BillingHandler,
    private val sectionDao: SectionDao,
) {

    private val linkItems = listOf(
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
    private val dividerItemAbout = DrawerItem.DividerItem(R.string.about)
    private val dividerItemCoffee = DrawerItem.DividerItem(R.string.coffee_for_developers)

    fun execute(): Flow<UseCaseResult<List<DrawerItem>>> = billingHandler.products
        .transform { products ->
            emit(UseCaseResult.Loading)
            val drawerItems = sectionDao.getSections().asSequence()
                .map { DrawerItem.SectionItem(it) }
                .plus(dividerItemAbout)
                .plus(linkItems)
                .plus(dividerItemCoffee)
                .plus(products.map { DrawerItem.ProductItem(it) })
                .toList()
            emit(UseCaseResult.Success(drawerItems))
        }
}
