package my.handbook.ui.drawer

import android.content.res.ColorStateList
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.android.material.shape.MaterialShapeDrawable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import my.handbook.R
import my.handbook.common.themeColor
import my.handbook.databinding.FragmentDrawerBinding
import my.handbook.ui.base.BaseFragment

@AndroidEntryPoint
class DrawerFragment : BaseFragment<FragmentDrawerBinding>(), DrawerAdapter.DrawerAdapterListener, DrawerInterface {

    override val layoutRes: Int = R.layout.fragment_drawer
    override val viewModel: DrawerViewModel by viewModels()

    override val drawerBehaviorCallback = DrawerBehaviorCallback()

    override fun toggleState() {
        viewModel.toggleBottomSheetState()
    }

    private val drawerAdapter = DrawerAdapter(this)
    private val behavior: BottomSheetBehavior<FrameLayout> by lazy(LazyThreadSafetyMode.NONE) {
        from(binding.drawerContainer)
    }
    private val shapeDrawable: MaterialShapeDrawable by lazy(LazyThreadSafetyMode.NONE) {
        val foregroundContext = binding.drawerContainer.context
        MaterialShapeDrawable(foregroundContext, null, R.attr.bottomSheetStyle, 0)
            .apply {
                fillColor = ColorStateList.valueOf(
                    foregroundContext.themeColor(R.attr.colorPrimarySurface)
                )
                elevation = resources.getDimension(R.dimen.plane_16)
                shadowCompatibilityMode = MaterialShapeDrawable.SHADOW_COMPAT_MODE_NEVER
                initializeElevationOverlay(requireContext())
            }
    }

    override fun initViews() {
        binding.drawerContainer.setOnApplyWindowInsetsListener { view, windowInsets ->
            // Record the window's top inset so it can be applied when the bottom sheet is slide up
            // to meet the top edge of the screen
            view.setTag(R.id.tag_system_window_inset_top, windowInsets.systemWindowInsetTop)
            windowInsets
        }
        binding.run {
            drawerContainer.background = shapeDrawable

            drawerScrimView.setOnClickListener {
                viewModel.onScrimViewClicked()
            }

            drawerBehaviorCallback.apply {
                // Scrim view transforms
                addOnSlideAction(AlphaSlideAction(drawerScrimView))
                addOnStateChangedAction(VisibilityStateAction(drawerScrimView))
                // Foreground transforms
                addOnSlideAction(ForegroundSheetTransformSlideAction(shapeDrawable))
                // Recycler transforms
                addOnStateChangedAction(ScrollToTopStateAction(drawerRecyclerView))
                // If the drawer is open, pressing the system back button should close the drawer
                addOnStateChangedAction(object : OnStateChangedAction {
                    override fun onStateChanged(sheet: View, newState: Int) {
                        viewModel.onBottomSheetStateChanged(newState)
                    }
                })
            }

            behavior.addBottomSheetCallback(drawerBehaviorCallback)
            behavior.state = STATE_HIDDEN

            drawerRecyclerView.adapter = drawerAdapter
            drawerRecyclerView.setHasFixedSize(true)
        }
    }

    override fun observeData() {
        viewModel.drawerItems.observe(viewLifecycleOwner) {
            drawerAdapter.submitList(it)
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.bottomSheetState.collect {
                behavior.state = it
            }
        }
    }

    override fun onSectionClicked(item: DrawerItem.SectionItem) {
        viewModel.onSectionClicked(item)
    }

    override fun onLinkClicked(item: DrawerItem.LinkItem) {
        viewModel.onLinkClicked(item)
    }

    override fun onProductClicked(item: DrawerItem.ProductItem) {
        viewModel.onProductClicked(activity, item)
    }
}
