package my.handbook.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import my.handbook.common.hideKeyboard

abstract class BaseFragment<out DB : ViewDataBinding> : Fragment() {

    abstract val layoutRes: Int

    abstract val viewModel: BaseViewModel

    abstract fun initViews()

    abstract fun observeData()

    private var _binding: DB? = null

    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            viewModel.onBackPressed()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        binding.setVariable(BR.model, viewModel)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.event.collect { handleEvent(it) }
        }

        observeData()
    }

    override fun onPause() {
        super.onPause()
        view?.let { v -> activity?.hideKeyboard(v) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleEvent(event: Event) {
        when (event) {
            is TextMessage -> Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
            is Navigate -> findNavController().navigate(event.direction)
            is NavigateUp -> findNavController().navigateUp()
            is NavigateBack -> if (!findNavController().popBackStack()) activity?.finish()
        }
    }
}
