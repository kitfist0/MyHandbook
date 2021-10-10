package my.handbook.ui.home

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import my.handbook.data.entity.Article
import my.handbook.ui.base.BaseViewModel
import my.handbook.usecase.ChangeArticleFavMarkUseCase
import my.handbook.usecase.GetArticlesUseCase
import my.handbook.usecase.UseCaseResult
import my.handbook.usecase.updateOnSuccess
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val changeArticleFavMarkUseCase: ChangeArticleFavMarkUseCase,
    getArticlesUseCase: GetArticlesUseCase,
) : BaseViewModel() {

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: LiveData<List<Article>> = _articles.asLiveData()

    init {
        getArticlesUseCase.execute().onEach { useCaseResult ->
            isLoading.set(useCaseResult is UseCaseResult.Loading)
            useCaseResult.updateOnSuccess(_articles)
        }.launchIn(viewModelScope)
    }

    fun onArticleClicked(file: String) {
        navigateTo(HomeFragmentDirections.actionHomeFragmentToReadFragment(file))
    }

    fun onFavoriteChanged(article: Article) {
        viewModelScope.launch { changeArticleFavMarkUseCase.execute(article) }
    }
}
