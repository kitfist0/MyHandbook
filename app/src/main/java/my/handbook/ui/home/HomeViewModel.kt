package my.handbook.ui.home

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import my.handbook.data.db.entity.Article
import my.handbook.data.repository.ArticleRepository
import my.handbook.data.repository.SectionRepository
import my.handbook.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
    sectionRepository: SectionRepository,
) : BaseViewModel() {

    val articles: LiveData<List<Article>> = sectionRepository.getSelectedSectionIds()
        .onEach { isLoading.set(true) }
        .map { articleRepository.getArticlesWithSectionIds(it) }
        .onEach { isLoading.set(false) }
        .asLiveData()

    fun onArticleClicked(file: String) {
        navigateTo(HomeFragmentDirections.actionHomeFragmentToReadFragment(file))
    }

    fun onFavoriteChanged(article: Article) {
        viewModelScope.launch { articleRepository.changeFavorite(article) }
    }
}
