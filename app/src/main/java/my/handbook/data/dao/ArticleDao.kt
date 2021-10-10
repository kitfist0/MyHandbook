package my.handbook.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import my.handbook.data.entity.Article

@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles")
    fun getArticles(): Flow<List<Article>>

    @Query("SELECT * FROM articles WHERE section IN (:sectionIds)")
    suspend fun getArticlesWithSectionIds(sectionIds: List<Int>): List<Article>

    @Update
    suspend fun update(article: Article)
}
