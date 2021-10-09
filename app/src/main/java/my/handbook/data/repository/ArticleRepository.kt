package my.handbook.data.repository

import my.handbook.data.db.dao.ArticleDao
import my.handbook.data.db.entity.Article
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticleRepository @Inject constructor(
    private val articleDao: ArticleDao,
) {

    suspend fun getArticlesWithSectionIds(sectionIds: List<Int>): List<Article> =
        articleDao.getArticlesWithSectionIds(sectionIds)

    suspend fun changeFavorite(article: Article) {
        article.favorite = article.favorite.not()
        articleDao.update(article)
    }
}
