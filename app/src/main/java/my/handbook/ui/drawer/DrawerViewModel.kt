package my.handbook.ui.drawer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import my.handbook.ui.base.BaseViewModel
import my.handbook.usecase.*
import javax.inject.Inject

@HiltViewModel
class DrawerViewModel @Inject constructor(
    private val changeSectionSelectionUseCase: ChangeSectionSelectionUseCase,
    getDrawerItemsUseCase: GetDrawerItemsUseCase,
    private val purchaseProductUseCase: PurchaseProductUseCase,
) : BaseViewModel() {

    private val _drawerItems = MutableStateFlow<List<DrawerItem>>(emptyList())
    val drawerItems: LiveData<List<DrawerItem>> = _drawerItems.asLiveData()

    init {
        getDrawerItemsUseCase.execute().onEach { useCaseResult ->
            useCaseResult.updateOnSuccess(_drawerItems)
        }.launchIn(viewModelScope)
    }

    private val _bottomSheetState = MutableStateFlow(BottomSheetBehavior.STATE_HIDDEN)
    val bottomSheetState = _bottomSheetState.asStateFlow()

    override fun onBackPressed() {
        if (bottomSheetState.value != BottomSheetBehavior.STATE_HIDDEN) {
            changeBottomSheetState(BottomSheetBehavior.STATE_HIDDEN)
        } else {
            super.onBackPressed()
        }
    }

    fun onSectionClicked(sectionItem: DrawerItem.SectionItem) {
        viewModelScope.launch { changeSectionSelectionUseCase.execute(sectionItem.section) }
    }

    fun onLinkClicked(linkItem: DrawerItem.LinkItem) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkItem.link))
        startActivity(intent)
    }

    fun onProductClicked(activity: Activity?, productItem: DrawerItem.ProductItem) {
        purchaseProductUseCase.execute(activity, productItem.product)
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
        _bottomSheetState.value = newState
    }
}
