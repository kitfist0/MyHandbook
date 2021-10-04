package simple.billing.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
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
        val view = inflater.inflate(R.layout.fragment_billing, container, false)
        view.findViewById<RecyclerView>(R.id.products_recycler_view).adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.products.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.errors.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    }

    override fun onProductClicked(product: Product) {
        viewModel.onProductClicked(activity, product)
    }
}
