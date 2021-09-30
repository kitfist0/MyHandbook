package my.handbook.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import my.handbook.data.db.entity.SearchResult
import my.handbook.data.repository.BaseParagraphRepository
import my.handbook.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: BaseParagraphRepository,
) : BaseViewModel() {

    private val _searchResults = MutableLiveData<List<SearchResult>>()
    val searchResults: LiveData<List<SearchResult>> = _searchResults

    fun onSearchRequestChanged(searchString: String) {
        viewModelScope.coroutineContext.cancelChildren()
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.set(true)
            delay(300L)
            val results = repository.getSearchResults(searchString)
            _searchResults.postValue(results)
            isLoading.set(false)
        }
    }

    fun onSearchResultClicked(file: String, text: String) {
        val searchResultText = text.replace("<b>", "").replace("</b>", "")
        navigateTo(SearchFragmentDirections.actionSearchFragmentToReadFragment(file, searchResultText))
    }
}
