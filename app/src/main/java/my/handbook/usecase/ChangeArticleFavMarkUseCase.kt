package my.handbook.usecase

import my.handbook.data.local.source.ArticleDao
import my.handbook.data.local.model.Article
import javax.inject.Inject

class ChangeArticleFavMarkUseCase @Inject constructor(
    private val articleDao: ArticleDao,
) {
    suspend fun execute(article: Article) {
        val updatedArticle = article.copy(favorite = !article.favorite)
        articleDao.update(updatedArticle)
    }
}
