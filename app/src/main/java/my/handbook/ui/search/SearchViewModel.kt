package my.handbook.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import my.handbook.data.local.model.SearchResult
import my.handbook.ui.base.BaseViewModel
import my.handbook.usecase.SearchParagraphUseCase
import my.handbook.usecase.UseCaseResult
import my.handbook.usecase.updateOnSuccess
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchParagraphUseCase: SearchParagraphUseCase,
) : BaseViewModel() {

    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: LiveData<List<SearchResult>> = _searchResults.asLiveData()

    fun onSearchRequestChanged(searchQuery: String) {
        viewModelScope.coroutineContext.cancelChildren()
        searchParagraphUseCase.execute(searchQuery).onEach { useCaseResult ->
            isLoading.set(useCaseResult is UseCaseResult.Loading)
            useCaseResult.updateOnSuccess(_searchResults)
        }.launchIn(viewModelScope)
    }

    fun onToolbarNavigationIconClicked() {
        navigateBack()
    }

    fun onSearchResultClicked(file: String, text: String) {
        val resultText = text.replace("<b>", "").replace("</b>", "")
        navigateTo(SearchFragmentDirections.fromSearchToRead(file, resultText))
    }
}
