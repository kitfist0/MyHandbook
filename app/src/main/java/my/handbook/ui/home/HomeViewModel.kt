package my.handbook.ui.home

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import my.handbook.data.db.entity.Article
import my.handbook.data.repository.ArticleRepository
import my.handbook.data.repository.SectionRepository
import my.handbook.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
    private val sectionRepository: SectionRepository,
) : BaseViewModel() {

    private val selectedSections = liveData {
        sectionRepository.getSelectedSections().collect { emit(it) }
    }

    val articles = selectedSections.switchMap { sections ->
        liveData { emit(articleRepository.getArticles(sections)) }
    }

    fun onArticleClicked(file: String) {
        navigateTo(HomeFragmentDirections.actionHomeFragmentToReadFragment(file))
    }

    fun onFavoriteChanged(article: Article) {
        viewModelScope.launch { articleRepository.changeFavorite(article) }
    }
}
