package my.handbook.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import my.handbook.data.local.model.Article
import my.handbook.ui.base.BaseViewModel
import my.handbook.usecase.ChangeArticleFavMarkUseCase
import my.handbook.usecase.GetArticlesOfSelectedSectionsUseCase
import my.handbook.usecase.UseCaseResult
import my.handbook.usecase.updateOnSuccess
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val changeArticleFavMarkUseCase: ChangeArticleFavMarkUseCase,
    getArticlesOfSelectedSectionsUseCase: GetArticlesOfSelectedSectionsUseCase,
) : BaseViewModel() {

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: LiveData<List<Article>> = _articles
        .onEach { list -> isEmpty.set(list.isEmpty()) }
        .asLiveData()

    init {
        getArticlesOfSelectedSectionsUseCase.execute()
            .onEach { useCaseResult ->
                isLoading.set(useCaseResult is UseCaseResult.Loading)
                useCaseResult.updateOnSuccess(_articles)
            }
            .launchIn(viewModelScope)
    }

    fun onArticleClicked(file: String) {
        navigateTo(HomeFragmentDirections.fromHomeToRead(file))
    }

    fun onFavoriteChanged(article: Article) {
        viewModelScope.launch { changeArticleFavMarkUseCase.execute(article) }
    }
}
