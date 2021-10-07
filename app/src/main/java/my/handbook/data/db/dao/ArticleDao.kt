package my.handbook.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import my.handbook.data.db.entity.Article

@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles")
    suspend fun getArticles(): List<Article>

    @Query("SELECT * FROM articles WHERE section IN (:selectedSections)")
    suspend fun getArticles(selectedSections: List<Int>): List<Article>

    @Update
    suspend fun update(article: Article)
}
