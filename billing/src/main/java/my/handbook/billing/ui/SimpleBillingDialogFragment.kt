package my.handbook.billing.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import my.handbook.billing.data.db.Product
import my.handbook.billing.databinding.FragmentBillingDialogBinding
import my.handbook.billing.util.billingViewModels

@AndroidEntryPoint
class SimpleBillingDialogFragment : BottomSheetDialogFragment(),
    SimpleProductAdapter.ProductAdapterListener {

    companion object {
        fun Fragment.showSimpleBillingDialog() = SimpleBillingDialogFragment()
            .show(childFragmentManager, SimpleBillingDialogFragment::class.simpleName)
    }

    private val viewModel: SimpleBillingViewModel by billingViewModels()
    private val adapter = SimpleProductAdapter(this)
    private lateinit var binding: FragmentBillingDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBillingDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.products.observe(viewLifecycleOwner) { adapter.submitList(it) }
    }

    override fun onProductClicked(product: Product) {
        activity?.let { viewModel.purchaseProduct(it, product.originalJson) }
    }
}
