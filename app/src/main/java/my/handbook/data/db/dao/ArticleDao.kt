package my.handbook.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import my.handbook.data.db.entity.Article

@Dao
interface ArticleDao {

    @Insert
    fun insertArticles(articles: List<Article>)

    @Insert
    fun insertArticle(article: Article)

    @Query("SELECT * FROM articles")
    suspend fun getArticles(): List<Article>

    @Query("SELECT * FROM articles WHERE id = :id")
    fun getArticleWithId(id: Long): Article

    @Update
    suspend fun update(article: Article)
}