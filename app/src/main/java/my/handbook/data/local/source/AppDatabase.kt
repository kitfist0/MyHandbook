package my.handbook.data.local.source

import androidx.room.Database
import androidx.room.RoomDatabase
import my.handbook.data.local.source.ArticleDao
import my.handbook.data.local.source.ParagraphDao
import my.handbook.data.local.source.SectionDao
import my.handbook.data.local.model.*

@Database(
    entities = [
        Article::class,
        Paragraph::class,
        ParagraphFts::class,
        Section::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun paragraphDao(): ParagraphDao
    abstract fun sectionDao(): SectionDao
}
