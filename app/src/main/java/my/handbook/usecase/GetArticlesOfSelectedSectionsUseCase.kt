package my.handbook.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import my.handbook.data.local.source.ArticleDao
import my.handbook.data.local.source.SectionDao
import my.handbook.data.local.model.Article
import javax.inject.Inject

class GetArticlesOfSelectedSectionsUseCase @Inject constructor(
    private val articleDao: ArticleDao,
    private val sectionDao: SectionDao,
) {
    fun execute(): Flow<UseCaseResult<List<Article>>> = articleDao.getArticles()
        .onStart { UseCaseResult.Loading }
        .combine(sectionDao.getSelectedSectionIds()) { articles, sectionIds ->
            val filteredArticles = articles.filter { article -> sectionIds.contains(article.section) }
            UseCaseResult.Success(filteredArticles)
        }
}
