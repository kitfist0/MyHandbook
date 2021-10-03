package simple.billing.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import simple.billing.R
import simple.billing.data.db.Product

class SimpleBillingDialogFragment : BottomSheetDialogFragment(),
    SimpleProductAdapter.ProductAdapterListener {

    companion object {
        fun Fragment.showSimpleBillingDialog() = SimpleBillingDialogFragment()
            .show(childFragmentManager, SimpleBillingDialogFragment::class.simpleName)
    }

    private val viewModel by lazy { SimpleBillingViewModel(requireActivity().application) }
    private val adapter by lazy { SimpleProductAdapter(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_billing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.products.observe(viewLifecycleOwner) { adapter.submitList(it) }
    }

    override fun onProductClicked(product: Product) {
        activity?.let { viewModel.purchaseProduct(it, product.originalJson) }
    }
}
