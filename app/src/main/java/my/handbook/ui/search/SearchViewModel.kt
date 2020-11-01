package my.handbook.ui.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import my.handbook.data.db.entity.SearchResult
import my.handbook.data.repository.BaseParagraphRepository

class SearchViewModel @ViewModelInject constructor(
    private val repository: BaseParagraphRepository
) : ViewModel() {

    val searchResults = MutableLiveData<List<SearchResult>>()

    fun onSearchRequestChanged(searchString: String) {
        viewModelScope.coroutineContext.cancelChildren()
        viewModelScope.launch(Dispatchers.IO) {
            delay(300L)
            val results = repository.getSearchResults(searchString)
            searchResults.postValue(results)
        }
    }
}
