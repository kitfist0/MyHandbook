package my.handbook.ui.drawer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import my.handbook.BuildConfig
import my.handbook.R
import my.handbook.data.repository.SectionRepository
import my.handbook.ui.base.BaseViewModel
import simple.billing.core.BillingHandler
import javax.inject.Inject

@HiltViewModel
class DrawerViewModel @Inject constructor(
    private val billingHandler: BillingHandler,
    private val sectionRepository: SectionRepository,
) : BaseViewModel() {

    private val drawerLinkItems = listOf(
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

    private val drawerProductItems = billingHandler.products.mapNotNull {
        it.map { product -> DrawerItem.ProductItem(product) }
    }

    val drawerItems = sectionRepository.getSections()
        .map { sections ->
            sections.map { DrawerItem.SectionItem(it) }
                .plus(listOf(DrawerItem.DividerItem(R.string.about)))
                .plus(drawerLinkItems)
        }
        .combine(drawerProductItems) { prevDrawerItems, productItems ->
            if (productItems.isEmpty()) {
                prevDrawerItems
            } else {
                prevDrawerItems
                    .plus(listOf(DrawerItem.DividerItem(R.string.coffee_for_developers)))
                    .plus(productItems)
            }
        }
        .asLiveData()

    private val _bottomSheetState = MutableStateFlow(BottomSheetBehavior.STATE_HIDDEN)
    val bottomSheetState = _bottomSheetState.asStateFlow()

    override fun onBackPressed() {
        if (bottomSheetState.value != BottomSheetBehavior.STATE_HIDDEN) {
            changeBottomSheetState(BottomSheetBehavior.STATE_HIDDEN)
        } else {
            super.onBackPressed()
        }
    }

    fun onBottomSheetStateChanged(newState: Int) {
        viewModelScope.launch { _bottomSheetState.emit(newState) }
    }

    fun onSectionClicked(sectionItem: DrawerItem.SectionItem) =
        viewModelScope.launch { sectionRepository.updateSection(sectionItem.section) }

    fun onLinkClicked(linkItem: DrawerItem.LinkItem) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkItem.link))
        startActivity(intent)
    }

    fun onProductClicked(activity: Activity?, productItem: DrawerItem.ProductItem) {
        activity?.let {
            billingHandler.purchaseProduct(it, productItem.product.originalJson)
        }
    }

    fun onScrimViewClicked() {
        changeBottomSheetState(BottomSheetBehavior.STATE_HIDDEN)
    }

    fun toggleBottomSheetState() {
        when (_bottomSheetState.value) {
            BottomSheetBehavior.STATE_HIDDEN ->
                changeBottomSheetState(BottomSheetBehavior.STATE_HALF_EXPANDED)
            BottomSheetBehavior.STATE_HALF_EXPANDED,
            BottomSheetBehavior.STATE_EXPANDED,
            BottomSheetBehavior.STATE_COLLAPSED ->
                changeBottomSheetState(BottomSheetBehavior.STATE_HIDDEN)
        }
    }

    private fun changeBottomSheetState(newState: Int) {
        viewModelScope.launch { _bottomSheetState.emit(newState) }
    }
}
