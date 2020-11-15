package my.handbook.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import my.handbook.data.db.dao.ArticleDao
import my.handbook.data.db.dao.ParagraphDao
import my.handbook.data.db.dao.SectionDao
import my.handbook.data.db.entity.Article
import my.handbook.data.db.entity.Paragraph
import my.handbook.data.db.entity.ParagraphFts
import my.handbook.data.db.entity.Section

@Database(
    entities = [
        Article::class,
        Paragraph::class,
        ParagraphFts::class,
        Section::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDao

    abstract fun paragraphDao(): ParagraphDao

    abstract fun sectionDao(): SectionDao
}
