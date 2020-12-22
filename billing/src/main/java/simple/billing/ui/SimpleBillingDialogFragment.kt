package simple.billing.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import simple.billing.data.db.Product
import simple.billing.databinding.FragmentBillingBinding

class SimpleBillingDialogFragment : BottomSheetDialogFragment(),
    SimpleProductAdapter.ProductAdapterListener {

    companion object {
        fun Fragment.showSimpleBillingDialog() = SimpleBillingDialogFragment()
            .show(childFragmentManager, SimpleBillingDialogFragment::class.simpleName)
    }

    // https://developer.android.com/topic/libraries/view-binding#fragments
    private var _binding: FragmentBillingBinding? = null
    private val binding get() = _binding!!

    private val viewModel by lazy { SimpleBillingViewModel(requireContext()) }
    private val adapter = SimpleProductAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBillingBinding.inflate(inflater, container, false)
            .apply { recyclerView.adapter = adapter }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.products.observe(viewLifecycleOwner) { adapter.submitList(it) }
    }

    override fun onProductClicked(product: Product) {
        activity?.let { viewModel.purchaseProduct(it, product.originalJson) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
