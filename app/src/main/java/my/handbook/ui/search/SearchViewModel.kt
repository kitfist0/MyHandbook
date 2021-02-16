package my.handbook.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import my.handbook.data.db.entity.SearchResult
import my.handbook.data.repository.BaseParagraphRepository
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
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
