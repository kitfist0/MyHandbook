package my.handbook.di

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import my.handbook.BuildConfig
import my.handbook.data.db.AppDatabase
import my.handbook.data.db.dao.ArticleDao
import my.handbook.data.db.dao.ParagraphDao
import my.handbook.util.dbRecreationRequired
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePreferences(app: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(app)
    }

    @Provides
    @Singleton
    fun provideDatabase(app: Application, preferences: SharedPreferences): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, BuildConfig.APP_DB_NAME).apply {
            if (preferences.dbRecreationRequired()) {
                fallbackToDestructiveMigration()
            }
            createFromAsset("database/${BuildConfig.ACTUAL_DB_FILE_NAME}")
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
}
