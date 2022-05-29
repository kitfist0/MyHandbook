package my.handbook.data

import androidx.room.Database
import androidx.room.RoomDatabase
import my.handbook.data.dao.ArticleDao
import my.handbook.data.dao.ParagraphDao
import my.handbook.data.dao.ProductDao
import my.handbook.data.dao.SectionDao
import my.handbook.data.entity.*

@Database(
    entities = [
        Article::class,
        Paragraph::class,
        ParagraphFts::class,
        Product::class,
        Section::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDao

    abstract fun paragraphDao(): ParagraphDao

    abstract fun productDao(): ProductDao

    abstract fun sectionDao(): SectionDao
}
