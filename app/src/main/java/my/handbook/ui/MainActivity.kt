package my.handbook.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import my.handbook.NavGraphDirections
import my.handbook.R
import my.handbook.databinding.ActivityMainBinding
import my.handbook.ui.drawer.DrawerInterface
import my.handbook.ui.drawer.HalfClockwiseRotateSlideAction
import my.handbook.ui.drawer.ShowHideFabStateAction
import my.handbook.util.contentView

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private val binding: ActivityMainBinding by contentView(R.layout.activity_main)

    private val drawerInterface: DrawerInterface by lazy(LazyThreadSafetyMode.NONE) {
        supportFragmentManager.findFragmentById(R.id.bottom_nav_drawer) as DrawerInterface
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        setUpBottomNavigationAndFab()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.homeFragment -> binding.run {
                bottomAppBar.visibility = View.VISIBLE
                bottomAppBar.performShow()
                fab.show()
            }
            R.id.readFragment, R.id.searchFragment ->
                hideBottomAppBar().also { binding.fab.hide() }
        }
    }

    private fun setUpBottomNavigationAndFab() {
        // Wrap binding.run to ensure ContentViewBindingDelegate is calling this Activity's
        // setContentView before accessing views
        binding.run {
            findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener(
                this@MainActivity
            )
        }

        // Set a custom animation for showing and hiding the FAB
        binding.fab.apply {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
            setOnClickListener {
                findNavController(R.id.nav_host_fragment).navigate(
                    NavGraphDirections.actionGlobalSearchFragment()
                )
            }
        }

        drawerInterface.drawerBehaviorCallback.apply {
            addOnSlideAction(HalfClockwiseRotateSlideAction(binding.bottomAppBarChevron))
            addOnStateChangedAction(ShowHideFabStateAction(binding.fab))
        }

        // Set up the BottomNavigationDrawer's open/close affordance
        binding.bottomAppBarContentContainer.setOnClickListener {
            drawerInterface.toggleState()
        }
    }

    private fun hideBottomAppBar() {
        binding.run {
            bottomAppBar.performHide()
            // Get a handle on the animator that hides the bottom app bar so we can wait to hide
            // the fab and bottom app bar until after it's exit animation finishes
            bottomAppBar.animate().setListener(object : AnimatorListenerAdapter() {

                var isCanceled = false

                override fun onAnimationEnd(animation: Animator) {
                    if (isCanceled) return
                    // Hide the BottomAppBar to avoid it showing above the keyboard
                    bottomAppBar.visibility = View.GONE
                    fab.visibility = View.INVISIBLE
                }

                override fun onAnimationCancel(animation: Animator) {
                    isCanceled = true
                }
            })
        }
    }

    private fun setFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }
}
