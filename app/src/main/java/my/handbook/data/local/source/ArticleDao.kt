package my.handbook.data.local.source

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import my.handbook.data.local.model.Article

@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles")
    fun getArticles(): Flow<List<Article>>

    @Update
    suspend fun update(article: Article)
}
