package my.handbook.ui.drawer

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import my.handbook.BuildConfig
import my.handbook.R
import my.handbook.billing.core.BillingHandler
import my.handbook.billing.ui.BillingViewModel
import my.handbook.common.combineWith
import my.handbook.data.repository.SectionRepository

class DrawerViewModel @ViewModelInject constructor(
    private val sectionRepository: SectionRepository,
    billingHandler: BillingHandler
) : BillingViewModel(billingHandler) {

    companion object {
        private val aboutDivider = listOf(DrawerItem.DividerItem(R.string.about))
        private val links = listOf(
            DrawerItem.LinkItem(
                link = "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}",
                titleRes = R.string.rate_app,
                iconRes = R.drawable.ic_twotone_grade
            ),
            DrawerItem.LinkItem(
                link = BuildConfig.GITHUB,
                titleRes = R.string.github,
                iconRes = R.drawable.ic_twotone_github
            ),
            DrawerItem.LinkItem(
                link = BuildConfig.LICENSE,
                titleRes = R.string.license,
                iconRes = R.drawable.ic_twotone_copyright
            )
        )
        private val coffeeDivider = listOf(DrawerItem.DividerItem(R.string.coffee_for_developers))
    }

    private val sections = liveData {
        sectionRepository.getSections().collect { emit(it) }
    }

    private val sectionAndLinkItems = sections.switchMap {
        liveData {
            val items = it.map { section -> DrawerItem.SectionItem(section) }
                .plus(aboutDivider)
                .plus(links)
            emit(items)
        }
    }

    private val productItems = products.map {
        it.map { product -> DrawerItem.ProductItem(product) }
    }

    val drawerItems = sectionAndLinkItems.combineWith(productItems) { slList, pList ->
        if (pList.isNullOrEmpty()) slList else slList?.plus(coffeeDivider)?.plus(pList)
    }

    fun onSectionClicked(sectionItem: DrawerItem.SectionItem) =
        viewModelScope.launch { sectionRepository.updateSection(sectionItem.section) }
}
