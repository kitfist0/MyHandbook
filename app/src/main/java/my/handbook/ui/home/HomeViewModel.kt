package my.handbook.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import my.handbook.data.db.entity.Article
import my.handbook.data.repository.ArticleRepository

class HomeViewModel @ViewModelInject constructor(
    private val repository: ArticleRepository
) : ViewModel() {

    val articles = liveData(Dispatchers.IO) { emit(repository.getArticles()) }

    fun onFavoriteClicked(article: Article) =
        viewModelScope.launch(Dispatchers.IO) { repository.changeFavorite(article) }
}
