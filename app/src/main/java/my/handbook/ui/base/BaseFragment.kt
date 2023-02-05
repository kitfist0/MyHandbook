package my.handbook.ui.base

import android.content.Intent
import android.net.Uri
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
            viewModel.baseEvents.collect { handleEvent(it) }
        }

        observeData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleEvent(event: BaseEvent) {
        when (event) {
            BaseEvent.NavigateBack ->
                if (!findNavController().popBackStack()) activity?.finish()
            is BaseEvent.NavigateTo ->
                findNavController().navigate(event.direction)
            is BaseEvent.OpenWebPage ->
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(event.address)))
            is BaseEvent.ShowTextMessage ->
                Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
        }
    }
}
