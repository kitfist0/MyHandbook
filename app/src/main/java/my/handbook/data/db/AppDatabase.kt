package my.handbook.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import my.handbook.data.db.dao.ArticleDao
import my.handbook.data.db.dao.ParagraphDao
import my.handbook.data.db.entity.Article
import my.handbook.data.db.entity.Paragraph
import my.handbook.data.db.entity.ParagraphFts

@Database(
    entities = [
        Article::class,
        Paragraph::class,
        ParagraphFts::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDao

    abstract fun paragraphDao(): ParagraphDao
}
