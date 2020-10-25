package my.handbook.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import my.handbook.databinding.FragmentSearchBinding
import my.handbook.util.hideKeyboard

@AndroidEntryPoint
class SearchFragment : Fragment(), SearchResultAdapter.SearchResultAdapterListener {

    private val viewModel: SearchViewModel by viewModels()
    private val adapter = SearchResultAdapter(this)
    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.recyclerView.adapter = adapter
        binding.searchToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.searchEditText.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                }
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    viewModel.onSearchRequestChanged(s.toString())
                }
            }
        )
        viewModel.searchResults.observe(viewLifecycleOwner) {
            Log.d("SearchFragment", "searchResults: $it")
            adapter.submitList(it)
        }
    }

    override fun onSearchResultClicked(cardView: View, file: String, text: String) {
        findNavController().navigate(
            SearchFragmentDirections.actionSearchFragmentToReadFragment(file, text)
        )
    }

    override fun onPause() {
        super.onPause()
        view?.let { activity?.hideKeyboard(it) }
    }
}
