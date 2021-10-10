package my.handbook.usecase

import kotlinx.coroutines.flow.*
import my.handbook.data.dao.ArticleDao
import my.handbook.data.dao.SectionDao
import my.handbook.data.entity.Article
import javax.inject.Inject

class GetArticlesUseCase @Inject constructor(
    private val articleDao: ArticleDao,
    private val sectionDao: SectionDao,
) {
    fun execute(): Flow<UseCaseResult<List<Article>>> = sectionDao.getSelectedSectionIds()
        .transform { sectionIds ->
            emit(UseCaseResult.Loading)
            val articles = articleDao.getArticlesWithSectionIds(sectionIds)
            emit(UseCaseResult.Success(articles))
        }
}
