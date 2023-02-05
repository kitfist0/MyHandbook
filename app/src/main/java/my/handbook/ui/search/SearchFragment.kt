package my.handbook.ui.search

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import my.handbook.R
import my.handbook.databinding.FragmentSearchBinding
import my.handbook.ui.base.BaseFragment

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(),
    SearchResultAdapter.SearchResultAdapterListener {

    override val layoutRes: Int = R.layout.fragment_search
    override val viewModel: SearchViewModel by viewModels()

    private val listAdapter = SearchResultAdapter(this)

    private val inputMethodManager by lazy(LazyThreadSafetyMode.NONE) {
        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun initViews() {
        with(binding) {
            searchToolbar.setNavigationOnClickListener {
                inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
                viewModel.onToolbarNavigationIconClicked()
            }
            searchEditText.requestFocus()
            searchEditText.doAfterTextChanged {
                viewModel.onSearchRequestChanged(it?.toString().orEmpty())
            }
            searchRecyclerView.adapter = listAdapter
            inputMethodManager.showSoftInput(searchEditText, 1)
        }
    }

    override fun observeData() {
        viewModel.searchResults.observe(viewLifecycleOwner) {
            listAdapter.submitList(it)
        }
    }

    override fun onSearchResultClicked(file: String, text: String) {
        viewModel.onSearchResultClicked(file, text)
    }
}
