package my.handbook.di

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.android.billingclient.api.BillingClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import my.handbook.data.AppDatabase
import my.handbook.data.dao.ArticleDao
import my.handbook.data.dao.ParagraphDao
import my.handbook.data.dao.SectionDao
import my.handbook.util.dbRecreationRequired
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePreferences(app: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(app)
    }

    @Provides
    @Singleton
    fun provideDatabase(app: Application, preferences: SharedPreferences): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, "app_handbook.db").apply {
            val dbFileName = app.assets.list("database")
                ?.let { list ->
                    if (list.size != 1) {
                        throw RuntimeException("Database folder is empty or contains more than 1 file")
                    }
                    list[0]
                }
                ?: throw RuntimeException("Database folder does not exist")
            if (preferences.dbRecreationRequired(dbFileName)) {
                fallbackToDestructiveMigration()
            }
            createFromAsset("database/$dbFileName")
            addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // do something after database has been created
                    db.execSQL("INSERT INTO paragraphs_fts(paragraphs_fts) VALUES('rebuild')")
                }
            })
        }.build()
    }

    @Provides
    @Singleton
    fun provideArticleDao(db: AppDatabase): ArticleDao {
        return db.articleDao()
    }

    @Provides
    @Singleton
    fun provideParagraphDao(db: AppDatabase): ParagraphDao {
        return db.paragraphDao()
    }

    @Provides
    @Singleton
    fun provideSectionDao(db: AppDatabase): SectionDao {
        return db.sectionDao()
    }

    @Provides
    fun provideBillingClientBuilder(app: Application): BillingClient.Builder {
        return BillingClient.newBuilder(app)
    }
}
