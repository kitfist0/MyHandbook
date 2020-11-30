package my.handbook.ui.drawer

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.android.material.shape.MaterialShapeDrawable
import dagger.hilt.android.AndroidEntryPoint
import my.handbook.R
import my.handbook.billing.util.billingViewModels
import my.handbook.common.themeColor
import my.handbook.databinding.FragmentDrawerBinding
import kotlin.LazyThreadSafetyMode.NONE

@AndroidEntryPoint
class DrawerFragment : Fragment(), DrawerAdapter.DrawerAdapterListener {

    private val viewModel: DrawerViewModel by billingViewModels()
    private val drawerAdapter = DrawerAdapter(this)
    private lateinit var binding: FragmentDrawerBinding

    private val behavior: BottomSheetBehavior<FrameLayout> by lazy(NONE) {
        from(binding.container)
    }

    private val bottomSheetCallback = BottomDrawerCallback()

    private val shapeDrawable: MaterialShapeDrawable by lazy(NONE) {
        val foregroundContext = binding.container.context
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

    private val closeDrawerOnBackPressed = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            close()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, closeDrawerOnBackPressed)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDrawerBinding.inflate(inflater, container, false)
        binding.container.setOnApplyWindowInsetsListener { view, windowInsets ->
            // Record the window's top inset so it can be applied when the bottom sheet is slide up
            // to meet the top edge of the screen.
            view.setTag(
                R.id.tag_system_window_inset_top,
                windowInsets.systemWindowInsetTop
            )
            windowInsets
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            container.background = shapeDrawable

            scrimView.setOnClickListener { close() }

            bottomSheetCallback.apply {
                // Scrim view transforms
                addOnSlideAction(AlphaSlideAction(scrimView))
                addOnStateChangedAction(VisibilityStateAction(scrimView))
                // Foreground transforms
                addOnSlideAction(ForegroundSheetTransformSlideAction(shapeDrawable))
                // Recycler transforms
                addOnStateChangedAction(ScrollToTopStateAction(drawerRecyclerView))
                // If the drawer is open, pressing the system back button should close the drawer.
                addOnStateChangedAction(object : OnStateChangedAction {
                    override fun onStateChanged(sheet: View, newState: Int) {
                        closeDrawerOnBackPressed.isEnabled = newState != STATE_HIDDEN
                    }
                })
            }

            behavior.addBottomSheetCallback(bottomSheetCallback)
            behavior.state = STATE_HIDDEN

            drawerRecyclerView.adapter = drawerAdapter
            drawerRecyclerView.setHasFixedSize(true)
        }

        viewModel.drawerItems.observe(viewLifecycleOwner) {
            Log.d("DEBUG_TAG", "drawerItems size: ${it?.size}")
            drawerAdapter.submitList(it)
        }
    }

    fun toggle() {
        when (behavior.state) {
            STATE_HIDDEN -> open()
            STATE_HALF_EXPANDED, STATE_EXPANDED, STATE_COLLAPSED -> close()
            STATE_DRAGGING, STATE_SETTLING -> {
            }
        }
    }

    fun open() {
        behavior.state = STATE_HALF_EXPANDED
    }

    fun close() {
        behavior.state = STATE_HIDDEN
    }

    fun addOnSlideAction(action: OnSlideAction) {
        bottomSheetCallback.addOnSlideAction(action)
    }

    fun addOnStateChangedAction(action: OnStateChangedAction) {
        bottomSheetCallback.addOnStateChangedAction(action)
    }

    override fun onSectionClicked(item: DrawerItem.SectionItem) {
        viewModel.onSectionClicked(item)
    }

    override fun onLinkClicked(item: DrawerItem.LinkItem) {
        close()
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.link))
        startActivity(intent)
    }

    override fun onProductClicked(item: DrawerItem.ProductItem) {
        activity?.let { viewModel.purchaseProduct(it, item.product.originalJson) }
    }
}
