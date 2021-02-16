package my.handbook.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import my.handbook.data.db.dao.ParagraphDao
import my.handbook.data.repository.BaseParagraphRepository
import my.handbook.data.repository.ParagraphRepository
import my.handbook.data.repository.PorterStemmer
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchModule {

    @Provides
    @Singleton
    fun provideParagraphRepository(paragraphDao: ParagraphDao): BaseParagraphRepository {
        return ParagraphRepository(paragraphDao, PorterStemmer())
    }
}
