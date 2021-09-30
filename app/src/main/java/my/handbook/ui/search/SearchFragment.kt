package my.handbook.ui.search

import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import my.handbook.R
import my.handbook.databinding.FragmentSearchBinding
import my.handbook.ui.base.BaseFragment

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(), SearchResultAdapter.SearchResultAdapterListener {

    override val layoutRes: Int = R.layout.fragment_search
    override val viewModel: SearchViewModel by viewModels()

    private val adapter = SearchResultAdapter(this)

    override fun initViews() {
        binding.searchToolbar.setNavigationOnClickListener { viewModel.navigateBack() }
        binding.searchEditText.doAfterTextChanged {
            viewModel.onSearchRequestChanged(it?.toString().orEmpty())
        }
        binding.searchRecyclerView.adapter = adapter
    }

    override fun observeData() {
        viewModel.searchResults.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onSearchResultClicked(file: String, text: String) {
        viewModel.onSearchResultClicked(file, text)
    }
}
